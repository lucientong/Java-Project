package com.tyz.csframework.annotation;

/**
 * @author tyz
 */
public class LostAnnotationException extends Exception {
    public LostAnnotationException() {
    }

    public LostAnnotationException(String message) {
        super(message);
    }

    public LostAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LostAnnotationException(Throwable cause) {
        super(cause);
    }

    public LostAnnotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
