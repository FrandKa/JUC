package com.kakaowo.juc_base.lessons02.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 16:57
 **/

public class DemoAsynchronous {
    // Java中的Future, JS的Promise; Reactor框架的Mono;
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 没有返回值的异步调用:
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + "> completableFuture1");
        });
        completableFuture.get();


        // 有返回值的异步调用:
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(
                () -> String.valueOf(1 / 0)
        );
        completableFuture1.whenComplete((t, u) -> {
            System.out.println("----t = " + t); // 返回值
            System.out.println("----u = " + u); // 异常
            // ----u = java.util.concurrent.CompletionException: java.lang.ArithmeticException: / by zero
        }).get();
    }
}
