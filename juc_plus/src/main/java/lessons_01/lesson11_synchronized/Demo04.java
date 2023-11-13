package lessons_01.lesson11_synchronized;

import org.openjdk.jol.info.ClassLayout;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 21:15
 **/

public class Demo04 {
    public static void main(String[] args) {
        /**
         * 虽然有竞争, 但是并不是很激烈, 获取锁的冲突时间很短;
         * 线程近乎交替执行的情况下
         * 使用自旋锁: CAS来解决这个问题;
         */
        Object o = new Object();
        new Thread(() -> {
            for (int i = 1; i < 100; i++) {
                synchronized (o) {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println("ClassLayout.parseInstance(o).toPrintable() = " + ClassLayout.parseInstance(o).toPrintable());
                }
            }
        }, "t1").start();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                synchronized (o) {
                    System.out.println(Thread.currentThread().getName());
                    System.out.println("ClassLayout.parseInstance(o).toPrintable() = " + ClassLayout.parseInstance(o).toPrintable());
                }
            }
        }, "t2").start();
    }
}
