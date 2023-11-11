package lessons_01.lesson06_jmm;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 15:04
 **/

public class Demo03 {
    public static void main(String[] args) {
        // 指令禁止重排:
        // 1. 依赖关系(最基础的)(同一个线程); 两个操作访问同一个变量, 有一个为写操作;
        // 1. 写后读: 不可以重排; 2. 写后写: 不可以重排; 3. 读后写: 不可以重排
        /**
         * 四大屏障:
         *
         * volatile适合的场景
         */
    }
}
