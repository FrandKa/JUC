package lessons_01.lesson09_Thread_local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 13:43
 **/
class Base {
    ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);

    public void add() {
        threadLocal.set(threadLocal.get() + 1);
    }
}
public class Demo02 {
    public static void main(String[] args) {
        Base base = new Base();
        ExecutorService pool = Executors.newFixedThreadPool(5);
        try {
            for (int i = 0; i < 1000; i++) {
                pool.submit(() -> {
                    try {
                        System.out.println(Thread.currentThread().getName() + " before: " + base.threadLocal.get());
                        base.add();
                        System.out.println(Thread.currentThread().getName() + " after: " + base.threadLocal.get());
                    } finally {
                        // 一定记得清除;
                        base.threadLocal.remove();
                    }
                });
            }
        } finally {
            pool.shutdown();
        }
    }
}
