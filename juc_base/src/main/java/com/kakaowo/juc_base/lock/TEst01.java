package com.kakaowo.juc_base.lock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 13:36
 **/

public class TEst01 {
    public static void test01() {
        /**
         *  wait, sleep;
         *  并发和并行:
         *  串行模式和并行模式:
         *  并发: 同一个时刻多个线程在访问同一个资源, 多个线程对于一个点;
         *  并行: 多项工作一起执行, 之后汇总;
         *  管程: Monitor监视器: 是一个同步机制; 通俗来说的锁;
         *  JVM中的同步基于进入和退出, 使用管程对象实现的, 在对象创建的时候创建的:
         *  执行线程操作需要先持有管程对象, 这个时候别的线程就不可以获取这个对象, 使用这个资源了;
         *
         */

        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "::" + Thread.currentThread().isDaemon());
            while (true) {

            }
        }, "aa");
        // 创建
        thread.setDaemon(true);
        thread.start();

        System.out.println(Thread.currentThread().getName() + "over");
    }

    public static void main(String[] args) {
        test01();
    }
}
