package com.tyz.spring_ioc.exception;

/**
 * 运行时异常和普通异常的差别在于，运行时异常不需要抛异常，只有在异常
 * 发生时才能获取到异常。
 */
public class BeanNotFoundException extends RuntimeException {
    public BeanNotFoundException() {
    }

    public BeanNotFoundException(String message) {
        super(message);
    }

    public BeanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanNotFoundException(Throwable cause) {
        super(cause);
    }

    public BeanNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
