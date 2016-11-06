package ru.sbrf.study;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Bulat on 28.09.2016.
 */
public class SynchronizedPingPong implements Runnable {
    private static final Object sync = new Object();
    private static Integer AMOUNT_OF_THREADS;
    private static Integer COUNT;
    private static volatile int turns = 0;
    private static ExecutorService executor;
    private final Integer threadId;

    public SynchronizedPingPong(Integer threadId) {
        this.threadId = threadId;
    }

    public static void main(String[] args) {
        AMOUNT_OF_THREADS = Integer.decode(args[0]);
        COUNT = Integer.decode(args[1]);
        executor = Executors.newFixedThreadPool(AMOUNT_OF_THREADS);
        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            executor.submit(new SynchronizedPingPong(i));
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (sync) {
                if (getThreadId().equals(turns % AMOUNT_OF_THREADS)) {
                    if (COUNT.equals(turns)) {
                        System.out.println("Turns: " + turns + ". ENDED " + getThreadId());
                        executor.shutdownNow();
                        return;
                    }
                    turns++;
                    sync.notifyAll();
                } else {
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        if (executor.isShutdown()) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public Integer getThreadId() {
        return threadId;
    }
}
