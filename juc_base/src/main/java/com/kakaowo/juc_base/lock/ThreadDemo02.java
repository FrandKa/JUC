package com.kakaowo.juc_base.lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 20:32
 **/

public class ThreadDemo02 {
    public static void main(String[] args) {
        Resource resource = new Resource();
        new Thread(resource::sub, "thread_sub1").start();
        new Thread(resource::sub, "thread_sub2").start();
        new Thread(resource::sub, "thread_sub3").start();
        new Thread(resource::sub, "thread_sub4").start();
        new Thread(resource::sub, "thread_sub5").start();
        new Thread(resource::sub, "thread_sub6").start();
        new Thread(resource::sub, "thread_sub7").start();
        new Thread(resource::sub, "thread_sub8").start();
        new Thread(resource::sub, "thread_sub9").start();
        new Thread(resource::sub, "thread_sub10").start();

        new Thread(resource::add, "thread_add1").start();
        new Thread(resource::add, "thread_add2").start();
        new Thread(resource::add, "thread_add3").start();
        new Thread(resource::add, "thread_add4").start();
        new Thread(resource::add, "thread_add5").start();
        new Thread(resource::add, "thread_add6").start();
        new Thread(resource::add, "thread_add7").start();
        new Thread(resource::add, "thread_add8").start();
        new Thread(resource::add, "thread_add9").start();
        new Thread(resource::add, "thread_add10").start();
    }
}

class Resource {
    private int num = 0;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private final int ADD = 0;
    private final int SUB = 1;

    public void add() {
        this.opera(ADD);
    }

    public void sub() {
        this.opera(SUB);
    }

    private void opera(int op) {
        lock.lock();
        try {
            while(num != op) {
                // 等待
                condition.await();
                System.out.println(Thread.currentThread().getName() + "> wait num: " + num);
            }
            if(op == ADD) {
                num += 1;
            } else {
                num -= 1;
            }
            System.out.println(Thread.currentThread().getName() + "> work num: " + num);
            // 通知
            condition.signalAll();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}