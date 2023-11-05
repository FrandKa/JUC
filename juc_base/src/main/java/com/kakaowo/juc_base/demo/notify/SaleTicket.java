package com.kakaowo.juc_base.demo.notify;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 18:29
 **/

class Ticket {
    // 定义资源:
    private int number = 30;

    public synchronized boolean sale() {
        if (number <= 0) {
            System.out.println("Sale out");
            return false;
        }
        System.out.println(Thread.currentThread().getName() + " sale No" + number);
        number -= 1;
        return true;
    }
}

public class SaleTicket {
    public static void main(String[] args) {
        Ticket ticket = new Ticket();
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
