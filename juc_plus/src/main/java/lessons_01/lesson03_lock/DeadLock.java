package lessons_01.lesson03_lock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 18:06
 **/

public class DeadLock {
    public static void main(String[] args) {
        final Object A = new Object();
        final Object B = new Object();

        new Thread(() -> {
            synchronized (A) {
                System.out.println(Thread.currentThread().getName() + "获得A, 需要B");
                synchronized (B) {
                    System.out.println(Thread.currentThread().getName() + "获得了所有的锁");
                }
            }
        }, "A").start();

        new Thread(() -> {
            synchronized (B) {
                System.out.println(Thread.currentThread().getName() + "获得B, 需要A");
                synchronized (A) {
                    System.out.println(Thread.currentThread().getName() + "获得了所有的锁");
                }
            }
        }, "B").start();
    }
}
