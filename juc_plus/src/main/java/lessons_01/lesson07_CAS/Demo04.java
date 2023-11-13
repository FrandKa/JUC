package lessons_01.lesson07_CAS;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 18:40
 **/

public class Demo04 {
    public static void main(String[] args) {
        /**
         * AtomicStamp
         */
        AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(0, 0);
        AtomicInteger atomicInteger = new AtomicInteger(0);

        atomicInteger.set(1);
        atomicInteger.set(0);
        // 这里的版本号需要我们手动控制, 如果手动误操作还是很危险的
        atomicStampedReference.set(1, atomicStampedReference.getStamp() + 1);
        atomicStampedReference.set(0, atomicStampedReference.getStamp() + 1);

        System.out.println(atomicInteger.compareAndSet(0, 2) + " - " + atomicInteger.get());
        System.out.println(atomicStampedReference.compareAndSet(0, 3, 2, 1));

    }
}
