package com.tyz.nio.actionbean;

import com.tyz.util.ArgumentMaker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 在用户没有对 {@link ISessionProcessor} 的响应以及请求方法进行覆盖的
 * 条件下，默认调用此类实现好的响应和请求的方法，通过扫描用户注册
 * 的 {@code action} 和 {@code abd} 映射，根据 {@code action}
 * 得到方法的封装类 {@link ActionBeanDefinition}，从而执行映射
 * 的方法。
 *
 * @author tyz
 */
public class DefaultSessionImpl implements ISessionProcessor {
    /**
     * 处理客户端的请求，并将响应的结果转换成json对象返回
     *
     * @param action 请求的行为
     * @param parameter 参数
     * @throws BeanNotExistException {@code action} 映射的bean不存在
     * @throws InvocationTargetException 反射调用方法失败
     */
    @Override
    public void dealRequest(String action, String parameter) throws BeanNotExistException, InvocationTargetException, IllegalAccessException {
        ActionBeanDefinition bean = ActionBeanFactory.getActionBeanDefinition(action);

        if (bean == null) {
            throw new BeanNotExistException("Action [" +
                    action + "] didn't have method to invoke.");
        }
        Object object = bean.getObject();
        Method method = bean.getMethod();
        Object[] args = getParameters(parameter, method, bean);

        method.invoke(object, args);
    }

    /**
     * 客户端处理服务器的响应
     *
     * @param action 客户端请求的行为
     * @param parameter 参数
     * @throws Exception 参数不合法或者 {@code action} 无映射
     */
    @Override
    public void dealResponse(String action, String parameter) throws Exception {
        dealRequest(action, parameter);
    }

    /**
     * 根据 {@code parameter} 解析出所有参数的值
     *
     * @param parameter 经 {@link ArgumentMaker} 编码的参数列表
     * @param method 需要执行的方法
     * @param bean action映射的bean
     * @return {@code method} 的所有参数的值
     */
    private Object[] getParameters(String parameter, Method method, ActionBeanDefinition bean) {
        ArgumentMaker argumentMaker = new ArgumentMaker(parameter);

        Parameter[] parameters = method.getParameters();
        if (parameters.length <= 0) {
            return new Object[] {};
        }

        Object[] result = new Object[parameters.length];
        List<ParameterDefinition> parameterList = bean.getParameterList();

        for (int index = 0; index < parameters.length; index++) {
            Type type = parameters[index].getParameterizedType();
            String name = parameterList.get(index).getName();
            result[index] = argumentMaker.getArgument(name, type);
        }

        return result;
    }
}
