package com.tyz.spring_aop.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy {
    private Invoker invoker;

    public CglibProxy(Invoker invoker) {
        this.invoker = invoker;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Object object) {
        Class<?> clazz = object.getClass();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                return invoker.invoke(object, method, args);
            }
        });
        return (T) enhancer.create();
    }
}
