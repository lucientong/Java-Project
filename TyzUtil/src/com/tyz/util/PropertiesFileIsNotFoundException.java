package com.tyz.util;

public class PropertiesFileIsNotFoundException extends Exception {
    public PropertiesFileIsNotFoundException() {
    }

    public PropertiesFileIsNotFoundException(String message) {
        super(message);
    }

    public PropertiesFileIsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertiesFileIsNotFoundException(Throwable cause) {
        super(cause);
    }

    public PropertiesFileIsNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
