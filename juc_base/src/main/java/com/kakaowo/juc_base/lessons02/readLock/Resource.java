package com.kakaowo.juc_base.lessons02.readLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 13:54
 **/

public class Resource {
    private Long version = 0L;

    private String data = "data";

    private final Lock lock = new ReentrantLock();

    public void read() {
        System.out.println(Thread.currentThread().getName() + "> read: " + data);
    }


}
