package com.kakaowo.juc_base.lessons02.blockQueue;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 14:39
 **/

public class Demo01 {
    public static void main(String[] args) {
        // 队列: 先进先出;
        // 栈: 后进先出;
        // 阻塞队列: 首先是一个对列;
        // 通过使用一个贡献的队列, 一端输入一段输出
        MyBlockQueue myBlockQueue = new MyBlockQueue();
        for (int i = 0; i < 21; i++) {
            new Thread(myBlockQueue::put, "provider: " + i).start();
            new Thread(myBlockQueue::take, "consumer: " + i).start();
        }
    }
}
