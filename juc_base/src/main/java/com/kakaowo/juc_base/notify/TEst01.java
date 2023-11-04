package com.kakaowo.juc_base.notify;

import java.util.Objects;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 19:57
 **/

public class TEst01 {
    public static void main(String[] args) {
        // lock会有死锁的风险, 不会自动地调用unlock()地方法:
        // 所以需要注意lock的时候;
        // lock可以是等待锁的线程响应中断;
        // 通过lock可以知道这个线程是否成功的或的了锁;
        // 如果竞争资源激烈的情况下lock的性能会更好;
        // 线程值间的通讯;
        // 调用start方法不会立即创建线程;
        // 线程之间的通讯;
        // 多线程编程步骤:
        // 1. 创建资源类;
        // 2. 在资源类的操作方法:
        // 2.1: 判断;
        // 2.2: 干活;
        // 2.3: 通知;
        // 3. 创建多个先后曾, 调用资源类的操作方法;
        // 4. 防止虚假唤醒问题: 使用where

        // 例子: 有两个线程, 实现对一个初始值是0的变量进行操作;
        // 其中一个线程对于他进行 + 1; 的操作;
        // 另一个线程对他进行 - 1; 的操作;
        // 交替完成多次;
        // 线程之间的通信;
        //
        Resource resource = new Resource();
        new Thread(resource::sub, "thread_sub1").start();
        new Thread(resource::sub, "thread_sub2").start();
        new Thread(resource::sub, "thread_sub3").start();
        new Thread(resource::sub, "thread_sub4").start();

        new Thread(resource::add, "thread_add1").start();
        new Thread(resource::add, "thread_add2").start();
        new Thread(resource::add, "thread_add3").start();
        new Thread(resource::add, "thread_add4").start();
        // 虚假唤醒问题:
        // 1. 目前有四个线程;
        // 如果只有add, sub两个线程的时候是没有问题的;
        // 但是如果sub wait的时候 wait会释放锁;
        // 其他线程会继续抢夺控制权;
        // wait方法特点在哪里睡就会在哪里醒过来;
        // 这个时候判断的语句就不会生效了;
        // 解决方案: 使用while(条件) 代替 if();
    }
}

class Resource {
    private int num = 0;

    public void add() {
        synchronized (this) {
            for(int i = 0; i < 100; ++i) {
                while (num != 0) {
                    try {
                        this.wait();
                        System.out.println(Thread.currentThread().getName() + "> wait num: " + num);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                num += 1;
                System.out.println(Thread.currentThread().getName() + "> num + 1 = " + num);
                this.notifyAll();
            }
        }
    }

    public void sub() {
        synchronized (this) {
            for(int i = 0; i < 100; ++i) {
                while (num != 1) {
                    try {
                        this.wait();
                        System.out.println(Thread.currentThread().getName() + "> wait num: " + num);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                num -= 1;
                System.out.println(Thread.currentThread().getName() + "> num - 1 = " + num);
                this.notifyAll();
            }
        }
    }
}
