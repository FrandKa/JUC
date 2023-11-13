package lessons_01.lesson11_synchronized;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 20:42
 **/



public class Demo03 {
    public static void main(String[] args) {
        /**
         * 001 无, 101偏向, 000轻量, 010重量锁
         * 原先是偏向锁, 使用CAS修改记录的线程id, 但是如果修改失败并且持有锁的线程任然处于同步代码块;
         * 升级位轻量级锁;
         * 轻量级锁会记录锁标志位000和指向线程栈中锁记录的指针。Lock Recode 指针
         * 一个线程占用这个资源的时候先复制原先的MW到自己的Lock Recode的位置
         * 竞争通过CAS修改MW的值为自己的Lock Recode, 释放资源, 将原先的MW(LockRecode的内容)先回
         * 如果其他线程也尝试获取同一个对象的轻量级锁，它们会通过 CAS（Compare And Swap）等指令来竞争锁。
         * 只有一个线程会成功，其他线程将进入自旋或者进一步升级为重量级锁。
         * JDK6之前: 10次或者CPU核数的一半;
         * JDK6之后: 自适应机制, 如果自旋成功了, 那么下一次的成功的限制会加大;
         * 如果很少自旋成功, 就会减少自旋的次数, 什么不自旋直接升级
         */
        Object o = new Object();

        new Thread(() -> {
            synchronized (o) {
                System.out.println("ClassLayout.parseInstance(o).toPrintable() = " + ClassLayout.parseInstance(o).toPrintable());
            }
        }, "t1").start();
    }
}
