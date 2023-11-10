package lessons_01.lesson03_lock;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 16:24
 **/

public class Demo02 {
    Object o = new Object();

    public void m1() {
        synchronized (o) {
            System.out.println("hello");
        }
    }

    public synchronized void m2() {
        System.out.println("hello");
    }

    public static synchronized void m3() {
        System.out.println("hello");
    }

    public static void main(String[] args) {

    }
}
