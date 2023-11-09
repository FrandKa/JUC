package lessons_01.lesson01;

import java.util.concurrent.*;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-09 10:43
 **/

public class CompletableFutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 解决阻塞问题:
        // 解决轮询消耗问题: 回调函数, 你结束了就回调这个函数通知我;
        // 观察者模式:
        // 不推荐使用new方法创建: 这个是不完备的;
//        CompletableFuture<String> completableFuture = new CompletableFuture<>();

        // 核心的4个静态方法:
        // 没有返回值:
        /**
         * Runnable;
         * Executor: 默认:     private static final Executor ASYNC_POOL = USE_COMMON_POOL ?
         *         ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
         */
//        CompletableFuture.runAsync();
        /**
         * 有返回值:
         * supply; : 供给性函数接口
         * executor: 默认:     private static final Executor ASYNC_POOL = USE_COMMON_POOL ?
         *         ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
         */
//        CompletableFuture.supplyAsync()

        // 没有返回值
        ExecutorService pool = Executors.newFixedThreadPool(3);
//        CompletableFuture<Void> completableFuture01 = CompletableFuture.runAsync(() -> {
//            // 默认使用: ForkJoinPool.commonPool-worker-1
//            System.out.println(Thread.currentThread().getName() + " ---come in run()");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }, pool); // pool-1-thread-1
//
//        // 输出是null;
//        System.out.println("completableFuture01.get() = " + completableFuture01.get());

        // 有返回值:
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " come in");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "aa";
        }, pool);

        // 有返回值:
        System.out.println("completableFuture.get() = " + completableFuture.get());

        pool.shutdown();
    }
}
