package lessons_01.lesson05_lockSupport;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 10:12
 **/

public class Demo01 {
    public static void main(String[] args) {
        Object o = new Object();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (o) {
                System.out.println(Thread.currentThread().getName() + " come in");
                try {
                    // 如果需要使用必须要先持有锁
                    o.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("苏醒");
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (o) {
                o.notifyAll();
                // 唤醒一定需要在wait()后面;
                System.out.println("唤醒");
            }
        }, "t2").start();
    }
}
