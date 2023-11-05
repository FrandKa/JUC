package com.kakaowo.juc_base.lessons02.blockQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 14:42
 **/

public class MyBlockQueue {
    private List<Object> list = new ArrayList<>();
    private final int CAPACITY = 3;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void take() {
        lock.lock();
        try {
            while(list.isEmpty()) {
                condition.await();
            }
            Object first = list.remove(0);
            System.out.println(Thread.currentThread().getName() + "> take :" + first);
            condition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void put() {
        lock.lock();
        try {
            while(list.size() == CAPACITY) {
                System.out.println(Thread.currentThread().getName() + " wait");
                condition.await();
            }
            TimeUnit.SECONDS.sleep(1);
            int num = new Random().nextInt(1024);
            list.add(num);
            System.out.println(Thread.currentThread().getName() + "> put :" + num);
            condition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
