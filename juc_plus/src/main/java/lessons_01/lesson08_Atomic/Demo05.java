package lessons_01.lesson08_Atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 20:56
 **/

class MyVar {
    public volatile Boolean isInit = Boolean.FALSE;

    AtomicReferenceFieldUpdater<MyVar, Boolean> fieldUpdater =
            AtomicReferenceFieldUpdater.newUpdater(MyVar.class, Boolean.class, "isInit");

    public void init() {
        if (fieldUpdater.compareAndSet(this, Boolean.FALSE, Boolean.TRUE)) {
            System.out.println(Thread.currentThread().getName() + " -- init it");
        } else {
            System.out.println("已初始化");
        }
    }
}
public class Demo05 {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        MyVar myVar = new MyVar();
        for (int i = 0; i < 5; i++) {
            new Thread(myVar::init, String.valueOf(i)).start();
        }


    }
}
