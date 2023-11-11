package lessons_01.lesson04_interrupt;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 09:57
 **/

public class Demo04 {
    public static void main(String[] args) {
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName()+ " - " + Thread.interrupted());
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName()+ " - " + Thread.interrupted());
        System.out.println("--------------------------------------------------------------------------");
        Thread.currentThread().interrupt();
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName()+ " - " + Thread.interrupted());
        System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName()+ " - " + Thread.interrupted());
        /**
         * 三种线程等待唤醒机制:
         * 1. synchronized: wait(), notify() ;
         * 2. 使用Lock().newCondition() => Condition > await(); signal();
         * 3. 使用LockSupport >>
         * 为什么需要LockSupport?
         */
    }
}
