package lessons_01.lesson02;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @program: juc_part
 * @description:
 * @author: Mr.Ka
 * @create: 2023-11-10 13:42
 **/

public class CompletableFutureDemo02 {
    // Runnable: () -> void // run()
    // Function: (a) -> a // apply()
    // Consumer (a) -> void : BiConsumer: (a, b) -> void; // accept()
    // supplier () -> a; // get();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            return "hello";
        });

        String s = completableFuture.join(); // 与get几乎一致, 区别就是编译的时候不会爆出异常;

        System.out.println("s = " + s);

    }

}

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
class Student {
    private Integer id;
    private String name;
    private String major;
}