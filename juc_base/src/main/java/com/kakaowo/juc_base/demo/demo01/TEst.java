package com.kakaowo.juc_base.demo.demo01;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 20:42
 **/

public class TEst {
    public static void main(String[] args) {
        // A 打印五次;
        // B 打印十次;
        // C 打印失误次;
        // 一共进行十轮
        Resource r = new Resource();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkA();
            }
        }, "AAA").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkA();
            }
        }, "AAA").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkA();
            }
        }, "AAA").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkB();
            }
        }, "BBB").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkB();
            }
        }, "BBB").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkC();
            }
        }, "CCC").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkB();
            }
        }, "BBB").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkC();
            }
        }, "CCC").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                r.doWorkC();
            }
        }, "CCC").start();
    }
}
