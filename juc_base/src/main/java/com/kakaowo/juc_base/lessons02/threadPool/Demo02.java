package com.kakaowo.juc_base.lessons02.threadPool;

import java.util.concurrent.*;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-05 15:43
 **/

public class Demo02 {
    public static void main(String[] args) {
        // 自定义线程池:
        // public ThreadPoolExecutor(
        //    int corePoolSize,               // 核心线程数
        //    int maximumPoolSize,            // 最大线程数
        //    long keepAliveTime,             // 线程空闲时间
        //    TimeUnit unit,                  // 空闲时间的时间单位
        //    BlockingQueue<Runnable> workQueue, // 任务队列
        //    ThreadFactory threadFactory,    // 线程工厂
        //    RejectedExecutionHandler handler // 拒绝策略
        //) {
        //    // 构造函数实现代码
        //}
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                20, // 常驻线程数量(核心)
                30, // 最大线程数量
                5, // 线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new ArrayBlockingQueue<>(5), // 阻塞队列
                Executors.defaultThreadFactory(), // 线程构造工厂
                new ThreadPoolExecutor.AbortPolicy() // 拒绝方法
        );
        // AbortPolicy（默认策略）：
        // 如果线程池无法接受新任务，将抛出 RejectedExecutionException 异常，拒绝新任务的提交。
        RejectedExecutionHandler abortPolicy = new ThreadPoolExecutor.AbortPolicy();

        // CallerRunsPolicy：
        // 如果线程池无法接受新任务，将把任务交给调用线程来执行。
        // 这可以用于确保任务一定会被执行，但可能会导致调用线程变慢。
        RejectedExecutionHandler callerRunsPolicy = new ThreadPoolExecutor.CallerRunsPolicy();

        // DiscardOldestPolicy：
        // 如果线程池无法接受新任务，将丢弃队列中最旧的任务，并尝试再次提交新任务。
        RejectedExecutionHandler discardOldestPolicy = new ThreadPoolExecutor.DiscardOldestPolicy();

        // DiscardPolicy：
        // 如果线程池无法接受新任务，将静默地丢弃新任务，不进行任何处理。
        RejectedExecutionHandler discardPolicy = new ThreadPoolExecutor.DiscardPolicy();

        // 1. 执行execute的时候调用run()的时候线程才会创建;
        // 2. 第一步：如果当前运行的线程数量少于核心线程数 (corePoolSize)，
        // 尝试启动一个新线程，以给定的任务作为它的第一个任务。addWorker 方法的调用是原子性的，
        // 它会检查线程池的运行状态 (runState) 和工作线程数量 (workerCount)，
        // 防止在不应该添加线程的情况下返回 false，避免出现误报情况。
        //
        //第二步：如果任务可以成功排队，
        // 然后我们仍然需要双重检查是否应该添加线程（因为自上次检查以来已有的线程可能已经终止），
        // 或者线程池在进入此方法后已关闭。
        // 因此，我们需要重新检查线程池的状态，如果需要，
        // 回滚排队操作（如果线程池已关闭），或者启动一个新线程（如果线程池没有线程）。
        //
        //第三步：如果无法将任务排队，然后尝试添加一个新线程。
        // 如果添加失败，我们知道线程池已关闭或达到饱和状态，因此拒绝该任务。

        // 先检测core线程是否有空余:
        // 没有就检查阻塞队列是否有空余;
        // 没有就新建线程;
        // 失败就调用拒绝方法:
        // 这里可以看出来, 线程的执行顺序和放入的顺序不一定相同;

    }
}
