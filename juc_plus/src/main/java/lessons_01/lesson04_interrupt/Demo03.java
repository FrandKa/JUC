package lessons_01.lesson04_interrupt;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 19:26
 **/

public class Demo03 {
    // 如果一个线程处于阻塞的状态; 其他线程调用interrupt()方法的时候会此乃给出阻塞状态; 然后抛出异常;
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            while(true) {
                if(Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " - " + Thread.currentThread().isInterrupted() + " stop");
                    break;
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                    Thread.currentThread().interrupt(); // 这里会清除状态, 所以需要再调用一次;
                }

                System.out.println("--- work");
            }
        }, "t1");
        t1.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            // 解决异常后不停止程序的问题
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            // 如果中断的线程是阻塞的一个状态会清除中断状态;, 然后抛出异常, 但是注意这里是清除中断状态 >> 直接为false了;
            // 那么就可能会一直进行下去, 无法正常的中断;
            // 但是这里会退出阻塞状态, 所以下一次调用的时候会正常中断;
            t1.interrupt();
        }, "t2").start();
    }
}
