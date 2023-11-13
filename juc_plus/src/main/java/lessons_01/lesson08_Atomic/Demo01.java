package lessons_01.lesson08_Atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 19:14
 **/

class MyNumber {
    AtomicInteger atomicInteger = new AtomicInteger(0);

    public void add() {
        atomicInteger.getAndIncrement();
    }
}

public class Demo01 {
    /**
     * 基础类型的原子类: Integer, Long, Double
     * Case:
     * 1. CountDownLatch
     */
    public static final int SIZE = 50;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);
        MyNumber myNumber = new MyNumber();
        for (int i = 0; i < SIZE; i++) {
            new Thread(() -> {
                try {
                    for (int i1 = 0; i1 < Integer.MAX_VALUE; i1++) {
                        myNumber.add();
                    }
                } finally {
                    // 一定需要放在finally中
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();
        // 为什么去的时候没有到5000呢? 因为main线程没有等待上面所有的线程都计算完成;
        System.out.println("myNumber.atomicInteger.get() = " + myNumber.atomicInteger.get());
    }
}
