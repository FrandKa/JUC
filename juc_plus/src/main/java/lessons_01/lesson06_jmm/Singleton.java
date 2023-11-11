package lessons_01.lesson06_jmm;

public class Singleton {
    private static volatile Singleton instance;

    // 私有构造方法，防止外部实例化
    private Singleton() {}

    // 公共方法获取实例
    public static Singleton getInstance() {
        // 第一次检查，如果实例已经存在，直接返回
        if (instance == null) {
            // 同步块，确保只有一个线程能够进入创建实例的代码块
            synchronized (Singleton.class) {
                // 第二次检查，因为可能有多个线程在第一次检查通过后等待锁
                if (instance == null) {
                    // 创建实例
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}