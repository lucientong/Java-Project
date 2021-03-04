package com.tyz.util;

/**
 * 当使用 {@link BytesTranslator} 将byte数组转换为别的类型
 * 的值时，数组长度不合法时所发生的异常。
 *
 * @author tyz
 */
public class InvalidBytesNumberException extends RuntimeException {
    public InvalidBytesNumberException() {
    }

    public InvalidBytesNumberException(String message) {
        super(message);
    }

    public InvalidBytesNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBytesNumberException(Throwable cause) {
        super(cause);
    }

    public InvalidBytesNumberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
