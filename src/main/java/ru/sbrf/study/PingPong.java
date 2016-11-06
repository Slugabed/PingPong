package ru.sbrf.study;

/**
 * Created by Bulat on 28.09.2016.
 */
abstract class PingPong implements Runnable {
    private final Integer threadId;

    public PingPong(Integer threadId) {
        this.threadId = threadId;
    }

    @Deprecated
    protected void printMessage() {
        System.out.println("I am thread " + threadId);
    }

    public Integer getThreadId() {
        return threadId;
    }
}
