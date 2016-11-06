package ru.sbrf.study;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Bulat on 30.09.2016.
 */
public class LockPingPong implements Runnable {
    private final static List<Condition> locks = new ArrayList<>();
    private static Lock lock = new ReentrantLock();
    private static Integer AMOUNT_OF_THREADS;
    private static Integer COUNT;
    private static volatile int turns = 0;
    private static ExecutorService executor;
    private static Long startTime;
    private static Long finishedTime;
    private final Integer threadId;

    public LockPingPong(Integer threadId) {
        this.threadId = threadId;
    }

    public static void main(String[] args) {
        AMOUNT_OF_THREADS = Integer.decode(args[0]);
        COUNT = Integer.decode(args[1]);
        executor = Executors.newFixedThreadPool(AMOUNT_OF_THREADS);
        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            locks.add(lock.newCondition());
        }
        addShutdownHook(args);
        start();
        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            executor.submit(new LockPingPong(i));
        }
    }

    private static void start() {
        startTime = System.currentTimeMillis();
    }

    private static void end() {
        finishedTime = System.currentTimeMillis();
    }

    private static void addShutdownHook(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                OutputStream os = null;
                if (args.length == 2) {
                    os = System.out;
                } else {
                    try {
                        os = Files.newOutputStream(Paths.get(args[2]));
                    } catch (IOException e) {
                        System.out.println("Error " + e.getMessage());
                        os = System.out;
                    }
                }
                PrintStream out = new PrintStream(os);
                out.print(((double) finishedTime - startTime) / COUNT);
                out.print("\t" + AMOUNT_OF_THREADS);
                out.print("\t" + COUNT);
                out.println("\t" + (finishedTime - startTime));
                try {
                    os.close();
                } catch (IOException e) {
                    //ignore
                }
                out.close();
            }
        });
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (getThreadId().equals(turns % AMOUNT_OF_THREADS)) {
                if (COUNT.equals(turns)) {
                    System.out.println("Turns: " + turns + ". ENDED " + getThreadId());
                    executor.shutdownNow();
                    end();
                    lock.unlock();
                    return;
                }
                locks.get((getThreadId() + 1) % AMOUNT_OF_THREADS).signal();
                turns++;
                lock.unlock();
            } else {
                try {
                    locks.get(getThreadId()).await();
                    lock.unlock();
                } catch (InterruptedException e) {
                    if (executor.isShutdown()) {
                        return;
                    }
                }
            }
        }
    }

    public Integer getThreadId() {
        return threadId;
    }
}
