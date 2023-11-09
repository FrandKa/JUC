package com.kakaowo.juc_base.demo.demo01;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demo01 {
    public static void main(String[] args) {
        HelloResource helloResource = new HelloResource();
        for (int i = 0; i < HelloResource.HELLO.length(); i++) {
            new Thread(helloResource::print, String.valueOf(i)).start();
        }
    }
}

class HelloResource {
    public static final String HELLO = "hello_world_and_the_string_is_very_long_abcdefghigklmn";
    private final Lock lock = new ReentrantLock(true);
    private List<Condition> conditions = new ArrayList<>();
    private static int index = 0;

    public HelloResource() {
        for (int i = 0; i < HELLO.length(); i++) {
            conditions.add(lock.newCondition());
        }
    }

    public void print() {
        lock.lock();
        try {
            int i = Integer.parseInt(Thread.currentThread().getName());
            while(i != index) {
                conditions.get(i).await();
            }
            System.out.print(HELLO.charAt(i));
            ++index;
            if(index < HELLO.length()) {
                conditions.get(index).signal();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}