package lessons_01.lesson08_Atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 19:25
 **/

public class Demo02 {
    /**
     * 数组类型的原子类:
     *
     */
    public static void main(String[] args) {
        /**
         * 需要传入长度
         */
        AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(10);
        int[] array = {1, 2, 3, 4, 5};
        AtomicIntegerArray atomicArray = new AtomicIntegerArray(array);

        int oldValue = atomicArray.getAndSet(2, 10);
        System.out.println("Old value at index 2: " + oldValue);
        System.out.println("New value at index 2: " + atomicArray.get(2));

        boolean success = atomicArray.compareAndSet(3, 4, 20);
        System.out.println("CAS operation result: " + success);
        System.out.println("New value at index 3: " + atomicArray.get(3));

        atomicArray.incrementAndGet(0);
        System.out.println("Incremented value at index 0: " + atomicArray.get(0));

    }
}
