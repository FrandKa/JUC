package lessons_01.lesson01;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-09 10:23
 **/

public class FutureTaskDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> ft = new FutureTask<>(() -> {
            System.out.println("----come in");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return "ft1 over";
        });

        Thread thread01 = new Thread(ft);
        thread01.start();
        System.out.println("其他业务");

        /**
         * 缺点:
         * 1. 一旦调用get() 一定要等待结果的返回, 容易产生阻塞;
         * 2. 假如我们希望是 过时不候, 自动离开
         * 3.
         *
         */
        // 如何优化, 防止阻塞:
        // 1. 添加超时限制
//        try {
//            String s = ft.get(2, TimeUnit.SECONDS);
//        } catch (TimeoutException e) {
//            System.out.println("time out");
//        }
        // 2. 状态判断:
        // 轮询状态, 后台返回状态;
        // 但是这个轮询会加大CPU的负荷, 降低了性能,
        while (true) {
            if(ft.isDone()) {
                System.out.println(ft.get());
                break;
            } else {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    System.out.println("在做了, 在做了...");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // 如果想要异步的结果吗尽量使用轮询的方式去获取, 不要使用阻塞的方式获得结果;
        // 这个使用CompletableFuture Java8;
        // 我们希望你的任务主动通知我, whenCompleted;
        // 回调通知;
        // 创建异步任务, Future + ThreadPool
        // 多个任务前后依赖可以组合处理;
        // 可以组合任务的结果:
        // 对于计算速度选择最快的:

        System.out.println(Thread.currentThread().getName() + "----end");

    }
}
