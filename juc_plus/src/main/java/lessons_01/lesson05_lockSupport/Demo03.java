package lessons_01.lesson05_lockSupport;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 10:24
 **/

public class Demo03 {
    public static void main(String[] args) {
        // LockSupport: park, unpark;
        // 1. 本身就是一个锁, 不需要自己手动考虑了;
        // 2. 支持先唤醒后等待; 只要有许可就可以放行;
        // 3. 但是一定需要记住要成双使用;

        /**
         * 这个通行证只有一个, 必须是一对一的, 一般是一对一, 不会出现多对多;
         *
         * 1. 是一个线程阻塞的工具类;
         * 2. 自己就持有一把锁;
         * 3. 所有的方法都是静态;
         * 4. park, unpark,
         * 5. 底层是调用UNSAFE类;
         */
        Thread t1 = new Thread(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            System.out.println("come in" + System.currentTimeMillis());
            // 直接放行, 没有park阻塞拦截;
            LockSupport.park();
            LockSupport.park();
            System.out.println("苏醒" + System.currentTimeMillis());
        }, "t1");
        t1.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        new Thread(() -> {
            System.out.println("唤醒");
            LockSupport.unpark(t1);
        }, "t2").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LockSupport.unpark(t1);
    }
}
