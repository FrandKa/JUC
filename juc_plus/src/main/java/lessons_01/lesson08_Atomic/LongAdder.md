LongAdder.md
```java
/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.atomic;

import java.io.Serializable;
public class LongAdder extends Striped64 implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;

    /**
     * Creates a new adder with initial sum of zero.
     */
    public LongAdder() {
    }

    /**
     * Adds the given value.
     *
     * @param x the value to add
     */
    public void add(long x) {
        Cell[] cs; long b, v; int m; Cell c;
        // cs 表示cells引用, b base; v 期望值, m cells长度, c表示命中的单元格
        // cells一开始默认 == null || 对于base进行CAS成功就会跳出;
        // 慢慢的产生了竞争: casBase失败;
        if ((cs = cells) != null || !casBase(b = base, b + x)) {
            // 进入循环
            int index = getProbe(); // 得到一个槽位的地址
            boolean uncontended = true;
            // cs == null
            // cs == 2
            if (
                cs == null || // true // false
                (m = cs.length - 1) < 0 // m = 1 false
                            ||
                (c = cs[index & m]) == null // 槽位是不是空的 空的代表不需要扩容, 不是空的尝试进行cas操作
                            ||
                !(uncontended = c.cas(v = c.value, v + x))) // 进行cas操作, 如果失败了就需要进行扩容了; 
            {
                
                longAccumulate(x, null, uncontended, index);
            }
        }
    }

    // 扩容操作: 
    final void longAccumulate(long x, LongBinaryOperator fn,
                              boolean wasUncontended, int index) {
        /**
         * base: 
         */
        // 如果index == 0, 因为0表示随机数字没有被初始化, 强制初始化随机函数, 获得index
        if (index == 0) {
            ThreadLocalRandom.current(); // force initialization
            index = getProbe(); // 线程的hash值, 明确需要进入的槽位, 必须要有一个值
            wasUncontended = true; // 设置为没有竞争
        }
        // 进入一个无限循环 collide 变量表示上一个槽位是否非空，用于处理碰撞的情况。
        // 一开始设置为false表示上一个槽位不是空的
        for (boolean collide = false;;) {       // True if last slot nonempty
            Cell[] cs; Cell c; int n; long v;
            // cs cells数组 不为空, 长度n 大于零: 
            // 多个线程同时命中了怎么处理: 6个情况: 
            if ((cs = cells) != null && (n = cs.length) > 0) {
                /**
                 * 
                 */
                if ((c = cs[(n - 1) & index]) == null) {
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        // 双端检索
                        Cell r = new Cell(x);   // Optimistically create
                        if (cellsBusy == 0 && casCellsBusy()) { // 抢锁
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                if ((rs = cells) != null &&
                                        (m = rs.length) > 0 &&
                                        rs[j = (m - 1) & index] == null) {
                                    rs[j] = r; // 写入槽位
                                    break; // 跳出循环
                                }
                            } finally {
                                cellsBusy = 0; // 释放坑位
                            }
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                
                
                else if (!wasUncontended)       // CAS already known to fail, 产生了冲突
                    wasUncontended = true;      // Continue after rehash 继续操作
                
                // 没有冲突, 但是有index不为空
                // 写入值
                else if (c.cas(v = c.value,
                        (fn == null) ? v + x : fn.applyAsLong(v, x)))
                    break;
                
                // 如果已经扩容极限了
                else if (n >= NCPU || cells != cs)
                    collide = false;            // At max size or stale
                
                
                else if (!collide)
                    collide = true;
                
                // 扩容
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        if (cells == cs)        // Expand table unless stale
                            cells = Arrays.copyOf(cs, n << 1); // 扩容一倍
                    } finally {
                        cellsBusy = 0; // 释放锁
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                index = advanceProbe(index); // 前进
            }
            
            // cs为空或者长度为0;
            // 初始化cells数组,
            // cellsBusy: 0: 没有人占用, 1: 有其他的线程占用了, 
            // casCellsBusy() 设置状态为 1; 表示占用
            else if (cellsBusy == 0 && cells == cs && casCellsBusy()) {
                try {                           // Initialize table
                    // 新建槽位
                    if (cells == cs) {
                        Cell[] rs = new Cell[2]; // 分裂两个
                        rs[index & 1] = new Cell(x);  // 对于新建的一个槽位进行赋值 ;
                        cells = rs; // 设置cells
                        break;
                    }
                } finally {
                    cellsBusy = 0; // 设置为0, 释放锁
                }
            }
            
            
            // Fall back on using base, 找base; 
            // cs为空, 并且cells又被占用了, 回到base区域操作;
            else if (casBase(v = base,
                    // 如果没有设置操作函数, 设置为累加操作, v + x, 如果有的话, 使用自定义的操作
                    (fn == null) ? v + x : fn.applyAsLong(v, x)))
                break;
        }
    }

    /**
     * Equivalent to {@code add(1)}.
     */
    public void increment() {
        add(1L);
    }

    /**
     * Equivalent to {@code add(-1)}.
     */
    public void decrement() {
        add(-1L);
    }

    /**
     * Returns the current sum.  The returned value is <em>NOT</em> an
     * atomic snapshot; invocation in the absence of concurrent
     * updates returns an accurate result, but concurrent updates that
     * occur while the sum is being calculated might not be
     * incorporated.
     *
     * @return the sum
     */
    public long sum() {
        Cell[] cs = cells;
        long sum = base;
        if (cs != null) {
            for (Cell c : cs)
                if (c != null)
                    sum += c.value;
        }
        return sum;
    }

    /**
     * Resets variables maintaining the sum to zero.  This method may
     * be a useful alternative to creating a new adder, but is only
     * effective if there are no concurrent updates.  Because this
     * method is intrinsically racy, it should only be used when it is
     * known that no threads are concurrently updating.
     */
    public void reset() {
        Cell[] cs = cells;
        base = 0L;
        if (cs != null) {
            for (Cell c : cs)
                if (c != null)
                    c.reset();
        }
    }

    /**
     * Equivalent in effect to {@link #sum} followed by {@link
     * #reset}. This method may apply for example during quiescent
     * points between multithreaded computations.  If there are
     * updates concurrent with this method, the returned value is
     * <em>not</em> guaranteed to be the final value occurring before
     * the reset.
     *
     * @return the sum
     */
    public long sumThenReset() {
        Cell[] cs = cells;
        long sum = getAndSetBase(0L);
        if (cs != null) {
            for (Cell c : cs) {
                if (c != null)
                    sum += c.getAndSet(0L);
            }
        }
        return sum;
    }

    /**
     * Returns the String representation of the {@link #sum}.
     * @return the String representation of the {@link #sum}
     */
    public String toString() {
        return Long.toString(sum());
    }

    /**
     * Equivalent to {@link #sum}.
     *
     * @return the sum
     */
    public long longValue() {
        return sum();
    }

    /**
     * Returns the {@link #sum} as an {@code int} after a narrowing
     * primitive conversion.
     */
    public int intValue() {
        return (int)sum();
    }

    /**
     * Returns the {@link #sum} as a {@code float}
     * after a widening primitive conversion.
     */
    public float floatValue() {
        return (float)sum();
    }

    /**
     * Returns the {@link #sum} as a {@code double} after a widening
     * primitive conversion.
     */
    public double doubleValue() {
        return (double)sum();
    }

    /**
     * Serialization proxy, used to avoid reference to the non-public
     * Striped64 superclass in serialized forms.
     * @serial include
     */
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 7249069246863182397L;

        /**
         * The current value returned by sum().
         * @serial
         */
        private final long value;

        SerializationProxy(LongAdder a) {
            value = a.sum();
        }

        /**
         * Returns a {@code LongAdder} object with initial state
         * held by this proxy.
         *
         * @return a {@code LongAdder} object with initial state
         * held by this proxy
         */
        private Object readResolve() {
            LongAdder a = new LongAdder();
            a.base = value;
            return a;
        }
    }

    /**
     * Returns a
     * <a href="{@docRoot}/serialized-form.html#java.util.concurrent.atomic.LongAdder.SerializationProxy">
     * SerializationProxy</a>
     * representing the state of this instance.
     *
     * @return a {@link SerializationProxy}
     * representing the state of this instance
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * @param s the stream
     * @throws java.io.InvalidObjectException always
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.InvalidObjectException {
        throw new java.io.InvalidObjectException("Proxy required");
    }

}

```

