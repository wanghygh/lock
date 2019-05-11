package wang.huaiyu.lock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wang.huaiyu.lock.service.DistributeLock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LockApplicationTests {

    @Autowired
    private DistributeLock distributeLock;

    @Test
    public void contextLoads() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        List<Integer> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i ++) {
            tasks.add(i + 1);
        }

        tasks.parallelStream().forEach(task -> executorService.execute(new Demo(task, distributeLock)));

        try {
            System.in.read();
        } catch (IOException e) {
            // DO NOTHING
        }
    }
}

class Demo implements Runnable {

    private int index;
    private DistributeLock distributeLock;

    public Demo(int index, DistributeLock distributeLock) {
        this.index = index;
        this.distributeLock = distributeLock;
    }

    @Override
    public void run() {
        boolean lock = distributeLock.lock("test", index + "");
        if (lock) {
            System.out.println(index + " do something");
            distributeLock.unlock("test", index + "");
        }
    }
}