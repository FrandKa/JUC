package lessons_01.lesson12_StempedLock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-17 09:47
 **/

public class Demo02 {
    public static void main(String[] args) {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

//        new Thread(() -> {
//            writeLock.lock();
//            System.out.println("---xie");
//            writeLock.unlock();
//        }, "A").start();
//
//        new Thread(() -> {
//            readLock.lock();
//            System.out.println("---读取");
//            readLock.unlock();
//        }, "B").start();

        writeLock.lock();
        System.out.println("---xie");
        /**
         * ..........
         */
        readLock.lock();
        System.out.println("read");

        writeLock.unlock();

        readLock.unlock();


    }
}
