package com.kakaowo.juc_base.lessons02.threadPool;

import java.util.concurrent.*;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 16:05
 **/

public class Demo03 {
    public static void main(String[] args) {
        // 自定义线程池:
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                20,
                30,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new MyRejectHandler()
        );

        try {
            for (int i = 0; i < 100; i++) {
                threadPoolExecutor.execute(() -> {
                    System.out.println(Thread.currentThread().getName());
                });
            }
        } finally {
            threadPoolExecutor.shutdown();
        }
    }
}

class MyRejectHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.println("rejected");
    }
}
