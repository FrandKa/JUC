package com.kakaowo.juc_base.lessons02.readLock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 14:31
 **/

public class Demo02 {
    public static void main(String[] args) {
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        // 锁降级: 可以在写操作的时候进行读操作, 提高性能;
        writeLock.lock();
        System.out.println("---write");
        readLock.lock();
        System.out.println("---read");
        writeLock.unlock();
        readLock.unlock();

        // 读锁不可以升级成为写锁
        readLock.lock();
        System.out.println("---read");
        // 不释放读锁就不可以使用写操作
        writeLock.lock();
        System.out.println("---write");
        writeLock.unlock();
        readLock.unlock();





    }
}
