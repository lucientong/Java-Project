package com.tyz.csframework.actionbean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 构造一个关于action的Bean，这是要要执行用户定义的action
 * 所映射的方法，需要的参数描述。
 *
 * @author tyz
 */
public class ActionBeanDefinition {
    /** 方法被反射执行所需要的对象 */
    private Object object;

    /** 方法被反射执行所需要的方法 */
    private Method method;

    /** 方法被反射执行所需要的参数列表 */
    private List<ParameterDefinition> parameterList;

    ActionBeanDefinition(Object object) {
        this(object, null);
    }

    ActionBeanDefinition(Object object, Method method) {
        this.object = object;
        this.method = method;
        this.parameterList = new ArrayList<>();
    }

    /**
     * 在参数列表 {@code parameterList} 中加入一个新的参数
     *
     * @param pd 注入好的一个参数对象 {@link ParameterDefinition}
     */
    void addParameter(ParameterDefinition pd) {
        this.parameterList.add(pd);
    }

    /**
     * 获取参数的类型列表
     *
     * @return 参数的类型列表
     */
    Class<?>[] getParameterTypes() {
        if (this.parameterList.isEmpty()) {
            return new Class<?>[] {};
        }
        Class<?>[] types = new Class[this.parameterList.size()];

        int index = 0;

        for (ParameterDefinition pd : this.parameterList) {
            types[index++] = (Class<?>) pd.getType();
        }
        return types;
    }

    /**
     * @return 返回bean中的对象
     */
    Object getObject() {
        return object;
    }

    /**
     * @return 返回bean中的方法
     */
    Method getMethod() {
        return method;
    }

    /**
     * 设置bean中的方法
     *
     * @param method 要设置的方法
     */
    void setMethod(Method method) {
        this.method = method;
    }

    /**
     * 得到bean中的参数列表
     *
     * @return 参数列表
     */
    List<ParameterDefinition> getParameterList() {
        return parameterList;
    }
}
