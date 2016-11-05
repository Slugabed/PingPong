package ru.sbrf.study;

/**
 * Created by Bulat on 28.09.2016.
 */
public class SynchronizedPingPong extends PingPong {
    static Object sync = new Object();
    static boolean isPINGTurn = true;

    public SynchronizedPingPong(String message) {
        super(message);
    }

    public static void main(String[] args) {
        new Thread(new SynchronizedPingPong(PING)).start();
        new Thread(new SynchronizedPingPong(PONG)).start();
    }

    @Override
    public void run() {
        while (true) {
            synchronized (sync) {
                if (PING.equals(getNameOfThread()) && isPINGTurn) {
                    printMessage();
                    isPINGTurn = false;
                } else if (PONG.equals(getNameOfThread()) && !isPINGTurn) {
                    printMessage();
                    isPINGTurn = true;
                }
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }
}
