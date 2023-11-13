package lessons_01.lesson08_Atomic;

import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.LongConsumer;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 21:03
 **/

public class Demo06 {
    /**
     * 增强类原理深度分析:
     * DoubleAdder
     * LongAdder;
     * DoubleAccumulator;
     * LongAccumulator;
     *
     * 点赞数吞吐量大, 但是不要求实时精准;
     */
    public static void main(String[] args) {
        // 默认一开始的时候就是0
        // 只能进行加法, 只能从零开始
        LongAdder longAdder = new LongAdder();
        longAdder.reset();
        LongAccumulator longAccumulator = new LongAccumulator(Long::sum, 100); // 可以传入初始化值
        longAccumulator.reset(); // 返回初始值
        // base = this.identity = identity; 初始化值单独存储;
        System.out.println(longAccumulator);
        longAccumulator.accumulate(100);
        System.out.println("longAccumulator = " + longAccumulator);
//        System.out.println(longAccumulator.get());
//        System.out.println(longAccumulator.longValue());
    }
}
