package com.tyz.spring_aop.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invoker {
    private InterceptorDefinition interceptor;
    private Invoker next;

    public Invoker(InterceptorDefinition interceptor) {
        this.interceptor = interceptor;
        this.next = null;
    }

    public Invoker(InterceptorDefinition interceptor, Invoker next) {
        this.interceptor = interceptor;
        this.next = next;
    }

    Object invoke(Object object, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Object result = null;

        boolean isTarget = interceptor.isTarget(method);
        if (!isTarget || interceptor.getInterceptor().before(args)) {
            if (this.next != null) {
                this.next.invoke(object, method, args);
            } else {
                result = method.invoke(object, args);
            }
            if (isTarget) {
                result = this.interceptor.getInterceptor().after(result);
            }
        }
        return result;
    }

}
