package lessons_01.lesson01;

import java.util.concurrent.*;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-09 10:06
 **/

public class FutureDemo02 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 优点:
         * 1. future + 线程池
         *
         *
         *
         */
        // 三个任务, 一个线程处理, 耗时多少: 三个任务
//        m1(); // 1101/ms
        // 同时做:
        long startTime = System.nanoTime();
        FutureTask<String> ft1 = new FutureTask<>(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "task01";
        });
        FutureTask<String> ft2 = new FutureTask<>(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "task02";
        });
        FutureTask<String> ft3 = new FutureTask<>(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "task03";
        });
        // 每一次都新建一个new出来的线程, 效率低下, 使用线程池;
//        Thread thread01 = new Thread(ft1);
//        thread01.start();

        ExecutorService pool = Executors.newFixedThreadPool(3);
        pool.submit(ft1);
        pool.submit(ft2);
        pool.submit(ft3);

        // 503/ms
        // 只受最大的耗时影响
        String res1 = ft1.get();
        String res2 = ft2.get();
        String res3 = ft3.get();


        long endTime = System.nanoTime();
        System.out.println("---costTime: " + (endTime - startTime) / 1000000L + "/ms");
        System.out.println(Thread.currentThread().getName() + " ----end");

        // 记得释放
        pool.shutdown();
    }

    public static void m1() {
        long startTime = System.nanoTime();

        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long endTime = System.nanoTime();
        System.out.println("---costTime: " + (endTime - startTime) / 1000000 + "/ms");
    }
}
