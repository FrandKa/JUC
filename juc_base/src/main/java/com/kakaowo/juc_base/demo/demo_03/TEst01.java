package com.kakaowo.juc_base.demo.demo_03;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 09:10
 **/

class Phone {
    public static synchronized void sendSMS() throws Exception {
        Thread.sleep(4000);
        System.out.println("----sendSMS");
    }

    public synchronized void sendEmail() throws Exception {
        System.out.println("----sendEmail");
    }

    public void getHello() {
        System.out.println("----getHello");
    }
}

/*
演示synchronized锁的八种情况的问题:
1. 标准访问, 先打印短信还是邮件;
* 先短信后邮件;
* synchronized锁的是当前对象, 是this;

2. 停四秒在短信
* 先短信后邮件;

3. 普通方法和短信
* 先hello

4. 两部手机:
* 先邮件后短信
* 不同对象, 不是一个锁

5. 静态一部;
* 先短信
* synchronized锁的是.class字节码文件;
* 所有的静态的方法都是一个锁了

6. 静态两部;
* 先短信

7. 一个静态一个普通: 一部;
* 先邮件
* 一个锁的是.class, 一个是this, 互不干扰

8. 一个静态一个普通: 两部;
* 先邮件

*/

public class TEst01 {
    public static void main(String[] args) throws InterruptedException {
        Phone phone = new Phone();
        Phone phone2 = new Phone();

        new Thread(() -> {
            try {
                phone.sendSMS();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "AAA").start();

        Thread.sleep(100);

        new Thread(() -> {
            try {
//                phone.sendSMS();
                phone.sendEmail();
//                phone2.sendEmail();
//                phone.getHello();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "BBB").start();
    }
}
