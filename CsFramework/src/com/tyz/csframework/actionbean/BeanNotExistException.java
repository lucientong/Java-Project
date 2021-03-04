package com.tyz.csframework.actionbean;

/**
 * 当在 {@link ActionBeanFactory} 中未找到相应的
 * {@link ActionBeanDefinition} 对象时发生次异常
 *
 * @author tyz
 */
public class BeanNotExistException extends Exception {
    public BeanNotExistException() {
    }

    public BeanNotExistException(String message) {
        super(message);
    }

    public BeanNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanNotExistException(Throwable cause) {
        super(cause);
    }

    public BeanNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
