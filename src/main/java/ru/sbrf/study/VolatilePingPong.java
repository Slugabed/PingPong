package ru.sbrf.study;

/**
 * Created by Bulat on 28.09.2016.
 */
public class VolatilePingPong extends PingPong {
    private static volatile int sync = 0;

    public VolatilePingPong(String message) {
        super(message);
    }

    public static void main(String[] args) {
        new Thread(new VolatilePingPong(PING)).start();
        new Thread(new VolatilePingPong(PONG)).start();
    }


    @Override
    public void run() {
        while (true) {
            if (PING.equals(getNameOfThread())) {
                if (0 == sync % 2) {
                    printMessage();
                    sync++;
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    //ignore
                }
            } else if (PONG.equals(getNameOfThread())) {
                if (1 == sync % 2) {
                    printMessage();
                    sync++;
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    //ignore
                }
            } else {
                throw new Error("???");
            }
        }
    }
}
