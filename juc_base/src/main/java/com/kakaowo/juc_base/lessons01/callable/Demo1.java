package com.kakaowo.juc_base.lessons01.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 10:52
 **/

class MyRunnable implements Runnable {
    @Override
    public void run() {

    }
}

class MyCallable implements Callable {
    @Override
    public Integer call() throws Exception {
        return 200;
    }
}

public class Demo1 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new Thread(new MyRunnable(), "R").start();
        FutureTask<Integer> ft = new FutureTask<Integer>(new MyCallable());
        new Thread(ft, "C").start();
        System.out.println(ft.get());
    }
}
