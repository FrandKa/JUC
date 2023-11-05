package com.kakaowo.juc_base.lessons01.various_locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 10:06
 **/

public class SyncLockDemo {
    public synchronized void add() {
        add();
    }

    public static void main(String[] args) {
        Object o = new Object();
        Lock lock = new ReentrantLock();

//        new Thread(() -> {
//            synchronized (o) {
//                System.out.println(Thread.currentThread().getName() + " wai");
//                synchronized (o) {
//                    System.out.println(Thread.currentThread().getName() + " nei");
//                }
//            }
//        }, "t1").start();

        new Thread(() -> {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " wai");
                try {
                    lock.lock();
                    System.out.println(Thread.currentThread().getName() + " nei");

                } finally {
                    lock.unlock();
                }
            } finally {
                lock.unlock();
            }
        }, "BB").start();

        new Thread(() -> {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " wai");
            } finally {
                lock.unlock();
            }
        }, "CC").start();
    }
}
