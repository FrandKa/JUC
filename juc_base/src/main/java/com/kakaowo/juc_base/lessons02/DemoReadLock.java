package com.kakaowo.juc_base.lessons02;

import java.util.concurrent.FutureTask;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 13:53
 **/

public class DemoReadLock {
    public static void main(String[] args) {
        MyCache cache = new MyCache();
        for (int i = 0; i < 5; i++) {
            final int num = i;
            new Thread(() -> {
                cache.insert(num + "", num);
            }, String.valueOf(i)).start();
        }

        for (int i = 0; i < 5; i++) {
            final int num = i;
            new Thread(() -> {
                cache.query(num + "");
            }, String.valueOf(i)).start();
        }
    }
}
