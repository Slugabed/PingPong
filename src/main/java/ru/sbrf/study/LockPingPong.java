package ru.sbrf.study;

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
        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            executor.submit(new LockPingPong(i));
        }
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (getThreadId().equals(turns % AMOUNT_OF_THREADS)) {
                if (COUNT.equals(turns)) {
                    System.out.println("Turns: " + turns + ". ENDED " + getThreadId());
                    executor.shutdownNow();
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
