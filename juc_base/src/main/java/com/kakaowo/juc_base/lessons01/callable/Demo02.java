package com.kakaowo.juc_base.lessons01.callable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 11:09
 **/

public class Demo02 {
    /**
     *
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> ft = new FutureTask<>(() -> {
            System.out.println("ft start");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("ft over");
            return 1024;
        });

        new Thread(ft).start();
        System.out.println("thread start");
        // ft.get()方法会阻塞
        Thread.sleep(100);
//        System.out.println("ft.cancel(false) = " + ft.cancel(false));
//        会中断已经开始的任务:
        System.out.println("ft.cancel(true) = " + ft.cancel(true));
        if(ft.isCancelled()) {
            System.out.println("ft is cancel");
        } else {
            System.out.println("ft.get() = " + ft.get());
        }
        if(ft.isDone()) {
            System.out.println("ft is done");
        }
        System.out.println("thread end");
    }
}
