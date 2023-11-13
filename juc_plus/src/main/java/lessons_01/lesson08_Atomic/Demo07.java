package lessons_01.lesson08_Atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 21:23
 **/

public class Demo07 {
    final static int SIZE = 50;
    public static void main(String[] args) throws InterruptedException {
        /**
         * 设计一个点赞计数器
         * 50个线程, 1,000,000次
         */
        long startTime = System.nanoTime();
        CountDownLatch countDownLatch = new CountDownLatch(SIZE);
        ClickNumber clickNumber = new ClickNumber();

        for (int i = 0; i < SIZE; i++) {
            new Thread(() -> {
                try {
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        // 点赞操作:
//                        clickNumber.add(); ---costTime: 4267/ms
//                        ------- 快 -------------------------
//                        clickNumber.add01(); ---costTime: 1069/ms
//                        ClickNumber.fieldUpdater.getAndIncrement(clickNumber); ---costTime: 1069/ms
//                        clickNumber.add02(); ---costTime: 1069/ms
//                        ------ 极快 ------------------
                        clickNumber.add03(); // ---costTime: 133/ms
//                        clickNumber.add04(); ---costTime: 95/ms
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();
        long endTime = System.nanoTime();
        System.out.println("---costTime: " + (endTime - startTime) / 1000000 + "/ms");
        System.out.println("clickNumber.number = " + clickNumber.number);
        System.out.println("clickNumber.atomicLong = " + clickNumber.atomicLong);
        System.out.println("clickNumber.longAdder = " + clickNumber.longAdder);
        System.out.println("clickNumber.longAccumulator = " + clickNumber.longAccumulator);
    }
}

class ClickNumber {
    public volatile long number = 0;
    AtomicLong atomicLong = new AtomicLong(0);

//    ---costTime: 1109/ms
    public static AtomicLongFieldUpdater<ClickNumber> fieldUpdater =
            AtomicLongFieldUpdater.newUpdater(ClickNumber.class, "number");

    LongAdder longAdder = new LongAdder();
    LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 0);

    public synchronized void add() {
//        ---costTime: 4267/ms
        number++;
    }

    public void add01() {
//        ---costTime: 1093/ms
        atomicLong.getAndIncrement();
    }

    public void add02() {
        fieldUpdater.getAndIncrement(this);
    }

    public void add03() {
        longAdder.increment();
    }

    public void add04() {
        longAccumulator.accumulate(1);
    }
}
