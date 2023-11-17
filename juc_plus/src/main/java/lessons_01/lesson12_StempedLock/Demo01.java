package lessons_01.lesson12_StempedLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-17 09:07
 **/

class Resource {
    Map<String, String> map = new HashMap<>();

    Lock lock = new ReentrantLock();

    // 不可重入
    StampedLock stampedLock = new StampedLock();

    ReadWriteLock rwLock =new ReentrantReadWriteLock();

    public void write(String key, String value) {
        long l = stampedLock.writeLock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t" + "---" + "write");
            map.put(key, value);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + "\t" + "---" + "over");
        } finally {
            stampedLock.unlock(l);
        }
    }

    public void read(String key) {
        // 解决写锁饥饿问题:
        // 1. 使用公平策略 >> 牺牲了系统的吞吐量;
        // 2. 使用乐观读锁的方式
        // 邮戳锁:
        long stamp = stampedLock.tryOptimisticRead();
        String value;
        System.out.println(Thread.currentThread().getName() + "\t" + "---" + "read");
        value = map.get(key);
        if(!stampedLock.validate(stamp)) {
            long l = stampedLock.readLock();
            value = map.get(key);
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                stampedLock.unlockRead(l);
            }
        }
        System.out.println(Thread.currentThread().getName() + "\t" + "---" + "value" + value);
    }
}

public class Demo01 {
    public static void main(String[] args) {
        /**
         * 读写锁:
         * ReentrantReadWriteLock:
         * 无锁: 混乱:
         *
         * Synchronized;
         * ReentrantLock; 安全;
         * 缺点: 不管是读还是写都只有一个线程占有资源;
         * 但是实际上读读操作其实是不需要独占的, 希望数据可以共享
         *
         */
        /**
         * 锁降级机制解决这个问题:
         * 什么是锁降级
         *
         */
        Resource resource = new Resource();
        for (int i = 0; i < 10; i++) {
            String num = String.valueOf(i);
            new Thread(() -> {
                resource.write(num, num);
            }, String.valueOf(i)).start();
        }

        for (int i = 0; i < 100; i++) {
            String num = String.valueOf(i % 10);
            new Thread(() -> {
                resource.read(num);
            }, String.valueOf(i)).start();
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 读锁没有完成的时候写锁是没有办法获得的
        for (int i = 0; i < 3; i++) {
            String num = String.valueOf(i + 10);
            new Thread(() -> {
                resource.write(num, num);
            }, String.valueOf(i + 10)).start();
        }

    }
}
