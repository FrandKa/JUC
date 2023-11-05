package com.kakaowo.juc_base.lessons02.fort_join;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 16:29
 **/

public class Demo01 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 例子: 实现从1 + -> 100;
        // 约定相加的两个数的差值不超过10;
        MyTask myTask = new MyTask(1, 100);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<Integer> task = forkJoinPool.submit(myTask);
        System.out.println("task.get() = " + task.get());
        forkJoinPool.shutdown();
    }
}

class MyTask extends RecursiveTask<Integer> {
    private static final Integer VALUE = 10;
    private Integer begin;
    private Integer end;
    
    private Integer result = 0;

    public MyTask(Integer begin, Integer end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        if(end - begin > VALUE) {
            Integer mid  = (begin + end) >> 1;
            System.out.println("mid = " + mid);
            MyTask left = new MyTask(begin, mid);
            MyTask right = new MyTask(mid + 1, end);
            left.fork();
            right.fork();
            result = left.join() + right.join();
        } else {
            for(int i =  begin; i <= end; i++) {
                result += i;
            }
        }
        return result;
    }
}
