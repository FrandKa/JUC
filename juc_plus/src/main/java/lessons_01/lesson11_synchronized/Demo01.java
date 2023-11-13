package lessons_01.lesson11_synchronized;

import org.openjdk.jol.info.ClassLayout;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 17:15
 **/

public class Demo01 {
    public static void main(String[] args) {
        /**
         * 1. 谈谈你对于Synchronized的锁优化;
         * 锁升级, 无锁, 偏量锁, 轻量锁:
         * synchronized是一个重量型锁, 会导致性能下降;
         * 如何做到在安全的同时又提高性能? 不要一口气上重量锁:
         * 无锁, 偏向锁, 轻量锁(CAS), 重量级锁(容易导致阻塞);
         * 锁的升级和演化:
         * 1. Java5之前只有无锁和重量级锁; 这个是一个操作系统级别的锁;, 锁的竞争激烈的时候性能会下降;
         * 2. 牵扯到了用户态的内核态的切换;
         *
         * 如果一个对象被锁住, 该Java对象的MW 的 LockWord指向monitor的起始地址;
         * Monitor的Owner字段会存放拥有相关联的对象锁的线程id
         */
        /**
         * java.lang.Object object internals:
         *  OFFSET  SIZE   TYPE DESCRIPTION                               VALUE            001 无锁状态
         *       0     4        (object header)                           01 00 00 00 (00000001 00000000 00000000 00000000) (1)
         *       4     4        (object header)                           00 00 00 00 (0000000,0 00000000 00000000 00000000,) (0)
         *       8     4        (object header)                           80 0e 00 00 (10000000 00001110 00000000 00000000) (3712)
         *      12     4        (loss due to the next object alignment)
         * Instance size: 16 bytes
         * Space losses: 0 bytes internal + 4 bytes external = 4 bytes total
         */
        Object o = new Object();
        // 如果调用了hashcode才会生成;
        System.out.println("o.hashCode() = " + o.hashCode());
        System.out.println("Integer.toBinaryString(o.hashCode()) = " + Integer.toBinaryString(o.hashCode()));

        System.out.println(ClassLayout.parseInstance(o).toPrintable());
    }
}
