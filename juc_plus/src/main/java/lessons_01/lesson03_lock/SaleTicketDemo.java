package lessons_01.lesson03_lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Ticket //资源类，模拟3个售票员卖完50张票
{
    private int number = 50;
    // 公平锁和非公平锁:
    // 多个线程按照盛情所得顺序来获得锁
    private Lock lock = new ReentrantLock(true);


    public void sale() {
        lock.lock();
        try {
            if (number > 0) {
                System.out.println(Thread.currentThread().getName() + "卖出第：\t" + (number--) + "\t 还剩下:" + number);
            }
        } finally {
            lock.unlock();
        }
    }
}

public class SaleTicketDemo {
    public static void main(String[] args)//一切程序的入口
    {
        Ticket ticket = new Ticket();

        new Thread(() -> {
            for (int i = 0; i < 55; i++) ticket.sale();
        }, "a").start();
        new Thread(() -> {
            for (int i = 0; i < 55; i++) ticket.sale();
        }, "b").start();
        new Thread(() -> {
            for (int i = 0; i < 55; i++) ticket.sale();
        }, "c").start();
    }
}