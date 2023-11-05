package com.kakaowo.juc_base.lessons01.various_locks;

import java.util.concurrent.locks.ReentrantLock;

public class LTicket {
    private int number = 30;

    //    public ReentrantLock(boolean fair) {
    //        sync = fair ? new FairSync() : new NonfairSync();
    //    }
    // 1. 非公平锁: 可能出现线程饿死的现线;
    // 2. 公平锁: 不会出现饿死现象
    private final ReentrantLock lock = new ReentrantLock(true);

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