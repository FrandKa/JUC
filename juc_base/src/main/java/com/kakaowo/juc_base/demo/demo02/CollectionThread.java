package com.kakaowo.juc_base.demo.demo02;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-04 21:05
 **/

public class CollectionThread {
    public static void main(String[] args) {
        /**
         * 集合的线程不安全问题
         * Exception in thread "Thread-71" java.util.ConcurrentModificationException
         * 并发修改异常:
         * 解决方法:
         * 1. 通过vector;
         * JDK 1.0的方案, 性能较差
         * ```java
         * // 添加了关键字:
         *     public synchronized boolean add(E e) {
         *         modCount++;
         *         add(e, elementData, elementCount);
         *         return true;
         *     }
         * ```
         *
         * 也不常用:
         * 2. Collections
         * 生成线程安全集合:
         *
         * 3. CopyOnWriteArrayList;
         * 1. 并发读取;
         * 2. 独立修改;
         * 3. 复制, 修改, 合并(覆盖);
         * ```java
         *     public boolean add(E e) {
         *         synchronized (lock) {
         *             Object[] es = getArray(); // 获取旧的数据
         *             int len = es.length;
         *             es = Arrays.copyOf(es, len + 1); // 复制
         *             es[len] = e; // 写入
         *             setArray(es); // 覆盖
         *             return true;
         *         }
         *     }
         * ```
         */
//        List<String> list = new ArrayList<>();
//        List<String> list = new Vector<>();
//        List<String> list = Collections.synchronizedList(new ArrayList<>());
//        List<String> list = new CopyOnWriteArrayList<>();

//        Set<String> set = new HashSet<>();
        // Exception in thread "Thread-43" java.util.ConcurrentModificationException
        // 解决方案: copyOnWriteArraySet()
//        Set<String> set = new CopyOnWriteArraySet<>();
//        Map<String, String> map = new HashMap<>();
        /**
         * ConcurrentHashMap：
         * 并发控制：ConcurrentHashMap 使用了一种称为分段锁（Segment Locks）的并发控制策略。它将整个数据结构划分为多个段（segments），每个段都有一个独立的锁。这样不同的线程可以同时操作不同的段，从而提高并发性能。只有在对同一段进行操作时，线程才需要竞争锁。
         * 实时更新：ConcurrentHashMap 允许实时更新，多个线程可以同时进行读和写操作，而且写操作不会导致复制整个数据结构。这意味着读操作的性能通常很好，而写操作只会影响涉及到的段，而不会锁住整个数据结构。
         */
        Map<String, String> map = new ConcurrentHashMap<>();
        // 并发修改异常:
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                // 添加内容:
                map.put(UUID.randomUUID().toString().substring(0, 8), UUID.randomUUID().toString().substring(0, 8));
                // 获取内容:
                System.out.println(Thread.currentThread().getName() + " list = " + map);
            }).start();
        }
    }
}
