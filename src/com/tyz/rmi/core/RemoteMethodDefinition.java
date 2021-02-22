package com.tyz.rmi.core;

import java.lang.reflect.Method;

/**
 * 这里定义两个成员变量，object 和 method,对应着执行一个方法所需要的两个参数。
 * 但是实际上，一个接口对应的实现类里的所有方法，都可以使用同一个对象，object
 * 可以设置成单例的。在实现上，可以用两个散列表，键都为客户端传进服务端的方法编
 * 码，值分别映射为对象和方法，但是这也是冗余了。所以我们索性不对对象采用单例的
 * 实现，因为每个索引都是一个指针，并不占用太多的空间，这部分空间可以被浪费掉。
 *
 * @author tyz
 */
public class RemoteMethodDefinition {
    private Object object;
    private Method method;

    public RemoteMethodDefinition(Object object, Method method) {
        this.object = object;
        this.method = method;
    }

    public Object getObject() {
        return object;
    }

    public Method getMethod() {
        return method;
    }
}
