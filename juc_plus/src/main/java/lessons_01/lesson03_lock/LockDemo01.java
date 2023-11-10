package lessons_01.lesson03_lock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 15:32
 **/

import java.util.concurrent.TimeUnit;

/**
 * 1. **尽可能使用无锁数据结构**：
 *    - 无锁数据结构（Lock-Free Data Structures）通常基于原子操作（Atomic Operations）而不是传统的锁机制。
 *    - 原子操作是不可中断的操作，可以在并发环境中安全地执行，而不需要显式的锁。使用无锁数据结构可以减少锁的争用，提高并发性。
 *
 * 2. **锁区块而不是整个方法体**：
 *    - 如果只有部分代码需要同步，尽量只锁住那部分代码，而不是整个方法体。
 *    - 这样可以减小锁的粒度，减少线程争用锁的可能性，提高并发度。
 *
 * 3. **使用对象锁而不是类锁**：
 *    - 对象锁是针对实例对象的，而类锁是针对类的。类锁的争用范围更大，容易成为瓶颈。
 *    - 在高并发情境下，更推荐使用对象级别的锁，使得锁的范围更小，减小锁的争用。
 *
 * 4. **尽量减小加锁代码块的工作量**：
 *    - 加锁的代码块越小，持有锁的时间越短，从而减小了其他线程等待锁的时间。
 *    - 避免在锁代码块中执行过多的工作，特别是避免调用耗时较长的RPC方法，以充分利用锁的并发性。
 *
 * 5. **避免在锁代码块中调用 RPC 方法**：
 *    - 远程过程调用（RPC）可能会引入不确定的延迟，尤其是在网络不稳定的情况下。
 *    - 在持有锁的情况下调用RPC方法，可能会导致其他线程在等待锁的时候被阻塞更长时间，降低系统的性能。
 *
 * 1. 普通: 先邮件; 因为锁对象都是this;
 * 2. sendE 3s >> 先邮件
 * 3. 普通的hello方法: >> 先邮件
 * 4. 两个手机: 先快的 >> 都是this
 * >> 锁的是.class字节码文件; Class
 * 5. 两个静态同步方法: 邮件
 * 6. 两个静态方法: 两个对象: 邮件
 * 7. 有一个静态的方法, 一个普通的方法: 一个手机: 快的先
 * 8. 同上, 两个对象:
 *
 * 总结:
 *
 */

class Phone {
    public static synchronized void sendEmail() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("---- sendEmail");
    }

    public synchronized void sendSMS() {
        System.out.println("---- sendSMS");
    }

    public void hello() {
        System.out.println("--- hello");
    }
}
public class LockDemo01 {
    /**
     * 线程操作资源类:
     */
    public static void main(String[] args) {
        test01();
    }

    public static void test01() {
        // 先邮件
        Phone phone = new Phone();
        Phone phone1 = new Phone();

        new Thread(() -> {
            phone.sendEmail();
        }, "a").start();

        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            phone1.sendSMS();
//            phone.hello();
        }, "b").start();
    }
}
