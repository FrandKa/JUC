package lessons_01.lesson11_synchronized;

import org.openjdk.jol.info.ClassLayout;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 22:08
 **/

class ClassA {
    static Object o = new Object();

    public void m1() {
        Object o1 = new Object();
        // 多个线程使用的锁各不相同, 会擦除这个无聊的锁;
        // 一个线程中的多个操作使用同一个锁, 会进行锁的粗化, 会将这些代码并到一个锁中
        synchronized (o) {
            System.out.println("--- eee\t" + o.hashCode() + "\t" + o1.hashCode());
            System.out.println("ClassLayout.parseInstance(o1).toPrintable() = " + ClassLayout.parseInstance(o1).toPrintable());
        }
        synchronized (o) {
            System.out.println("--- eee\t" + o.hashCode() + "\t" + o1.hashCode());
            System.out.println("ClassLayout.parseInstance(o1).toPrintable() = " + ClassLayout.parseInstance(o1).toPrintable());
        }
        synchronized (o) {
            System.out.println("--- eee\t" + o.hashCode() + "\t" + o1.hashCode());
            System.out.println("ClassLayout.parseInstance(o1).toPrintable() = " + ClassLayout.parseInstance(o1).toPrintable());
        }
        synchronized (o) {
            System.out.println("--- eee\t" + o.hashCode() + "\t" + o1.hashCode());
            System.out.println("ClassLayout.parseInstance(o1).toPrintable() = " + ClassLayout.parseInstance(o1).toPrintable());
        }
        synchronized (o) {
            System.out.println("--- eee\t" + o.hashCode() + "\t" + o1.hashCode());
            System.out.println("ClassLayout.parseInstance(o1).toPrintable() = " + ClassLayout.parseInstance(o1).toPrintable());
        }
    }
}

public class Demo05 {
    public static void main(String[] args) {
        ClassA a = new ClassA();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                a.m1();
            }, String.valueOf(i)).start();
        }
    }
}
