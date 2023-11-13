package lessons_01.lesson07_CAS;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 18:47
 **/

public class Demo05 {
    static AtomicInteger atomicInteger = new AtomicInteger(100);
    static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100, 0);

    public static void main(String[] args) {
        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t" + "---" + stamp);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            atomicStampedReference.compareAndSet(100, 101, stamp, ++stamp);
            System.out.println(Thread.currentThread().getName() + "\t" + "---" + atomicStampedReference.getStamp());
            atomicStampedReference.compareAndSet(101, 100, stamp, ++stamp);
            System.out.println(Thread.currentThread().getName() + "\t" + "---" + atomicStampedReference.getStamp());
        }, "t1").start();

        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t" + "---" + stamp);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("atomicStampedReference.compareAndSet(100, 2022, stamp, stamp + 1) = " + atomicStampedReference.compareAndSet(100, 2022, stamp, stamp + 1) + " " + Thread.currentThread().getName() + " " + atomicStampedReference.getReference());
        }, "t4").start();
    }

    private static void abaDemo() {
        new Thread(() -> {
            atomicInteger.compareAndSet(100, 101);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            atomicInteger.compareAndSet(101, 100);
        }, "t1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean flag = atomicInteger.compareAndSet(100, 2022);
            System.out.println(flag + " - " + atomicInteger.get());
        }, "t2").start();
    }
}
