package com.tyz.spring_ioc.exception;

public class SetterMethodNotFoundException extends Exception {
    public SetterMethodNotFoundException() {
    }

    public SetterMethodNotFoundException(String message) {
        super(message);
    }

    public SetterMethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetterMethodNotFoundException(Throwable cause) {
        super(cause);
    }

    public SetterMethodNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
