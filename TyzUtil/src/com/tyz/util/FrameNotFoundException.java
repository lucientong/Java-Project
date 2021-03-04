package com.tyz.util;

public class FrameNotFoundException extends Exception {
    public FrameNotFoundException() {
    }

    public FrameNotFoundException(String message) {
        super(message);
    }

    public FrameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameNotFoundException(Throwable cause) {
        super(cause);
    }

    public FrameNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
