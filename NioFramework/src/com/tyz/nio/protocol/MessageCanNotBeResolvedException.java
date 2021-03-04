package com.tyz.nio.protocol;

/**
 * 当端口之间传递的信息无法被解析时发生的异常
 * @author tyz
 */
public class MessageCanNotBeResolvedException extends Exception {
    public MessageCanNotBeResolvedException() {
    }

    public MessageCanNotBeResolvedException(String message) {
        super(message);
    }

    public MessageCanNotBeResolvedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageCanNotBeResolvedException(Throwable cause) {
        super(cause);
    }

    public MessageCanNotBeResolvedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
