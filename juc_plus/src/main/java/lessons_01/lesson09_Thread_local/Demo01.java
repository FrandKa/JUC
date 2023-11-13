package lessons_01.lesson09_Thread_local;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 13:17
 **/

class House {
    int saleCount = 0;

    public synchronized void sale() {
        this.saleCount += 1;
    }

    ThreadLocal<Integer> saleNumber = ThreadLocal.withInitial(() -> 0);

    public void saleVolume() {
        saleNumber.set(saleNumber.get() + 1);
    }

}
public class Demo01 {
    public static void main(String[] args) {
        /**
         * ThreadLocal提供线程局部变量;
         * 1. 为什么需要这个:
         * 实现每一个线程都有自己的专属本地变量副本, 避免了线程安全问题;
         * 为了线程安全, 只能加锁, 但是性能不高;
         * 假设一个公用的资源拷贝到各个线程实现一对一的模型, 这个时候就不需要加锁了;
         *
         */
        House house = new House();
        long startTime = System.nanoTime();

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                int size = new Random().nextInt(5) + 1;
                try {
                    for (int i1 = 0; i1 < size; i1++) {
                        house.saleVolume();
                        house.sale();
                    }
                    System.out.println("house.saleNumber.get() = " + house.saleNumber.get());
                } finally {
                    // 用完之后一定记得释放
                    house.saleNumber.remove();
                }
                // 需要及时释放, remove();
                // 必须回收自定义的线程内存变量, 尽力使用try finally进行释放; 防止线程池的时候不断地复用'
                // 时间久了线程会爆炸;

            }, String.valueOf(i + 1)).start();
        }

        long endTime = System.nanoTime();
        System.out.println("---costTime: " + (endTime - startTime) / 1000000 + "/ms");


        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("house.saleCount = " + house.saleCount);

    }
}