这段代码是 `LongAdder` 类中的 `longAccumulate` 方法，用于处理累加的情况，包括初始化、调整大小、创建新的 `Cell` 对象以及处理竞争的情况。我会逐步解释这段代码：

1. **初始化和获取哈希值：**
```
if (index == 0) {
   ThreadLocalRandom.current(); // force initialization
   index = getProbe();
   wasUncontended = true;
}
```
如果 `index` 为0，它会强制进行初始化，获取当前线程的哈希值，并将 `wasUncontended` 设置为 `true`。这一步的目的是确保哈希值的存在，以及在后续的逻辑中能够正确处理竞争。

2. **循环处理：**
```
for (boolean collide = false;;) { // True if last slot nonempty
   // ... (后续代码)
}
```
这是一个无限循环，用于处理不同情况下的累加操作。`collide` 变量表示上一个槽位是否非空，用于处理碰撞的情况。

3. **处理 `Cell` 数组非空的情况：**
```
Cell[] cs; Cell c; int n; long v;
if ((cs = cells) != null && (n = cs.length) > 0) {
   // ... (后续代码)
}
```
如果 `Cell` 数组不为空，且长度大于0，表示有多个 `Cell` 可以使用。进入后续的逻辑处理。

4. **处理 `Cell` 为null的情况：**
```
if ((c = cs[(n - 1) & index]) == null) {
   // ... (后续代码)
}
```
如果选定的索引位置的 `Cell` 为 `null`，尝试创建一个新的 `Cell` 对象，并使用 CAS 操作来尝试将其放入数组中。

