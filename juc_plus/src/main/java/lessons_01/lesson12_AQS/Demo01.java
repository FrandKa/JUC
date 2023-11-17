package lessons_01.lesson12_AQS;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-14 08:59
 **/

public class Demo01 {
    public static void main(String[] args) {
        /**
         * 锁和同步器的关系: 锁是面向程序员的, 底层就是通过AQS实现的;
         * AQS >> Sync >> FairSync || NoFairSync (ReentrantLock)
         * acquire():
         * tryAcquire() >> 尝试获取锁; 公平锁先去判断是否有线程在排队, 非公平锁就是直接抢占
         * true >> 抢到了;
         * false 没有抢到 >> 排队;
         * 1. addWaiter(Node, model) >> 使用独占的方式加入队列
         * 2. acquireQueue() >> 正式的坐稳队列(阻塞)
         * 反复尝试, 如果是头节点的下一个节点并且已经准备好了 >> 尝试抢占
         * 抢占失败了 >> 使用LockSupport.pack()方法阻塞;
         * 3. unlock() >> release() >> tryRelease() >> 尝试唤醒;
         * 对于头节点的下一个节点并且准备就绪的情况下 >> LockSupport.unpack唤醒线程;
         */
//        ReentrantLock lock = new ReentrantLock();
        int num = Integer.MAX_VALUE + 1;
        System.out.println("num = " + num); // num = -2147483648
    }
}
