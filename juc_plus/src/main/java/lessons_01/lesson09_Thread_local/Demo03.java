package lessons_01.lesson09_Thread_local;

import java.lang.ref.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-13 13:51
 **/

class MyClass {
    @Override
    protected void finalize() throws Throwable {
        System.out.println(" --- finalize");
    }
}

public class Demo03 {
    /**
     * Thread / ThreadLocal / ThreadLocalMap
     * Thread >> ThreadLocal.ThreadLocalMap;
     * 每一个Thread都有一个属性: ThreadLocal.ThreadLocalMap;
     * ThreadLocalMap 是 ThreadLocal的一个静态内部类;
     * 底层是使用一个静态的Entry extends WeakReference
     * ThreadLocalMap中存储Entry >> ThreadLocal key || Object value;
     *
     * 弱引用和内存泄漏:
     * 1. 什么是内存泄漏: 不在被使用的变量但是占据着内存空间不使用;
     * 2. `WeakReference< ThreadLocal >` 两成包装成为一个Entry对象:
     * 为什么使用弱引用:
     *
     *
     */
    public static void main(String[] args) {
        // GCroot根可达机制以及四种引用
    }

    private static void phantom() {
        // 虚引用, 最弱的, 必须和引用队列一起使用() 在任何时候都可能会被回收;
        // get方法总是返回null, 更常作为一种通知机制;
        // 回收的时候收到一个系统通知, 比finalize更加的灵活
        // 回收之后会放入引用队列当中
        MyClass myClass = new MyClass();
        ReferenceQueue<MyClass> queue = new ReferenceQueue<>();
        PhantomReference<MyClass> phantomReference = new PhantomReference<>(myClass, queue);
        List<byte[]> list = new ArrayList<>();
        new Thread(() -> {
            while(true) {
                list.add(new byte[1024 * 1024]);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 永远是null
                System.out.println("phantomReference.get() = " + phantomReference.get());
            }
        }, "t1").start();
        new Thread(() -> {
            while(true) {
                Reference<? extends MyClass> poll = queue.poll();
                if(poll != null) {
                    System.out.println("虚引用被回收了");
                    break;
                }
            }
        }, "t2").start();
    }

    private static void wake() {
        // 不管够不够用, 只要gc了就回收
        WeakReference<MyClass> weakReference = new WeakReference<>(new MyClass());
        System.out.println(" --- before gc " + weakReference);
        System.gc();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(" --- after gc " + weakReference);
    }

    private static void soft() {
        // Map<String, SoftReference<Bitmap>> imageCache = new HashMap<>(); 避免OOM的内存优化;
        // 不够用才回收
        SoftReference<MyClass> softReference = new SoftReference<>(new MyClass());
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("after " + softReference);
        System.gc();
        try {
            byte[] bytes = new byte[20 * 1024 * 1024];
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            System.out.println("after " + softReference);
        }
    }

    private static void stronge() {
        MyClass myClass = new MyClass();
        // 强引用: 不会回收, 内存不足的时候不会回收它, 处于一个可达状态, 及时以后永远不会被用到, 也不会回收, 这也是内存泄漏的主要原因之一
        myClass = null; // 说明不在指向了, 回收 人工开启GC
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("gc after" + myClass);
    }

}
