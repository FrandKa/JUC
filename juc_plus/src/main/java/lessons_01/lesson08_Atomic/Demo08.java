package lessons_01.lesson08_Atomic;

import java.util.concurrent.atomic.LongAdder;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 09:44
 **/

public class Demo08 {
    public static void main(String[] args) {
        /**
         * LongAdder为什么更快:
         * 1. 我的猜测: LongAdder使用了32位的修改方式
         *
         * 隐藏成就: Number, Striped64, Cell
         * Cell
         *
         * LongAdder:
         * AtomicLong缺点CAS在竞争的时候会产生大量的空转:
         * 1. CPU的性能大大的下降；
         * 2. 进行分段操作的话, 可能可以解决这个办法:
         * 一开始: long base;
         * 第一次失败: Cell[0], Cell[1]分裂成为两个分散热点;
         * 各个线程只对自己的Cell[]进行操作;
         * 最后的时候result = base + Cell[]的和
         *
         * 总结: AtomicLong: 原理Unsafe的CAS自旋;
         * 缺点多个线程CAS操作会产生大量的自旋, 消耗CPU;
         * LongAdder 空间换时间, 分散热点;
         * 缺点实时性不精确, 最终一致性;
         * 使用场景: 大量用户点赞场景:
         */
        LongAdder longAdder = new LongAdder();
    }
}
