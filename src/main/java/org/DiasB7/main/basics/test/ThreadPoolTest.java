package org.DiasB7.main.basics.test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadPoolTest {
    Queue<Runnable> tasks;
    volatile boolean shutdown;
    int poolSize;

    public ThreadPoolTest(int poolSize) {
        this.tasks = new ConcurrentLinkedQueue<>();
        this.shutdown = false;
        this.poolSize = poolSize;
    }

    public void start() {
        for (int i = 0; i < poolSize; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!shutdown) {
                        Runnable task = tasks.poll();
                        if (task != null) task.run();
                    }
                }
            });
            thread.start();
        }
    }

    public void submit(Runnable task) {
        this.tasks.add(task);
    }

    public void shutdown() {
        this.shutdown = true;
    }

    static void main() throws InterruptedException {
        ThreadPoolTest poolTest = new ThreadPoolTest(Runtime.getRuntime().availableProcessors());
        poolTest.start();

        long start = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            poolTest.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(finalI + " task is running on " + Thread.currentThread().getName());
                }
            });
        }
        Thread.sleep(1000);
        poolTest.shutdown();
        long elapsedNs = System.nanoTime() - start;
        System.out.printf("executed for: %.3f ms%n", elapsedNs / 1_000_000.0);
    }

}
