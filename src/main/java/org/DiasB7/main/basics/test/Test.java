package org.DiasB7.main.basics.test;

import java.util.concurrent.locks.LockSupport;

public class Test {

    static void main() throws InterruptedException {
        Thread childThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("parking");
                System.out.println("Thread state = " + Thread.currentThread().getState());
                LockSupport.park();
                System.out.println("unparked");
                System.out.println("Thread state = " + Thread.currentThread().getState());
            }
        });
        childThread.start();

        Thread.sleep(1000);
        System.out.println("Thread state = " + childThread.getState());
        LockSupport.unpark(childThread);
        //happens-before, this getState() cant get latest state of the thread
        System.out.println("Thread state = " + childThread.getState());
    }

    static void main1() throws InterruptedException {
        Thread childThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    System.out.println("Printing...");
                }
            }
        });
        childThread.start();

        Thread.sleep(1000);
        childThread.interrupt();
    }

}