5. **CAS 操作尝试放入新的 `Cell`：**
```
if (cellsBusy == 0) {       // Try to attach new Cell
   Cell r = new Cell(x);   // Optimistically create
   if (cellsBusy == 0 && casCellsBusy()) {
       try {
           Cell[] rs; int m, j;
           if ((rs = cells) != null &&
               (m = rs.length) > 0 &&
               rs[j = (m - 1) & index] == null) {
               rs[j] = r;
               break;
           }
       } finally {
           cellsBusy = 0;
       }
       continue;           // Slot is now non-empty
   }
}
collide = false;
```
在这一段代码中，尝试通过 CAS 操作来将新的 `Cell` 放入数组中。如果成功，跳出循环。如果不成功，继续后续的处理逻辑。

6. **CAS 操作尝试累加 `Cell` 的值：**
```
else if (c.cas(v = c.value, (fn == null) ? v + x : fn.applyAsLong(v, x)))
   break;
```
如果选定的 `Cell` 不为空，尝试使用 CAS 操作来累加 `Cell` 的值。如果 CAS 操作成功，表示累加成功，跳出循环。

7. **其他情况的处理：**
```
else if (n >= NCPU || cells != cs)
   collide = false;            // At max size or stale
else if (!collide)
   collide = true;
else if (cellsBusy == 0 && casCellsBusy()) {
   try {
       if (cells == cs)        // Expand table unless stale
           cells = Arrays.copyOf(cs, n << 1);
   } finally {
       cellsBusy = 0;
   }
   collide = false;
   continue;                   // Retry with expanded table
}
index = advanceProbe(index);
```
这一部分处理了其他情况，包括数组大小已经达到上限、数组已经被修改过等情况。根据不同情况进行处理，以确保累加操作的正确性和性能。

8. **处理 `Cell` 数组为null的情况：**
```
else if (cellsBusy == 0 && cells == cs && casCellsBusy()) {
   try {                           // Initialize table
       if (cells == cs) {
           Cell[] rs = new Cell[2];
           rs[index & 1] = new Cell(x);
           cells = rs;
           break;
       }
   } finally {
       cellsBusy = 0;
   }
}
```
如果 `Cell` 数组为 `null`，尝试通过 CAS 操作来初始化数组，并放入新的 `Cell`。如果成功，跳出循环。

9. **最后的处理，使用基本值：**
```
// Fall back on using base
else if (casBase(v = base, (fn == null) ? v + x : fn.applyAsLong(v, x)))
   break;
```
如果以上的情况都没有匹配，最后的备选方案是使用基本值进行累加。

这段代码的设计主要考虑了多线程环境下的高并发场景，通过分段累加和 CAS 操作来保证累加的原子性，同时尽可能地减小竞争点，提高性能。