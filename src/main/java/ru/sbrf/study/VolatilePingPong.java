package ru.sbrf.study;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Bulat on 28.09.2016.
 */
public class VolatilePingPong extends PingPong {
    private static Integer AMOUNT_OF_THREADS;
    private static Integer COUNT;
    private static volatile int turns = 0;
    private static ExecutorService executor;

    public VolatilePingPong(Integer threadId) {
        super(threadId);
    }

    public static void main(String[] args) {
        AMOUNT_OF_THREADS = Integer.decode(args[0]);
        COUNT = Integer.decode(args[1]);
        executor = Executors.newFixedThreadPool(AMOUNT_OF_THREADS);
        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            executor.submit(new VolatilePingPong(i));
        }
    }

    @Override
    public void run() {
        while (true) {
            if (Thread.interrupted()) {
                return;
            }
            if (isMyTurn()) {
                if (COUNT.equals(turns)) {
                    System.out.println("Turns: " + turns + ". ENDED: " + getThreadId());
                    executor.shutdownNow();
                }
                turns++;
            } else {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    if (executor.isShutdown()) {
                        return;
                    }
                }
            }
        }
    }

    private boolean isMyTurn() {
        return (getThreadId().equals(turns % AMOUNT_OF_THREADS));
    }

}
