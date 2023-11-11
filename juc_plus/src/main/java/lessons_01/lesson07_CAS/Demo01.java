package lessons_01.lesson07_CAS;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 16:09
 **/

public class Demo01 {
    // 使用原子类会更加轻量, 开销小;
    //
    AtomicInteger atomicInteger = new AtomicInteger(5);

    public int get() {
        return atomicInteger.get();
    }

    public void add() {
        // 乐观锁, 不加锁
        atomicInteger.getAndIncrement();
    }

    public static void main(String[] args) {
        // CAS底层原理:
        AtomicInteger atomicInt = new AtomicInteger(5);
        System.out.println(atomicInt.compareAndSet(5, 2022) + "\t" + atomicInt.get());
        System.out.println(atomicInt.compareAndSet(5, 2022) + "\t" + atomicInt.get());

        // 非阻塞原子性操作:
        // cmpxchg指令: 对总线加锁: 硬件级的保证; 但是独占的时间很短, 性能更好;
        // CAS依靠CPU原语实现的, 硬件级的保证;, 比较要更新变量的值和预期的值, 相等更新, 不相等 >> 放弃或者自旋;


    }
}
