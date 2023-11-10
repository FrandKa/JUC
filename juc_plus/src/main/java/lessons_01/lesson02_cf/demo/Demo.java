package lessons_01.lesson02_cf.demo;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 14:03
 **/

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 *
 * 案例说明：电商比价需求，模拟如下情况：
 *
 * 1需求：
 *  1.1 同一款产品，同时搜索出同款产品在各大电商平台的售价;
 *  1.2 同一款产品，同时搜索出本产品在同一个电商平台下，各个入驻卖家售价是多少
 *
 * 2输出：出来结果希望是同款产品的在不同地方的价格清单列表，返回一个List<String>
 * 《mysql》 in jd price is 88.05
 * 《mysql》 in dangdang price is 86.11
 * 《mysql》 in taobao price is 90.43
 *
 * 3 技术要求
 *   3.1 函数式编程
 *   3.2 链式编程
 *   3.3 Stream流式计算
 */
public class Demo {
    static List<NetMall> list = Arrays.asList(
            new NetMall("jd"),
            new NetMall("dd"),
            new NetMall("db")
    );

    public static List<String> getPrice(List<NetMall> list, String productName) {
        return list.stream().map(i -> String.format("《%s》 in %s price is %.2f", productName, i.getNetMallName(), i.calPrice(productName))).collect(Collectors.toList());
    }

    public static List<String> getPriceAsync(List<NetMall> list, String productName) {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        return list.stream()
                .map(i -> CompletableFuture.supplyAsync(() -> String.format("《%s》 in %s price is %.2f", productName, i.getNetMallName(), i.calPrice(productName)), pool))
                .toList()
                .stream()
                .map(CompletableFuture::join)  // 直接在 CompletableFuture 上调用 join
                .collect(Collectors.toList());  // 最终的结果集，List<String>
        // 关于为什么这里需要先转换成为List:
        // 1. toList()方法会阻塞等待, 收集所有的CF, 然后每一个CF调用join方法: 也就是说是先花了1s收集, 然后map;
        // 2. 如果不这么做, 就是一个一个调用map(), 这个本质上还是串行的

    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        List<String> mysql = getPriceAsync(list, "mysql");
        mysql.forEach(System.out::println);
        long endTime = System.nanoTime();

        System.out.println("---costTime: " + (endTime - startTime) / 1000000 + "/ms");
    }
}

class NetMall {
    @Getter
    private String netMallName;

    public NetMall(String netMallName) {
        this.netMallName = netMallName;
    }

    public double calPrice(String productName) {
        System.out.println(Thread.currentThread().getName() + "--come in");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(Thread.currentThread().getName() + "--over");
        return ThreadLocalRandom.current().nextDouble() * 2 + productName.charAt(0);
    }
}
