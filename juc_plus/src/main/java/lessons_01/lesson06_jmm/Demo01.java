package lessons_01.lesson06_jmm;

import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 14:40
 **/

public class Demo01 {
    public static void main(String[] args) {
        MyNumber myNumber = new MyNumber();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 1000; i1++) {
                    myNumber.add();
                }
            }, String.valueOf(i)).start();
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("myNumber.number = " + myNumber.number);
    }
}

class MyNumber {
    volatile int number = 0;
    volatile int version = 0;

    public void add() {
        while(true) {
            int newNumber = number + 1;
            int temp = version;
            if(temp == version) {
                number = newNumber;
                version = temp + 1;
                // i++:
                // 1. 获得i; 2. add(); 3. 写入i;
                // volatile不适合符合操作 (不符合原子性)
                // 适合作为一个信号或者标志 (可见性保证)
                // volatile对于复合操作不满足原子性;
                // 因为这里的内存屏蔽只是针对一个操作的, 但是在复合操作的间隙中间还是会有线程安全问题
                break;
            }
        }
    }
}