package lessons_01.lesson08_Atomic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 19:44
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
class BackAccount {
    String bankName = "CCB";
    volatile int money = 0;

    static AtomicIntegerFieldUpdater<BackAccount> fieldUpdater =
            AtomicIntegerFieldUpdater.newUpdater(BackAccount.class, "money");

    // 原子性, 不加锁:
    public static void plus(BackAccount backAccount) {
        fieldUpdater.getAndIncrement(backAccount);
    }
}

public class Demo04 {
    /**
     * AtomicIntegerFieldUpdater
     * 更新字段值;
     * 以一种线程安全的方式修改一个非线程安全的类中的字段的值;
     * 1. 修改的属性一定需要有 public volatile 关键字;
     * 2. 修改的属性不可以是static; // 因为这个修改本身就是相当于静态的;
     * AtomicIntegerFieldUpdater 只可以操作: int || Integer不可以
     *
     * 以前需要修改: 必须上锁;
     * 原先的更新方式是对一整个对象上锁, 但是这种原子化的操作可以不对整个对象加锁, 只是精细化的对于字段的原子操作;
     */
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        BackAccount backAccount = new BackAccount();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    for (int i1 = 0; i1 < 1000; i1++) {
                        BackAccount.plus(backAccount);
                    }
                } finally {
                    countDownLatch.countDown();
                }
            }, String.valueOf(i)).start();
        }
        countDownLatch.await();

        System.out.println(backAccount.money);
    }
}
