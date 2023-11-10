package lessons_01.lesson02;

import java.util.concurrent.*;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 13:23
 **/

public class CompletableFutureDemo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            CompletableFuture.supplyAsync(() -> {
                System.out.println("-----come in");
                int result = ThreadLocalRandom.current().nextInt(10);

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                System.out.println("res = " + result);

                return result;
                // v result   ;  e: exception
            }, pool).whenComplete((v, e) -> {
                if(e == null) {
                    System.out.println("计算完成, 更新系统Update Value = " + v);
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                System.out.println("异常: " + e.getCause() + " " + e.getMessage());
                return null;
            });

            System.out.println(Thread.currentThread().getName() + " --- other");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }
    }
}
