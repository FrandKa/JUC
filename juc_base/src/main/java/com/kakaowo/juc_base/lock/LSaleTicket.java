package com.kakaowo.juc_base.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 18:59
 **/

class LTicket {
    private int number = 30;

    private final ReentrantLock lock = new ReentrantLock();

    public boolean sale() {
        // 上锁:
        lock.lock();
        try {
            if(number <= 0) {
                return false;
            }
            System.out.println(Thread.currentThread().getName() + " sale " + number);
            number -= 1;
        } finally {
            // 解锁:
            lock.unlock();
        }
        return true;
    }

}

public class LSaleTicket {
    public static void main(String[] args) {
        LTicket ticket = new LTicket();
        Thread t1 = new Thread(() -> {
            while (ticket.sale()) ;
        }, "thread1");
        Thread t2 = new Thread(() -> {
            while (ticket.sale()) ;
        }, "thread2");
        Thread t3 = new Thread(() -> {
            while (ticket.sale()) ;
        }, "thread3");
        t1.start();
        t2.start();
        t3.start();
    }
}
