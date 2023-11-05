package com.kakaowo.juc_base.lessons01.juc_utils;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 13:32
 **/

public class DemoSemaphore {
    public static void main(String[] args) {
        final int SUM = 3;
        final int TOTAL = 6;
        Semaphore semaphore = new Semaphore(SUM);
        for (int i = 0; i < TOTAL; i++) {
            new Thread(() -> {
                try {
                    // 抢占:
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "> get");
                    TimeUnit.SECONDS.sleep(new Random().nextInt(20));
                    System.out.println(Thread.currentThread().getName() + "> ----leave");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    semaphore.release();
                }
            }, "car" + String.valueOf(i)).start();
        }
    }
}
