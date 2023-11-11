package lessons_01.lesson06_jmm;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 16:01
 **/

public class Demo04 {
    public static void main(String[] args) {
        /**
         * 如何正确的适用volatile:
         * 1. 可见性: 写完立即刷新, 总是能够读取到最新值 >> 适用总线嗅探机制, 和内存屏蔽机制;
         * 2. 有序性: 禁止重排: happens-before规则 + 内存屏障(四个);
         * 3. 原子性: 没有;
         *
         * 底层会在字节码添加flag: ACC_VOLATILE;
         *
         * 内存屏障: 排序约束: 阻止屏障的两边的代码重排序
         */
    }
}
