package com.tyz.spring_aop.proxy;

public class UnionProxy {
    private static final int CGLIB_PROXY = 1;

    private Invoker invokerHead;
    private int proxyType;

    /**
     * 配置不同的代理模式
     * @param proxyType 值为1时为CGLibProxy，其他值(proxyType <= 2^31-1)
     *                  为JDKProxy代理模式
     */
    public UnionProxy(int proxyType) {
        this.proxyType = proxyType;
        this.invokerHead = null;
    }

    public void addInterceptor(InterceptorDefinition interceptor) {
        if (this.invokerHead == null) {
            this.invokerHead = new Invoker(interceptor);
        } else {
            this.invokerHead = new Invoker(interceptor, invokerHead);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Object object) {
        if (this.invokerHead == null) {
            return null;
        }
        if (this.proxyType == CGLIB_PROXY) {
            return (T) new CglibProxy(this.invokerHead).getProxy(object);
        } else {
            return (T) new JdkProxy(this.invokerHead).getProxy(object);
        }
    }
}
