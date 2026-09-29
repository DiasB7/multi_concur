package org.DiasB7.main.basics.test;

import java.util.concurrent.*;

public class PoolMain {
    //Fixed size thread pool, CPU BOUND
    //cahced pool increasing, after time to leave threads dying, less memory, new thread creation takes time and heavy
    //Fork Join Pool: increasing, defeault threads increasing if needed, one thread can take job from another. CPU BOUND
    //Scheduler Pool
    //single thread pool: all operations like pool but thread safe

    //virtual thread: working on carrier thread

    //only pool no thread creation, if cpu bound then cpu count, if io when not loading cpu then many threads -- thread pool
    // IO = virtual threads; CPU = FIxed sized pool or Fork Join pool


    //Availabilit: if server always and instantly replies with denies and etc without waiting or hung
    //COnsistenc: if server replies correctly

    static void main() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 100; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);


        //how to keep loading always high: server cant keep processing of 10k requests per sec and etc, requests growing, waiting increasing
        //limit pool, so on 1001th request throw exception:
        //AbortPolicy (reject exception), Discard Policy (silent ignroing, deleting tasks), discard oldest policy, caller runs (run in current thread)
        //needed to defend from ddos, and in case exceptions sent
        //custom pool
        ExecutorService poolWithBackPressure = new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

        ExecutorService executorService1 = Executors.newFixedThreadPool(1, new MyThread());
        //without waiting = execute
        //with waiting/asynch = submit
        Future<?> result = executorService.submit(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });

        //callback
        try {
            result.get();
        } catch (ExecutionException e) {
            System.out.println("exception");
        }
    }

    static class MyThread implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread childThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    throw new RuntimeException("keks1");
                }
            });
            childThread.setUncaughtExceptionHandler((t, e) -> System.out.println("two excppt: " + t.getName() + " " + e.getMessage()));
            return childThread;
        }
    }
}
