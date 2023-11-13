package lessons_01.lesson09_Thread_local;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 15:24
 **/

public class Demo04 {
    public static void main(String[] args) {
        /**
         * 为什么ThreadMapLocal需要使用弱引用:
         * 因为Entry中的key的引用指向的是ThreadLocal, 但是我们自己new 出来的时候也是指向ThreadLocal, 它在方法执行完毕的时候
         * 理论上是需要回收的, 但是由于key还是指向的ThreadLocal导致它还是不可以回收;
         * 所以我们需要设置为一个弱引用, 保证ThreadLocal销毁的时候, Entry也得回收, 防止内存泄漏(越来越多)
         * 但是弱引用仍然可能出现问题:
         * 1. 此后我们调用set的时候就是给Entry中赋值, 这样以来会被回收, 会产生一个key为null的空键
         * 2. 为了复用线程, 所以在使用线程池的时候并不会结束线程那么这个key == null 的entry会的value就会越来越多
         * 所以当我们需要手动调用remove方法去清除这些entry, 防止线程复用的时候获得上一次的值
         *
         * get和set方法的时候都会expungeStaleEntry()来清除脏entry;
         * remove的时候也会调用这个方法清除;
         * 彻底解决这个问题;
         *
         * 1. 记得初始化, 防止出现空指针异常; return setInitialValue(t);
         * 2. 记得remove;
         * 3. 推荐设置为static:
         *
         * ThreadLocal并不解决线程之间共享数据的问题;
         * 解决变量在不同的线程中隔离的问题;
         * Thread >> ThreadLocalMap >> Entry(WeakReference(ThreadLocal), Object);
         *
         */
    }
}
