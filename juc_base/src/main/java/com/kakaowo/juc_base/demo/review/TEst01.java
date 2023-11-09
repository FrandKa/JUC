package com.kakaowo.juc_base.demo.review;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 17:17
 **/

public class TEst01 {
    public static void test01() {
        // 创建读写锁对象
        ReadWriteLock rwLock = new ReentrantReadWriteLock();
        // 创建读锁:
        Lock readLock = rwLock.readLock();
        // 创建写锁:
        Lock writeLock = rwLock.writeLock();
        // 锁方法和Lock相似
        readLock.lock();
        readLock.unlock();
    }
}
