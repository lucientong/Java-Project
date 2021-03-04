package com.tyz.spring_aop.proxy;

import java.lang.reflect.Method;

public interface IInterceptor {
    boolean before(Object[] args);
    Object after(Object result);
}
