package ru.sbrf.study;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Bulat on 30.09.2016.
 */
public class LockPingPong extends PingPong {
    static Lock lock = new ReentrantLock();
    static Condition PONGLock = lock.newCondition();
    static boolean isPINGTurn = true;

    public LockPingPong(String nameOfThread) {
        super(nameOfThread);
    }

    public static void main(String[] args) {
        new Thread(new LockPingPong(PING)).start();
        new Thread(new LockPingPong(PONG)).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                lock.lock();
                if (PING.equals(getNameOfThread()) && isPINGTurn) {
                    printMessage();
                    isPINGTurn = false;
                    PONGLock.signal();
                } else if (PONG.equals(getNameOfThread())) {
                    if (!isPINGTurn) {
                        printMessage();
                        isPINGTurn = true;
                    } else if (isPINGTurn) {
                        try {
                            PONGLock.await();
                        } catch (InterruptedException e) {
                            //ignore
                        }
                    }

                }
                lock.unlock();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    //ignore
                }
            } finally {
            }
        }
    }
}
