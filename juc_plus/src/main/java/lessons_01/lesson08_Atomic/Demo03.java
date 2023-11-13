package lessons_01.lesson08_Atomic;

import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 19:29
 **/

public class Demo03 {
    public static void main(String[] args) {
        AtomicMarkableReference<Integer> atomicMarkableReference = new AtomicMarkableReference<>(0, false);
        boolean marked;
        marked = atomicMarkableReference.isMarked();
        System.out.println("marked = " + marked);
        atomicMarkableReference.compareAndSet(0, 1, marked, !marked);
        marked = atomicMarkableReference.isMarked();
        System.out.println("marked = " + marked);
        atomicMarkableReference.compareAndSet(1, 0, marked, !marked);
        marked = atomicMarkableReference.isMarked();
        System.out.println("marked = " + marked);
        atomicMarkableReference.compareAndSet(0, 100, marked, !marked);
        System.out.println("atomicMarkableReference.getReference() = " + atomicMarkableReference.getReference());
        marked = atomicMarkableReference.isMarked();
        System.out.println("marked = " + marked);
    }
}
