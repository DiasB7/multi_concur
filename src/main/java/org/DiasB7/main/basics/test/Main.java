package org.DiasB7.main.basics.test;

public class Main {
    //Multithreading/oncurrency Test.Main idea - high performance app
    //app in multithread env, should utilize as much CPU as possible
    //threads - java client wrapper to communicate with OS, so it would provide opportunities for app to run on cores, scheduled properly, stack used
    //app in multhreaded - restrictions found by amdahl law S = 1/((1-P) + P/N); S= coef how much can be increased by, against 1; P = share of possible parallelism, N - cores
    //*To parallel IO bound, need few disks that would have full data copy by parts/halves
    //Many threads can load cpu and context switching become slower

    //echo $? 1 - error; 0 - return good
    //for external observer not considering jvm thread return, it considers only main return; main dont consider is child thread gracefully or throw exception

    //Java Thread is independent activity(general), set of instructions needed to be executed on CPU having: stack, local states, references to heap; associated to a process by OS
    //When thread created: JVM communicate with OS to associate its thread(s) with itself(process); thread with run method, stack;
    //When thread started: thread.start(), when scheduler consider effective, it schedules one of threads on some core;
    //scheduling is uncontrollable(OS based), working asynchronously, we can only ask to start; Scheduler considers when effective to start a thread on a core;
    //*affinity/tries - trick for jvm to ask OS to schedule execution of the thread on specific core, for potential optimization with cached values

    //JVM exits when all child non daemon threads have finished, from main thread
    //JVM dont care about daemon's 'finally', it still finishing after main
    //when part of program not needed to be exist when core shutdown, then daemon: like metrics, logging
    //Thread termination/interruption design should be correct to not let it execute forever; flag Thread.interrupted()
    //park() unpark() lock can move thread to waiting or from waiting

    //from WAITING or WAITING_TIMED it can be interrupted rustling in exception
    //during RUNNABLE need to have .interrupted() flag otherwise it runs forever, to have abrupt interruption do VT, they die right after main

    //someThread.join() - current thread waits for someThread to finish, even daemon
    //Thread.run() reusing the thread, start scheduling new real thread

    //CPU-BOUND = CPU, CPU-1, CPU*2, CPU*4
    //IO-BOUND = THREAD >> CPU_COUNT
    //Threads do very fast job then wait on io and etc

    public static void main(String[] args) throws InterruptedException {
        //Thread is slow and heavy: KB-MB (-Xss flag as args); Runnable passing fast and thin;
        //no run - silent die
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
            }
        });
        //not guarantee behavior: scheduler can make main, 0// 0, main
        thread.start();
        //faster than start, so almost always execute first
        System.out.println(Thread.currentThread().getName());
        //start is long
        Thread.sleep(200);

        //States NEW -> RUNNABLE -> BLOCKED//WAITING//TIMED_WAITING -> RUNNABLE -> TERMINATED
        //NEW - instance created, start not yet completed, not yet scheduled
        //RUNNABLE - after start() or other states, means its connected to OS and possibly currently running in CPU or waiting to be scheduled
        //BLOCKED - ready and waiting for lock to be released, not interruptable; not blocked by resource, IO etc. VT dont really go BLOCKED;
        //WAITING/TIMED_WAITING - thread is sleeping, can be interrupted
        //TERMINATED - ends when run() over, it cant be reused again

        //*LOCK FREE/no blocking - high cpu usage;;; BLOCKING approach - ordered,low cpu

        //getState dont have guarantee, happens-before/ called from another thread not reliable
        Thread thread1 = new Thread();
        System.out.println(thread1.getState());

        long start = System.nanoTime();
        //vars passed to anonym or lambda are effectively final and cant be changed inside; for thread: include in thread class or create shared instance or Atomic;
        Thread thread2 = new Thread(() -> {while (true){if (System.nanoTime() - start >= 2_000_000_000L) {break;}}});
        thread2.start();
        Thread.sleep(500);
        System.out.println(thread2.getState());

        Thread childThread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("keks");
            }
        });
        childThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("one excppt: " + Thread.currentThread().getName() + " " + e.getMessage());
            }
        });
        childThread.start();

    }
}
