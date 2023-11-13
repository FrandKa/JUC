package lessons_01.lesson07_CAS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 18:22
 **/

public class Demo03 {
    // 自旋锁:
    // SpinLock:
    /**
     * CAS的缺点:
     * 1. 循环时间长了导致CPU开销越来越大;
     * 2. ABA问题:
     * 比较并替换 >> 如果一个线程初始值A, 另一个线程 A -> B -> A, 因为最后的值是A, 导致还是正常通过了
     * 使用version来代替(版本号, 戳记流水) 只看内容是不可以的
     *
     * AtomicStamp
     */
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void lock() {
        Thread thread = Thread.currentThread();
        // 如果没有人持有锁, 持有锁, 设置锁;
        // 如果有人持有了, 就反复尝试
        while (!atomicReference.compareAndSet(null, thread)) {

        }
    }

    public void unLock() {
        Thread thread = Thread.currentThread();
        // 如果锁持有者是自己, 就释放, 不是就反复尝试
        while(!atomicReference.compareAndSet(thread, null)) {

        }
    }

    public static void main(String[] args) {
        Demo03 demo03 = new Demo03();
        new Thread(() -> {
            demo03.lock();
            System.out.println("come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("unlock");
            demo03.unLock();
        }, "A").start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            System.out.println("come in");
            demo03.lock();

            System.out.println("get");

            demo03.unLock();
        }, "B").start();
    }
}
