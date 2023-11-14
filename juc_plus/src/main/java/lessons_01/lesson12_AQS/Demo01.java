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
         *
         */
//        ReentrantLock lock = new ReentrantLock();
        int num = Integer.MAX_VALUE + 1;
        System.out.println("num = " + num); // num = -2147483648
    }
}
