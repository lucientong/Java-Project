package com.tyz.timer.core;

public class Timer implements Runnable {
    public static long DEFAULT_DELAY_TIME = 1000;

    private long delayTime;
    private volatile boolean isRunning;
    private IAssignment assignment;

    public Timer() {
        this.delayTime = DEFAULT_DELAY_TIME;
    }

    public Timer setAssignment(IAssignment assignment) {
        this.assignment = assignment;
        return this;
    }

    public Timer setDelayTime(long delayTime) {
        this.delayTime = delayTime;
        return this;
    }

    public void startUp() {
        this.isRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        this.isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Thread.sleep(this.delayTime);
                new Thread(this.assignment).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
