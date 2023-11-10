package lessons_01.lesson02;

import java.util.concurrent.*;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 14:42
 **/

public class Demo02 {
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        test06();
    }

    /**
     * 获得结果方法
     */
    public static void test01() {
        // get()
        // join();
        // getNow();
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "abc";
        });

//        System.out.println("completableFuture.get() = " + completableFuture.get());
        // 超时设置:
//        System.out.println("completableFuture.get(2, TimeUnit.SECONDS) = " + completableFuture.get(2, TimeUnit.SECONDS));
        // join 和 get差不多
//        System.out.println("completableFuture.join() = " + completableFuture.join());

        // 如果计算完成, 返回计算结果, 如果没有, 会使用我们设置的默认值
//        try {
//            TimeUnit.SECONDS.sleep(2);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("completableFuture.getNow(\"xxx\") = " + completableFuture.getNow("xxx"));
        // 是否打断, 原先的值, 直接获得默认值;
        System.out.println(completableFuture.complete("complete") + " - " + completableFuture.join());
    }

    // 对于计算结果进行处理
    public static void test02() {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        try {
            CompletableFuture.supplyAsync(() -> {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("get 1");
                        return 1;
                        // handle有异常也可向下走;, 可以捕获这个异常;
                    }, pool).handle((f, e) -> {
                        // 哪里错了哪里中断;
                        int i = 10 / 0;
                        System.out.println("1 + 2");
                        return f + 2;
                        // thenApply() 一般使用: 如果有异常就中断;
                    }).handle((f, e) -> {
                        System.out.println("f + 3");
                        return f + 3;
                    })
                    .whenComplete((v, e) -> {
                        if (e == null) {
                            System.out.println("result = " + v);
                        }
                    }).exceptionally(e -> {
                        System.out.println(e.getMessage());
                        return null;
                    });

            System.out.println(Thread.currentThread().getName() + " do others");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }
    }
    public static void test03() {
        // 消费型: 没有返回值值:
        CompletableFuture.supplyAsync(() -> {
            return 1;
        }).thenApply(v -> v + 2)
                .thenAccept(System.out::println);
        CompletableFuture.supplyAsync(() -> {
            System.out.println("A");
            return "A";
        }).thenRun(() -> {
            System.out.println("B");
        }).thenAccept(v -> {
            System.out.println(v);
        });
    }
    // thenRun(Runnable) >> A结束继续执行B, 没有输入也没有返回; AB无关系;
    // thenAccept() >> B 需要 A 的返回结果, 但是没有返回值;
    // thenApply() >> B 需要 A, B也有返回;

    public static void test04() {
        ExecutorService pool = Executors.newFixedThreadPool(5);
        // 1pool-1-thread-1
        //2pool-1-thread-1
        //3pool-1-thread-1
        //4main
        //null
        // 但是，请注意，thenRun 方法是在前一个阶段的线程上执行的，而不是在指定的线程池上执行。
        // 这就是为什么你观察到有几次 thenRun 使用了 main 线程的原因。
        // 默认情况下，如果不显式指定线程池，thenRun 会在前一个阶段的线程上运行。
        // 不一定会直接用你指定的线程池: 执行的太快了, 直接使用main完成了
        // 后面的任务和前面的任务共用一个线程池
        try {
            CompletableFuture<Void> completableFuture = CompletableFuture.supplyAsync(() -> {
                System.out.println("1" + Thread.currentThread().getName());
                return "abc";
                // 不传递线程池: ForkJoinPool
                // 传入了一个自定义的线程池: 后面的任务都用自定义的
                // Async: 的方法不会传递自定义的线程池
            }, pool).thenRun(() -> {
                System.out.println("2" + Thread.currentThread().getName());
            }).thenRun(() -> {
                System.out.println("3" + Thread.currentThread().getName());
            }).thenRun(() -> {
                System.out.println("4" + Thread.currentThread().getName());
            });
            System.out.println(completableFuture.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }
    }

    // 对于计算速度进行选择:
    public static void test05() {
        CompletableFuture<String> playerA = CompletableFuture.supplyAsync(() -> {
            System.out.println("PlayerA come in");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Player A";
        });
        CompletableFuture<String> playerB = CompletableFuture.supplyAsync(() -> {
            System.out.println("PlayerB come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Player A";
        });
        CompletableFuture<String> playerC = CompletableFuture.supplyAsync(() -> {
            System.out.println("PlayerC come in");
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Player C";
        });

        CompletableFuture<String> result = playerA.applyToEither(playerB, f -> {
            return f + " win";
        });

        System.out.println(result.join());

    }

    public static void test06() {
        // thenCombine() > 先完成的需要等待:
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " --- start");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("1 over");
            return 10;
        });

        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " --- start");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("2 over");
            return 20;
        });

        CompletableFuture<Integer> result = future1.thenCombine(future2, (x, y) -> {
            System.out.println("--- 合并");
            return x + y;
        });

        System.out.println("result.join() = " + result.join());

    }
}
