package ru.sbrf.study;

/**
 * Created by Bulat on 28.09.2016.
 */
public abstract class PingPong implements Runnable{
    public static final String PING = "PING";
    public static final String PONG = "PONG";
    private final String nameOfThread;
    public PingPong(String nameOfThread) {
        this.nameOfThread = nameOfThread;
    }

    protected void printMessage() {
        System.out.println(nameOfThread);
    }

    public String getNameOfThread() {
        return nameOfThread;
    }
}
