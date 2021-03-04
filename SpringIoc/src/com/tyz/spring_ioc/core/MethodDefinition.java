package com.tyz.spring_ioc.core;

import java.lang.reflect.Method;

/**
 * 方法的定义，为处理带参的方法做准备
 */
public class MethodDefinition {
    private Object object;
    private Method method;

    MethodDefinition(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    Object getObject() {
        return object;
    }

    Method getMethod() {
        return method;
    }
}
