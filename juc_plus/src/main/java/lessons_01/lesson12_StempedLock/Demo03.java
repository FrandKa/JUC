package lessons_01.lesson12_StempedLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-17 10:42
 **/

public class Demo03 {
    static int num = 21;
    static StampedLock stampedLock = new StampedLock();

    public void write() {
        // 返回一个流水戳记:
        long l = stampedLock.writeLock();
        System.out.println(Thread.currentThread().getName() + "\t" + "---" + "write");
        try {
            num = num + 21;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } finally {
            stampedLock.unlockWrite(l);
        }
        System.out.println(Thread.currentThread().getName() + "\t" + "---" + "over");
    }

    void read() {
        long l = stampedLock.tryOptimisticRead();
        int result = num;
        System.out.println(Thread.currentThread().getName() + "\t" + "---" + "read");
        System.out.print("读取中");
        for (int i = 0; i < 8; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print(".");
        }
        System.out.println();
        if(!stampedLock.validate(l)) {
            long stamp = stampedLock.readLock();
            try {
                System.out.println("值已经被修改, 需要重新读取, 悲观锁加上");
                System.out.print("重新读取");
                result = num;
                for (int i = 0; i < 8; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.print(".");
                }
                System.out.println();

            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        System.out.println(Thread.currentThread().getName() + "\t" + "---" + "result = " + result);
    }

    public static void main(String[] args) {
        Demo03 demo03 = new Demo03();
        new Thread(() -> {
            demo03.read();
        }, "readThread").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "\t" + "---" + "come in");
            demo03.write();
        }, "writeThread").start();
    }

}
