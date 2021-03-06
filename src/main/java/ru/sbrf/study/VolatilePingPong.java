package ru.sbrf.study;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * Created by Bulat on 28.09.2016.
 */
public class VolatilePingPong implements Runnable {
    private static Integer AMOUNT_OF_THREADS;
    private static Integer COUNT;
    private static volatile int turns = 0;
    private static ExecutorService executor;
    private static Long startTime;
    private static Long finishedTime;
    private final Integer threadId;

    public VolatilePingPong(Integer threadId) {
        this.threadId = threadId;
    }

    public static void main(String[] args) {
        AMOUNT_OF_THREADS = Integer.decode(args[0]);
        COUNT = Integer.decode(args[1]);
        executor = Executors.newFixedThreadPool(AMOUNT_OF_THREADS);
        addShutdownHook(args);
        start();
        for (int i = 0; i < AMOUNT_OF_THREADS; i++) {
            executor.submit(new VolatilePingPong(i));
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
                        os = Files.newOutputStream(Paths.get(args[2]), APPEND);
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
            if (Thread.interrupted()) {
                return;
            }
            if (isMyTurn()) {
                if (COUNT.equals(turns)) {
                    System.out.println("Turns: " + turns + ". ENDED: " + getThreadId());
                    executor.shutdownNow();
                    end();
                }
                turns++;
            } else {
                Thread.yield();
            }
        }
    }

    private boolean isMyTurn() {
        return (getThreadId().equals(turns % AMOUNT_OF_THREADS));
    }

    public Integer getThreadId() {
        return threadId;
    }
}
