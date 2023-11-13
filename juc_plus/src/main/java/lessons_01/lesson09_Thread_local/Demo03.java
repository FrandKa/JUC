package lessons_01.lesson09_Thread_local;

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
        MyClass myClass = new MyClass();
        // 强引用: 不会回收, 内存不足的时候不会回收它, 处于一个可达状态, 及时以后永远不会被用到, 也不会回收, 这也是内存泄漏的主要原因之一
        System.out.println("gc before " + myClass);
        myClass = null; // 说明不在指向了, 回收 人工开启GC
    }

}
