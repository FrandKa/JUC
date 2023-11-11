package lessons_01.lesson07_CAS;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 16:19
 **/

public class Demo02 {
    private static final String str = "hello_world_abcdefaniondfasdddddddddfdasfagagdgagasgsad";
    static AtomicInteger len = new AtomicInteger(0);

    public static void main(String[] args) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            int num = i; // lambda表达式访问不了
            new Thread(() -> {
                // 自旋操作
                while(len.get() != num) {

                }
                System.out.print(str.charAt(num));
                len.getAndIncrement();
            }).start();
        }
    }
}
