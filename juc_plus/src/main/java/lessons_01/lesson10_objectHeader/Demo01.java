package lessons_01.lesson10_objectHeader;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 16:28
 **/

public class Demo01 {
    public static void main(String[] args) {
        /**
         * 一个对象的组成:
         * 1. 对象实例: 对象头 >> 实例数据 >> 对齐填充
         * 对象头: 对象标记; MarkWord, 类型指针(类元信息);
         * 大小
         */

        /**
         * 1. 一个对象的大小; 没有数据的时候一般是16字节
         * 只有一个空的对象: 8 + 8;
         * 2. 这个hashCode记录在哪里?
         */
        Object o = new Object();
        System.out.println("o.hashCode() = " + o.hashCode());

        synchronized (o) {
            // 怎么知道这个锁;
            System.out.println(1);
        }
        // 4. 这个时候如何判断
        System.gc();

        // 实例的对象在堆空间
        // 类型指针指向方法区的类型模板: 左边的那个值 Klass类元信息
        Customer c2 = new Customer();
    }
}

class Customer { // 只有一个对象头的实例对象, 没有实例数据
    // 定义了一些数据, 实例数据挂载
    int id;
    String name;
}
