package lessons_01.lesson01;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-09 09:47
 **/

public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Future: 定义了操作一股任务执行的一些方法:
        // 可以为主线程开一个分支任务;
        // Future: Java5新加的一个接口; 提供了一个异步并行的计算功能;
        // 主线程继续处理核心任务, 异步线程处理分支任务;
        // 多线程/有返回/异步任务;
        // 获得一个多线程只可以使用Thread()接收Runnable;
        // 但是Runnable没有返回值; Callable
        // 而且还有Future接口的规范 Future
        // 使用RunnableFuture;
        // 使用其实现类: FutureTask
        FutureTask<String> ft = new FutureTask<>(new MyThread02());
        Thread thread01 = new Thread(ft, "ft01");
        thread01.start();

        String result = ft.get(); // 注意这里的get和js不同, 这里是阻塞的;


        System.out.println("result = " + result);

    }
}

class MyThread01 implements Runnable {
    @Override
    public void run() {

    }
}

class MyThread02 implements Callable<String> {
    @Override
    public String call() throws Exception {
        System.out.println("------come in call()");
        return "Hello Callable";
    }
}
