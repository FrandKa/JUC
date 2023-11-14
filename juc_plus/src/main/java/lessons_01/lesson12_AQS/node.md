node.md
```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {

    private static final long serialVersionUID = 7373984972572414691L;

    /**
     * Creates a new {@code AbstractQueuedSynchronizer} instance
     * with initial synchronization state of zero.
     */
    protected AbstractQueuedSynchronizer() { }
    // Node status bits, also used as argument and return values
    static final int WAITING   = 1;          // must be 1
    static final int CANCELLED = 0x80000000; // must be negative
    static final int COND      = 2;          // in a condition wait

    /** CLH Nodes */
    
    abstract static class Node {
        volatile Node prev;       // initially attached via casTail
        volatile Node next;       // visibly nonnull when signallable
        Thread waiter;            // visibly nonnull when enqueued
        
        // 判断是否阻塞
        volatile int status;      // written by owner, atomic bit ops by others

        // methods for atomic operations
        final boolean casPrev(Node c, Node v) {  // for cleanQueue
            return U.weakCompareAndSetReference(this, PREV, c, v);
        }
        final boolean casNext(Node c, Node v) {  // for cleanQueue
            return U.weakCompareAndSetReference(this, NEXT, c, v);
        }
        final int getAndUnsetStatus(int v) {     // for signalling
            return U.getAndBitwiseAndInt(this, STATUS, ~v); // ~ 补码操作在Java中，~ 是一元按位补码操作符，它会对二进制表示的每一个位执行取反操作。
        }
        final void setPrevRelaxed(Node p) {      // for off-queue assignment
            U.putReference(this, PREV, p);
        }
        final void setStatusRelaxed(int s) {     // for off-queue assignment
            U.putInt(this, STATUS, s);
        }
        final void clearStatus() {               // for reducing unneeded signals
            U.putIntOpaque(this, STATUS, 0);
        }

        private static final long STATUS
            = U.objectFieldOffset(Node.class, "status");
        private static final long NEXT
            = U.objectFieldOffset(Node.class, "next");
        private static final long PREV
            = U.objectFieldOffset(Node.class, "prev");
    }

    // Concrete classes tagged by type
    static final class ExclusiveNode extends Node { }
    static final class SharedNode extends Node { }

    static final class ConditionNode extends Node
        implements ForkJoinPool.ManagedBlocker {
        ConditionNode nextWaiter;            // link to next waiting node

        /**
         * Allows Conditions to be used in ForkJoinPools without
         * risking fixed pool exhaustion. This is usable only for
         * untimed Condition waits, not timed versions.
         */
        public final boolean isReleasable() {
            return status <= 1 || Thread.currentThread().isInterrupted();
        }

        public final boolean block() {
            while (!isReleasable()) LockSupport.park();
            return true;
        }
    }

    /**
     * Head of the wait queue, lazily initialized.
     */
    private transient volatile Node head;

    /**
     * Tail of the wait queue. After initialization, modified only via casTail.
     */
    private transient volatile Node tail;

    /**
     * The synchronization state.
     */
    private volatile int state; // 状态位 0: 空闲 || >= 1: 被占用

    /**
     * Returns the current value of synchronization state.
     * This operation has memory semantics of a {@code volatile} read.
     * @return current state value
     */
    protected final int getState() {
        return state;
    }

    protected final void setState(int newState) {
        state = newState;
    }

    protected final boolean compareAndSetState(int expect, int update) {
        return U.compareAndSetInt(this, STATE, expect, update);
    }

    // Queuing utilities

    private boolean casTail(Node c, Node v) {
        return U.compareAndSetReference(this, TAIL, c, v);
    }

    /**
     * Tries to CAS a new dummy node for head.
     * Returns new tail, or null if OutOfMemory
     */
    private Node tryInitializeHead() {
        for (Node h = null, t;;) {
            if ((t = tail) != null)
                return t;
            else if (head != null)
                Thread.onSpinWait();
            else {
                if (h == null) {
                    try {
                        h = new ExclusiveNode();
                    } catch (OutOfMemoryError oome) {
                        return null;
                    }
                }
                if (U.compareAndSetReference(this, HEAD, null, h))
                    return tail = h;
            }
        }
    }

    /**
     * Enqueues the node unless null. (Currently used only for
     * ConditionNodes; other cases are interleaved with acquires.)
     */
    final void enqueue(ConditionNode node) {
        if (node != null) {
            boolean unpark = false;
            for (Node t;;) {
                if ((t = tail) == null && (t = tryInitializeHead()) == null) {
                    unpark = true;             // wake up to spin on OOME
                    break;
                }
                node.setPrevRelaxed(t);        // avoid unnecessary fence
                if (casTail(t, node)) {
                    t.next = node;
                    if (t.status < 0)          // wake up to clean link
                        unpark = true;
                    break;
                }
            }
            if (unpark)
                LockSupport.unpark(node.waiter);
        }
    }

    /** Returns true if node is found in traversal from tail */
    final boolean isEnqueued(Node node) {
        for (Node t = tail; t != null; t = t.prev)
            if (t == node)
                return true;
        return false;
    }

    /**
     * Wakes up the successor of given node, if one exists, and unsets its
     * WAITING status to avoid park race. This may fail to wake up an
     * eligible thread when one or more have been cancelled, but
     * cancelAcquire ensures liveness.
     */
    private static void signalNext(Node h) {
        Node s;
        if (h != null && (s = h.next) != null && s.status != 0) {
            s.getAndUnsetStatus(WAITING);
            LockSupport.unpark(s.waiter);
        }
    }

    /** Wakes up the given node if in shared mode */
    private static void signalNextIfShared(Node h) {
        Node s;
        if (h != null && (s = h.next) != null &&
            (s instanceof SharedNode) && s.status != 0) {
            s.getAndUnsetStatus(WAITING);
            LockSupport.unpark(s.waiter);
        }
    }

    final int acquire(Node node, int arg, boolean shared,
                      boolean interruptible, boolean timed, long time) {
        Thread current = Thread.currentThread();
        byte spins = 0, postSpins = 0;   // retries upon unpark of first thread
        boolean interrupted = false, first = false;
        Node pred = null;               // predecessor of node when enqueued
        
        for (;;) {
            if (!first && (pred = (node == null) ? null : node.prev) != null &&
                !(first = (head == pred))) {
                if (pred.status < 0) {
                    cleanQueue();           // predecessor cancelled
                    continue;
                } else if (pred.prev == null) {
                    Thread.onSpinWait();    // ensure serialization
                    continue;
                }
            }
            if (first || pred == null) {
                boolean acquired;
                try {
                    if (shared)
                        acquired = (tryAcquireShared(arg) >= 0);
                    else
                        acquired = tryAcquire(arg);
                } catch (Throwable ex) {
                    cancelAcquire(node, interrupted, false);
                    throw ex;
                }
                if (acquired) {
                    if (first) {
                        node.prev = null;
                        head = node;
                        pred.next = null;
                        node.waiter = null;
                        if (shared)
                            signalNextIfShared(node);
                        if (interrupted)
                            current.interrupt();
                    }
                    return 1;
                }
            }
            Node t;
            if ((t = tail) == null) {           // initialize queue
                if (tryInitializeHead() == null)
                    return acquireOnOOME(shared, arg);
            } else if (node == null) {          // allocate; retry before enqueue
                try {
                    node = (shared) ? new SharedNode() : new ExclusiveNode();
                } catch (OutOfMemoryError oome) {
                    return acquireOnOOME(shared, arg);
                }
            } else if (pred == null) {          // try to enqueue
                node.waiter = current;
                node.setPrevRelaxed(t);         // avoid unnecessary fence
                if (!casTail(t, node))
                    node.setPrevRelaxed(null);  // back out
                else
                    t.next = node;
            } else if (first && spins != 0) {
                --spins;                        // reduce unfairness on rewaits
                Thread.onSpinWait();
            } else if (node.status == 0) {
                node.status = WAITING;          // enable signal and recheck
            } else {
                long nanos;
                spins = postSpins = (byte)((postSpins << 1) | 1);
                if (!timed)
                    LockSupport.park(this);
                else if ((nanos = time - System.nanoTime()) > 0L)
                    LockSupport.parkNanos(this, nanos);
                else
                    break;
                node.clearStatus();
                if ((interrupted |= Thread.interrupted()) && interruptible)
                    break;
            }
        }
        return cancelAcquire(node, interrupted, interruptible);
    }

    /**
     * Spin-waits with backoff; used only upon OOME failures during acquire.
     */
    private int acquireOnOOME(boolean shared, int arg) {
        for (long nanos = 1L;;) {
            if (shared ? (tryAcquireShared(arg) >= 0) : tryAcquire(arg))
                return 1;
            U.park(false, nanos);               // must use Unsafe park to sleep
            if (nanos < 1L << 30)               // max about 1 second
                nanos <<= 1;
        }
    }

    /**
     * Possibly repeatedly traverses from tail, unsplicing cancelled
     * nodes until none are found. Unparks nodes that may have been
     * relinked to be next eligible acquirer.
     */
    private void cleanQueue() {
        for (;;) {                               // restart point
            for (Node q = tail, s = null, p, n;;) { // (p, q, s) triples
                if (q == null || (p = q.prev) == null)
                    return;                      // end of list
                if (s == null ? tail != q : (s.prev != q || s.status < 0))
                    break;                       // inconsistent
                if (q.status < 0) {              // cancelled
                    if ((s == null ? casTail(q, p) : s.casPrev(q, p)) &&
                        q.prev == p) {
                        p.casNext(q, s);         // OK if fails
                        if (p.prev == null)
                            signalNext(p);
                    }
                    break;
                }
                if ((n = p.next) != q) {         // help finish
                    if (n != null && q.prev == p) {
                        p.casNext(n, q);
                        if (p.prev == null)
                            signalNext(p);
                    }
                    break;
                }
                s = q;
                q = q.prev;
            }
        }
    }

    private int cancelAcquire(Node node, boolean interrupted,
                              boolean interruptible) {
        if (node != null) {
            node.waiter = null;
            node.status = CANCELLED;
            if (node.prev != null)
                cleanQueue();
        }
        if (interrupted) {
            if (interruptible)
                return CANCELLED;
            else
                Thread.currentThread().interrupt();
        }
        return 0;
    }

    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    public final void acquire(int arg) {
        if (!tryAcquire(arg))
            acquire(null, arg, false, false, false, 0L);
    }

    public final void acquireInterruptibly(int arg)
        throws InterruptedException {
        if (Thread.interrupted() ||
            (!tryAcquire(arg) && acquire(null, arg, false, true, false, 0L) < 0))
            throw new InterruptedException();
    }

    public final boolean tryAcquireNanos(int arg, long nanosTimeout)
        throws InterruptedException {
        if (!Thread.interrupted()) {
            if (tryAcquire(arg))
                return true;
            if (nanosTimeout <= 0L)
                return false;
            int stat = acquire(null, arg, false, true, true,
                               System.nanoTime() + nanosTimeout);
            if (stat > 0)
                return true;
            if (stat == 0)
                return false;
        }
        throw new InterruptedException();
    }

    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            signalNext(head);
            return true;
        }
        return false;
    }

    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0)
            acquire(null, arg, true, false, false, 0L);
    }

    public final void acquireSharedInterruptibly(int arg)
        throws InterruptedException {
        if (Thread.interrupted() ||
            (tryAcquireShared(arg) < 0 &&
             acquire(null, arg, true, true, false, 0L) < 0))
            throw new InterruptedException();
    }

    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout)
            throws InterruptedException {
        if (!Thread.interrupted()) {
            if (tryAcquireShared(arg) >= 0)
                return true;
            if (nanosTimeout <= 0L)
                return false;
            int stat = acquire(null, arg, true, true, true,
                               System.nanoTime() + nanosTimeout);
            if (stat > 0)
                return true;
            if (stat == 0)
                return false;
        }
        throw new InterruptedException();
    }

    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            signalNext(head);
            return true;
        }
        return false;
    }

    public final boolean hasQueuedThreads() {
        for (Node p = tail, h = head; p != h && p != null; p = p.prev)
            if (p.status >= 0)
                return true;
        return false;
    }

    public final boolean hasContended() {
        return head != null;
    }

    public final Thread getFirstQueuedThread() {
        Thread first = null, w; Node h, s;
        if ((h = head) != null && ((s = h.next) == null ||
                                   (first = s.waiter) == null ||
                                   s.prev == null)) {
            // traverse from tail on stale reads
            for (Node p = tail, q; p != null && (q = p.prev) != null; p = q)
                if ((w = p.waiter) != null)
                    first = w;
        }
        return first;
    }

    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.waiter == thread)
                return true;
        return false;
    }

    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return (h = head) != null && (s = h.next)  != null &&
            !(s instanceof SharedNode) && s.waiter != null;
    }

    public final boolean hasQueuedPredecessors() {
        Thread first = null; Node h, s;
        if ((h = head) != null && ((s = h.next) == null ||
                                   (first = s.waiter) == null ||
                                   s.prev == null))
            first = getFirstQueuedThread(); // retry via getFirstQueuedThread
        return first != null && first != Thread.currentThread();
    }
    
    public final int getQueueLength() {
        int n = 0;
        for (Node p = tail; p != null; p = p.prev) {
            if (p.waiter != null)
                ++n;
        }
        return n;
    }

    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.waiter;
            if (t != null)
                list.add(t);
        }
        return list;
    }

    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!(p instanceof SharedNode)) {
                Thread t = p.waiter;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<>();
        for (Node p = tail; p != null; p = p.prev) {
            if (p instanceof SharedNode) {
                Thread t = p.waiter;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }
    
    public String toString() {
        return super.toString()
            + "[State = " + getState() + ", "
            + (hasQueuedThreads() ? "non" : "") + "empty queue]";
    }

    public final boolean owns(ConditionObject condition) {
        return condition.isOwnedBy(this);
    }
    
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }
    
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }
    
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }
    
    public class ConditionObject implements Condition, java.io.Serializable {
        private static final long serialVersionUID = 1173984872572414699L;
        /** First node of condition queue. */
        private transient ConditionNode firstWaiter;
        /** Last node of condition queue. */
        private transient ConditionNode lastWaiter;

        /**
         * Fixed delay in nanoseconds between releasing and reacquiring
         * lock during Condition waits that encounter OutOfMemoryErrors
         */
        static final long OOME_COND_WAIT_DELAY = 10L * 1000L * 1000L; // 10 ms

        /**
         * Creates a new {@code ConditionObject} instance.
         */
        public ConditionObject() { }

        // Signalling methods

        /**
         * Removes and transfers one or all waiters to sync queue.
         */
        private void doSignal(ConditionNode first, boolean all) {
            while (first != null) {
                ConditionNode next = first.nextWaiter;
                if ((firstWaiter = next) == null)
                    lastWaiter = null;
                if ((first.getAndUnsetStatus(COND) & COND) != 0) {
                    enqueue(first);
                    if (!all)
                        break;
                }
                first = next;
            }
        }

        /**
         * Moves the longest-waiting thread, if one exists, from the
         * wait queue for this condition to the wait queue for the
         * owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        public final void signal() {
            ConditionNode first = firstWaiter;
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            else if (first != null)
                doSignal(first, false);
        }

        /**
         * Moves all threads from the wait queue for this condition to
         * the wait queue for the owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively}
         *         returns {@code false}
         */
        public final void signalAll() {
            ConditionNode first = firstWaiter;
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            else if (first != null)
                doSignal(first, true);
        }

        // Waiting methods

        /**
         * Adds node to condition list and releases lock.
         *
         * @param node the node
         * @return savedState to reacquire after wait
         */
        private int enableWait(ConditionNode node) {
            if (isHeldExclusively()) {
                node.waiter = Thread.currentThread();
                node.setStatusRelaxed(COND | WAITING);
                ConditionNode last = lastWaiter;
                if (last == null)
                    firstWaiter = node;
                else
                    last.nextWaiter = node;
                lastWaiter = node;
                int savedState = getState();
                if (release(savedState))
                    return savedState;
            }
            node.status = CANCELLED; // lock not held or inconsistent
            throw new IllegalMonitorStateException();
        }

        /**
         * Returns true if a node that was initially placed on a condition
         * queue is now ready to reacquire on sync queue.
         * @param node the node
         * @return true if is reacquiring
         */
        private boolean canReacquire(ConditionNode node) {
            // check links, not status to avoid enqueue race
            Node p; // traverse unless known to be bidirectionally linked
            return node != null && (p = node.prev) != null &&
                (p.next == node || isEnqueued(node));
        }

        /**
         * Unlinks the given node and other non-waiting nodes from
         * condition queue unless already unlinked.
         */
        private void unlinkCancelledWaiters(ConditionNode node) {
            if (node == null || node.nextWaiter != null || node == lastWaiter) {
                ConditionNode w = firstWaiter, trail = null;
                while (w != null) {
                    ConditionNode next = w.nextWaiter;
                    if ((w.status & COND) == 0) {
                        w.nextWaiter = null;
                        if (trail == null)
                            firstWaiter = next;
                        else
                            trail.nextWaiter = next;
                        if (next == null)
                            lastWaiter = trail;
                    } else
                        trail = w;
                    w = next;
                }
            }
        }

        /**
         * Constructs objects needed for condition wait. On OOME,
         * releases lock, sleeps, reacquires, and returns null.
         */
        private ConditionNode newConditionNode() {
            int savedState;
            if (tryInitializeHead() != null) {
                try {
                    return new ConditionNode();
                } catch (OutOfMemoryError oome) {
                }
            }
            // fall through if encountered OutOfMemoryError
            if (!isHeldExclusively() || !release(savedState = getState()))
                throw new IllegalMonitorStateException();
            U.park(false, OOME_COND_WAIT_DELAY);
            acquireOnOOME(false, savedState);
            return null;
        }

        public final void awaitUninterruptibly() {
            ConditionNode node = newConditionNode();
            if (node == null)
                return;
            int savedState = enableWait(node);
            LockSupport.setCurrentBlocker(this); // for back-compatibility
            boolean interrupted = false, rejected = false;
            while (!canReacquire(node)) {
                if (Thread.interrupted())
                    interrupted = true;
                else if ((node.status & COND) != 0) {
                    try {
                        if (rejected)
                            node.block();
                        else
                            ForkJoinPool.managedBlock(node);
                    } catch (RejectedExecutionException ex) {
                        rejected = true;
                    } catch (InterruptedException ie) {
                        interrupted = true;
                    }
                } else
                    Thread.onSpinWait();    // awoke while enqueuing
            }
            LockSupport.setCurrentBlocker(null);
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (interrupted)
                Thread.currentThread().interrupt();
        }

        public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = newConditionNode();
            if (node == null)
                return;
            int savedState = enableWait(node);
            LockSupport.setCurrentBlocker(this); // for back-compatibility
            boolean interrupted = false, cancelled = false, rejected = false;
            while (!canReacquire(node)) {
                if (interrupted |= Thread.interrupted()) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;              // else interrupted after signal
                } else if ((node.status & COND) != 0) {
                    try {
                        if (rejected)
                            node.block();
                        else
                            ForkJoinPool.managedBlock(node);
                    } catch (RejectedExecutionException ex) {
                        rejected = true;
                    } catch (InterruptedException ie) {
                        interrupted = true;
                    }
                } else
                    Thread.onSpinWait();    // awoke while enqueuing
            }
            LockSupport.setCurrentBlocker(null);
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (interrupted) {
                if (cancelled) {
                    unlinkCancelledWaiters(node);
                    throw new InterruptedException();
                }
                Thread.currentThread().interrupt();
            }
        }

        public final long awaitNanos(long nanosTimeout)
                throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = newConditionNode();
            if (node == null)
                return nanosTimeout - OOME_COND_WAIT_DELAY;
            int savedState = enableWait(node);
            long nanos = (nanosTimeout < 0L) ? 0L : nanosTimeout;
            long deadline = System.nanoTime() + nanos;
            boolean cancelled = false, interrupted = false;
            while (!canReacquire(node)) {
                if ((interrupted |= Thread.interrupted()) ||
                    (nanos = deadline - System.nanoTime()) <= 0L) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;
                } else
                    LockSupport.parkNanos(this, nanos);
            }
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (cancelled) {
                unlinkCancelledWaiters(node);
                if (interrupted)
                    throw new InterruptedException();
            } else if (interrupted)
                Thread.currentThread().interrupt();
            long remaining = deadline - System.nanoTime(); // avoid overflow
            return (remaining <= nanosTimeout) ? remaining : Long.MIN_VALUE;
        }

        public final boolean awaitUntil(Date deadline)
                throws InterruptedException {
            long abstime = deadline.getTime();
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = newConditionNode();
            if (node == null)
                return false;
            int savedState = enableWait(node);
            boolean cancelled = false, interrupted = false;
            while (!canReacquire(node)) {
                if ((interrupted |= Thread.interrupted()) ||
                    System.currentTimeMillis() >= abstime) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;
                } else
                    LockSupport.parkUntil(this, abstime);
            }
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (cancelled) {
                unlinkCancelledWaiters(node);
                if (interrupted)
                    throw new InterruptedException();
            } else if (interrupted)
                Thread.currentThread().interrupt();
            return !cancelled;
        }
        
        public final boolean await(long time, TimeUnit unit)
                throws InterruptedException {
            long nanosTimeout = unit.toNanos(time);
            if (Thread.interrupted())
                throw new InterruptedException();
            ConditionNode node = newConditionNode();
            if (node == null)
                return false;
            int savedState = enableWait(node);
            long nanos = (nanosTimeout < 0L) ? 0L : nanosTimeout;
            long deadline = System.nanoTime() + nanos;
            boolean cancelled = false, interrupted = false;
            while (!canReacquire(node)) {
                if ((interrupted |= Thread.interrupted()) ||
                    (nanos = deadline - System.nanoTime()) <= 0L) {
                    if (cancelled = (node.getAndUnsetStatus(COND) & COND) != 0)
                        break;
                } else
                    LockSupport.parkNanos(this, nanos);
            }
            node.clearStatus();
            acquire(node, savedState, false, false, false, 0L);
            if (cancelled) {
                unlinkCancelledWaiters(node);
                if (interrupted)
                    throw new InterruptedException();
            } else if (interrupted)
                Thread.currentThread().interrupt();
            return !cancelled;
        }
        
        final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
            return sync == AbstractQueuedSynchronizer.this;
        }
        
        protected final boolean hasWaiters() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                if ((w.status & COND) != 0)
                    return true;
            }
            return false;
        }
        
        protected final int getWaitQueueLength() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int n = 0;
            for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                if ((w.status & COND) != 0)
                    ++n;
            }
            return n;
        }
        
        protected final Collection<Thread> getWaitingThreads() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            ArrayList<Thread> list = new ArrayList<>();
            for (ConditionNode w = firstWaiter; w != null; w = w.nextWaiter) {
                if ((w.status & COND) != 0) {
                    Thread t = w.waiter;
                    if (t != null)
                        list.add(t);
                }
            }
            return list;
        }
    }

    // Unsafe
    private static final Unsafe U = Unsafe.getUnsafe();
    private static final long STATE
        = U.objectFieldOffset(AbstractQueuedSynchronizer.class, "state");
    private static final long HEAD
        = U.objectFieldOffset(AbstractQueuedSynchronizer.class, "head");
    private static final long TAIL
        = U.objectFieldOffset(AbstractQueuedSynchronizer.class, "tail");
    static {
        Class<?> ensureLoaded = LockSupport.class;
    }
}

```