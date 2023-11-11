package lessons_01.lesson07_CAS.Atomic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-11 18:20
 **/
@Data
@AllArgsConstructor
class User {
    private String name;
    private int age;
}

public class Demo01 {
    public static void main(String[] args) {
        AtomicReference<User> atomicReference = new AtomicReference<>();
        User user1 = new User("zs", 22);
        User user2 = new User("ls", 22);

        atomicReference.set(user1);
        System.out.println(atomicReference.compareAndSet(user1, user2) + "   " + atomicReference.get());
        System.out.println(atomicReference.compareAndSet(user1, user2) + "   " + atomicReference.get());
    }
}
