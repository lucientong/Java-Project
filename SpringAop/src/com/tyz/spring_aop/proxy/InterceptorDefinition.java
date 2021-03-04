package com.tyz.spring_aop.proxy;

import org.omg.PortableInterceptor.Interceptor;

import java.lang.reflect.Method;

public class InterceptorDefinition {
    private String condition;
    private IInterceptor interceptor;

    InterceptorDefinition(String condition, IInterceptor interceptor) {
        this.condition = condition;
        this.interceptor = interceptor;
    }

    IInterceptor getInterceptor() {
        return interceptor;
    }

    boolean isTarget(Method method) {
        //TODO do some detection
        return true;
    }
}
