package lessons_01.lesson11_synchronized;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 18:15
 **/
class Resource {
    public int num = 50;

    synchronized void sale() {
        if (num > 0) {
            num--;
            System.out.println(Thread.currentThread().getName() + " get " + num);
            System.out.println(ClassLayout.parseInstance(this).toPrintable());
        }
    }
}

public class Demo02 {
    public static void main(String[] args) {
        /**
         * 单线程竞争, 偏向锁;
         */
        Resource r = new Resource();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            for (int i = 0; i < 55; i++) {
                r.sale();
            }
        }, "t1").start();

//        new Thread(() -> {
//            for (int i = 0; i < 55; i++) {
//                r.sale();
//            }
//        }, "t2").start();
//
//        new Thread(() -> {
//            for (int i = 0; i < 55; i++) {
//                r.sale();
//            }
//        }, "t3").start();
    }
}
