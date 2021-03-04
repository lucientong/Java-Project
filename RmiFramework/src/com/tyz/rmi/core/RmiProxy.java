package com.tyz.rmi.core;

import java.lang.reflect.Proxy;

/**
 * 实现RMI中的代理
 *
 * @author tyz
 */
public class RmiProxy {

    private RmiClient rmiClient;

    public RmiProxy(RmiClient rmiClient) {
        this.rmiClient = rmiClient;
    }

    /**
     * 调用 RmiClient 与服务器建立短链接，最终通过服务器得到一个代理
     * @param clazz 需要被代理的接口
     * @return 返回一个代理类
     */
    @SuppressWarnings("unchecked")
    public  <T> T getProxy(Class<?> clazz) {
        if (!clazz.isInterface()) {
            throw new ClassTypeException("Class [" +
                    clazz.getName() + "] must be interface.");
        }
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?>[] interfaces = new Class<?>[] {clazz};

        return (T) Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            return this.rmiClient.methodInvoker(method, args);
        });
    }
}
