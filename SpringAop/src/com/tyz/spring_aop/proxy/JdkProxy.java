package com.tyz.spring_aop.proxy;

import java.lang.reflect.Proxy;

public class JdkProxy {
    private Invoker invoker;

    public JdkProxy(Invoker invoker) {
        this.invoker = invoker;
    }

    public Object getProxy(Object object) {
        Class<?> clazz = object.getClass();
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?>[] interfaces = clazz.getInterfaces();

        return Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            return this.invoker.invoke(object, method, args);
        });
    }
}
