package lessons_01.lesson01;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-09 09:10
 **/

public class Demo01 {
    public static void main(String[] args) {
        Thread thread01 = new Thread(() -> {
            while (true) {}
        });
        thread01.start();
        // thread.c
        // jvm.cpp
        // thread.cpp

        // 一把锁: synchronized; => 第四章解释:
        // 2个并: 什么是并发; 什么是并行;
        // 并发: concurrent: 同一个实体上的多个事件, 一台处理器"同时" => 秒杀服务, 12306
        // 并行: 在不同的实体上的多个事情, 多台设备的多个任务; 多对多

        // 3个程: 线程, 进程, 管程;
        /**
         * 进程: 系统中运行的一个应用程序(独立的内存)
         * 线程: 轻量级的进程, 干活的基本单元
         * 管程: Monitor; 也就是平时所说的锁;
         *
         * 用户线程:
         * 我们自己开启的线程
         *
         * 守护线程:
         * 帮助用户线程实现一些工作的服务的线程(例如: GC)
         */

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // CompletableFuture:
        // Future: 接口
    }
}
