package com.kakaowo.juc_base.lessons01.juc_utils;

import java.util.concurrent.CountDownLatch;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 12:59
 **/

public class DemoCountDown {
    private static final Integer TOTAL = 6;
    public static void main(String[] args) throws InterruptedException {
        // 6个同学离开教室后, 值班同学开可以锁门:
        int number = 6;
        CountDownLatch countDownLatch = new CountDownLatch(TOTAL);

        for (int i = 0; i < TOTAL; i++) {
            new Thread(() -> {
                System.out.println("> No." + Thread.currentThread().getName() + " leave");
                countDownLatch.countDown();
            }, String.valueOf(i + 1)).start();
        }
        // 一直等待, 直到计数器变成0之后唤醒
        countDownLatch.await();

        System.out.println(Thread.currentThread().getName() + "> all leave, close door");
    }
}
