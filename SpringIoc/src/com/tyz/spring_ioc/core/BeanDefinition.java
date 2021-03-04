package com.tyz.spring_ioc.core;

/**
 * 定义一个Bean，由于要使用懒汉模式实现DI，这里需要对每一个单例bean
 * 判断它有没有完成注入，如果完成了，就不要第二次注入。
 */
public class BeanDefinition {
    private Class<?> clazz;
    private Object object;
    private volatile boolean isInjected;

    Class<?> getClazz() {
        return clazz;
    }

    void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    Object getObject() {
        return object;
    }

    void setObject(Object object) {
        this.object = object;
    }

    boolean isInjected() {
        return isInjected;
    }

    void setInjected(boolean injected) {
        isInjected = injected;
    }
}
