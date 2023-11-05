package com.kakaowo.juc_base.lessons02.readLock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 13:53
 **/

public class DemoReadLock {
    public static void main(String[] args) {
        // 容易出现线程饥饿问题:
        // 读的时候不可以写, 但是写的时候可以读, 如果一直读就一直不可以写;
        // 锁降级:
        // 写入锁降级为读锁: (写的权限 > 读的权限)
        // JDK8:
        // 获得写锁 => 获得读锁 => 释放写锁(降级) => 释放读锁
        MyCache cache = new MyCache();
        for (int i = 0; i < 5; i++) {
            final int num = i;
            new Thread(() -> {
                cache.insert(num + "", num);
            }, String.valueOf(i)).start();
        }

        for (int i = 0; i < 5; i++) {
            final int num = i;
            new Thread(() -> {
                cache.query(num + "");
            }, String.valueOf(i)).start();
        }
    }
}
