package com.kakaowo.juc_base.demo.demo01;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 20:43
 **/

public class Resource {
    private int flag = 1;
    private Lock lock = new ReentrantLock();
    private Condition conditionA = lock.newCondition();
    private Condition conditionB = lock.newCondition();
    private Condition conditionC = lock.newCondition();

    public void doWorkA() {
        lock.lock();
        try {
            while(this.flag != 1) {
                conditionA.await();
            }
            System.out.println(Thread.currentThread().getName());
            this.flag = 2;
            conditionB.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void doWorkB() {
        lock.lock();
        try {
            while(this.flag != 2) {
                conditionB.await();
            }
            System.out.println(Thread.currentThread().getName());
            this.flag = 3;
            conditionC.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void doWorkC() {
        lock.lock();
        try {
            while(this.flag != 3) {
                conditionC.await();
            }
            System.out.println(Thread.currentThread().getName());
            this.flag = 1;
            conditionA.signal();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
