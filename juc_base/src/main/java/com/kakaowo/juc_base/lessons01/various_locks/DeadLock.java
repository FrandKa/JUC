package com.kakaowo.juc_base.lessons01.various_locks;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 10:17
 **/

public class DeadLock {
    final static Object a = new Object();
    final static Object b = new Object();
    public static void main(String[] args) {
        /**
         * 1. 什么是死锁:
         * 两个或者两个以上的进程在执行过程中, 因为争夺资源而造成的一种相互等待的现象;
         * 如果没有外力干涉, 就无法继续推进;
         *
         * 2. 产生死锁的原因:
             * 1. 系统资源不足;
             * 2. 进程推进的顺序不合适;
             * 3. 资源分配不当;
         * 3. 验证是否是死锁;
         * 1. jps 类似linux ps -ef;
         * 2. jstack
         */
        new Thread(() -> {
            synchronized (a) {
                System.out.println(Thread.currentThread().getName() + " 持有A, 试图获得B");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (b) {
                    System.out.println(Thread.currentThread().getName() + " 持有A B");
                }
            }
        }, "A").start();

        new Thread(() -> {
            synchronized (b) {
                System.out.println(Thread.currentThread().getName() + " 持有B, 试图获得A");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (a) {
                    System.out.println(Thread.currentThread().getName() + " 持有A B");
                }
            }
        }, "B").start();

    }
}
