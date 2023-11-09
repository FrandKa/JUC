package com.kakaowo.juc_base.lessons02.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 15:16
 **/

public class Demo01 {
    public static void main(String[] args) {
        // 一池N线程:
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        // 一池一线程:
//        ExecutorService executorService = Executors.newSingleThreadExecutor();
        // 线程池根据需求创建线程, 可扩容的线程池:
//        ExecutorService executorService = Executors.newCachedThreadPool();
        final long t1 = System.nanoTime();
        try {
            for (int i = 0; i < 1000; i++) {
                // 执行:
                executorService.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " work");
                });
            }
            executorService.execute(() -> {
                long t2 = System.nanoTime();
                System.out.println("time: "  + (t2 - t1));
            });
        } finally {
            // 放回线程池:
            executorService.shutdown();
        }
    }
}
