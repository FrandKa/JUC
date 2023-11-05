package com.kakaowo.juc_base.lessons02.blockQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 15:03
 **/

public class Demo02 {
    public static void main(String[] args) throws InterruptedException {
        // 创建一个:
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
        // 异常方法:
        test04(queue);
    }

    public static void test01(BlockingQueue<String> queue) {
        System.out.println("queue.add(\"a\") = " + queue.add("a"));
        System.out.println("queue.add(\"b\") = " + queue.add("b"));
        System.out.println("queue.add(\"c\") = " + queue.add("c"));
        // Exception in thread "main" java.lang.IllegalStateException: Queue full
        System.out.println("queue.element() = " + queue.element());
//        System.out.println("queue.add(\"a\") = " + queue.add("a"));
        System.out.println("queue.remove() = " + queue.remove());
        System.out.println("queue.remove() = " + queue.remove());
        System.out.println("queue.remove() = " + queue.remove());
    }

    public static void test02(BlockingQueue<String> queue) {
        System.out.println("queue.offer(\"a\") = " + queue.offer("a"));
        System.out.println("queue.offer(\"b\") = " + queue.offer("b"));
        System.out.println("queue.offer(\"c\") = " + queue.offer("c"));
        System.out.println("queue.offer(\"c\") = " + queue.offer("w"));
//        // Exception in thread "main" java.lang.IllegalStateException: Queue full
//        System.out.println("queue.element() = " + queue.element());
////        System.out.println("queue.add(\"a\") = " + queue.add("a"));
        System.out.println("queue.poll() = " + queue.poll());
        System.out.println("queue.poll() = " + queue.poll());
        System.out.println("queue.poll() = " + queue.poll());
        System.out.println("queue.poll() = " + queue.poll());
    }

    public static void test03(BlockingQueue<String> queue) throws InterruptedException {
        // 阻塞:
        queue.put("a");
        queue.put("b");
        queue.put("c");
//        queue.put("c");
        System.out.println("queue.take() = " + queue.take());
        System.out.println("queue.take() = " + queue.take());
        System.out.println("queue.take() = " + queue.take());
//        System.out.println("queue.take() = " + queue.take());
    }

    public static void test04(BlockingQueue<String> queue) throws InterruptedException {
        System.out.println("queue.offer(\"a\") = " + queue.offer("a", 3L, TimeUnit.MICROSECONDS));
        System.out.println("queue.offer(\"b\") = " + queue.offer("b", 3L, TimeUnit.MICROSECONDS));
        System.out.println("queue.offer(\"c\") = " + queue.offer("c", 3L, TimeUnit.MICROSECONDS));
        System.out.println("queue.offer(\"w\") = " + queue.offer("w", 3L, TimeUnit.SECONDS));
//        System.out.println("queue.poll() = " + queue.poll());
//        System.out.println("queue.poll() = " + queue.poll());
//        System.out.println("queue.poll() = " + queue.poll());
//        System.out.println("queue.poll() = " + queue.poll());
    }
}
