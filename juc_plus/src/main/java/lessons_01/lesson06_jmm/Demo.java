package lessons_01.lesson06_jmm;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 10:46
 **/

public class Demo {
    /**
     * JMM是对于硬件和系统的一种适配和兼容;
     * JMM本身就是一种抽象的概念, 并不真实存在;
     * 是一种约定和规范;
     * 关键技术围绕: 可见性, 原子性, 有序性;
     * 可见性:
     *
     */
    // 可见性:
    // 添加volatile关键字, 通知其他所有的线程这个变量的更改; 总线嗅探机制;
    // 因为不在一个线程中 所以不满足次序;
    // 1. 主线程没有刷新主存中的值;
    // 2. t1没有去更新数据;
    // read => load => use => assign => store => write
    // 一旦是高并发的情况, 上面的必定混乱
    // => lock => unlock 写操作一定加锁 >> 标记为独占

    static volatile boolean flag = true;

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " come in");
            while(flag) {
                // 这里的flag其实是t1线程的自己的内存中的值, 他不会去内存中读取新的值;
            }
            System.out.println("break out");
        }, "t1").start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        flag = false;
        System.out.println("修改完成");
    }
}
