package lessons_01.lesson04_interrupt;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 18:28
 **/

public class Demo01 {
    static volatile boolean isStop = false;
    static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    static volatile int count = 0;
    public static void main(String[] args) {
        // 一般一个线程应该有线程自己进行中断和停止, 而不是受其他的线程排布;
        // stop resume 都被废弃了;
        // 但是有的时候还是需要打断:
        // 所以提供了一个中断标识协商机制, 只是协商而不是强制中断;
        // 如果请求中断, 标识就会为true, 但是具体的操作还是有线程自己实现;
        // 中断标识位: true : 中断; false : 没有;

        /**
         * interrupt; () -> void; 设置中断标志位位true;
         * interrupted; static () -> boolean
         * 1. 判断当前线程是否被中断, 返回当前线程的中断状态;
         * 2. 清除当前线程的中断状态: false;
         *
         * isInterrupted; () -> boolean
         * 1. 判断当前线程是否被中断, 通过检查中断标识位;
         */
        test04();

    }

    public static void test01() {
        // 如何停止中断运行中的线程;
        /**
         * 1. 通过一个volatile变量来实现;
         * 2. 通过AtomicBoolean;
         * 3. 通过Thread自带的中断api实例方法实现
         */
        new Thread(() -> {
            while(true) {
                if(isStop) {
                    System.out.println("stop");
                    break;
                }
                System.out.println("---hello " + ++count);
            }
        }, "t1").start();

        while(true) {
            if(count > 20) {
                isStop = true;
                break;
            }
        }

    }

    public static void test02() {
        // AtomicBoolean;
        new Thread(() -> {
            while(true) {
                if(atomicBoolean.get()) {
                    System.out.println("stop");
                    break;
                }
                System.out.println("---hello " + ++count);
            }
        }, "t1").start();

        while(true) {
            if(count == 20) {
                atomicBoolean.set(true);
                break;
            }
        }
    }

    public static void test03() {
        // 通过自带的三个api实现: 上面的方法不够及时:
        Thread t1 = new Thread(() -> {
            while (true) {
//                try {
//                    TimeUnit.SECONDS.sleep(1);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                if(Thread.currentThread().isInterrupted()) {
                    /**
                     * Exception in thread "t1" java.lang.RuntimeException: java.lang.InterruptedException: sleep interrupted
                     * 	at lessons_01.lesson04_interrupt.Demo01.lambda$test03$2(Demo01.java:92)
                     * 	at java.base/java.lang.Thread.run(Thread.java:1583)
                     */
                    System.out.println("---stop");
                    break;
                }
                System.out.println("----hello");
            }
        }, "t1");
        t1.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Thread.interrupted() = " + Thread.interrupted());
        /**
         *     public static boolean interrupted() {
         *         return currentThread().getAndClearInterrupt();
         *     }
         *
         *     boolean getAndClearInterrupt() {
         *         boolean oldValue = interrupted;
         *         // We may have been interrupted the moment after we read the field,
         *         // so only clear the field if we saw that it was set and will return
         *         // true; otherwise we could lose an interrupt.
         *         if (oldValue) {
         *             interrupted = false;
         *             clearInterruptEvent();
         *         }
         *         return oldValue;
         *     }
         */
        t1.interrupt();
    }

    public static void test04() {
        Thread thread = new Thread(() -> {
            for(int i = 0; i < 300; i++) {
                System.out.println("----: " + i);
            }
            System.out.println("Thread.currentThread().isInterrupted() = " + Thread.currentThread().isInterrupted());
        }, "t1");
        thread.start();
        System.out.println("thread.isInterrupted() = " + thread.isInterrupted());
        try {
            TimeUnit.MILLISECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread.interrupt(); // 仅仅是设置状体true, 并不会直接停止线程;
        System.out.println("thread.isInterrupted() = " + thread.isInterrupted());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // true,
        /**
         * 但在线程正常结束时，Java并不会自动清除中断标识。因此，如果在线程结束时没有明确地清除中断标识，它将保持为true。
         * 这里不同的JDK的情况可能会不同:
         * JDK8: 是直接通过interrupt0()进行处理的; 然后调用isInterrupted的时候也是直接走native的方法
         * JDK17: 添加了一个属性 interrupted; interrupt的时候会将这个属性设置true;
         * 但是线程停止的时候, 这个属性并不会清除为false; 然后isInterrupted()方法:
         *     public boolean isInterrupted() {
         *         return interrupted;
         *     }
         * 很明显它是直接返回这个属性的, 这样可以提高代码的效率; 所以这里JDK17+会返回true;
         */
        System.out.println("thread.isInterrupted() = " + thread.isInterrupted());

    }
}
