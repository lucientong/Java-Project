package com.tyz.timer.core;

public interface IAssignment extends Runnable {
    @Override
    default void run() {
        excute();
    }

    void excute();
}
