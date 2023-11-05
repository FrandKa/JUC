package com.kakaowo.juc_base.lessons01.juc_utils;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 13:19
 **/

public class DemoCyclicBarrier {
    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        final int NUMBER = 7;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(NUMBER, () -> {
            System.out.println("原神, 启动!!!!!!!!!!!!!!!!!!!");
        });
        for (int i = 0; i < NUMBER; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "> is ready");
                try {
                    int awaitNumber = cyclicBarrier.await();
//                    System.out.println(Thread.currentThread().getName() + "> is No." + awaitNumber + ", leave: " + (NUMBER - awaitNumber));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
                System.out.println(Thread.currentThread().getName() + "> 启动!!!!!!");
            }, "thread" + String.valueOf(i + 1)).start();
        }
        // 重置
//        System.out.println("cyclicBarrier.await() = " + cyclicBarrier.await());
    }
}
