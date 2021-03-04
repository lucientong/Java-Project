package com.tyz.csframework.actionbean;

/**
 * 由于客户端的请求和服务器的响应逻辑都是使用反射机制，
 * 调用用户通过 {@link com.tyz.csframework.annotation.Action}
 * 注册的方法，所以将两个行为整合在一起，并默认通过
 * {@link DefaultSessionImpl} 实现该反射逻辑。
 *
 * @author tyz
 */
public interface ISessionProcessor {
    /**
     * 客户端向服务器发出请求
     *
     * @param action 请求的行为
     * @param parameter 参数
     * @return String 返回服务器响应的结果
     * @throws Exception 执行反射方法或者未找到 {@code action} 的映射
     */
    String dealRequest(String action, String parameter) throws Exception;

    /**
     * 服务器处理客户端的请求
     *
     * @param action 客户端请求的行为
     * @param parameter 参数
     * @throws Exception 执行反射方法或者未找到 {@code action} 的映射
     */
    void dealResponse(String action, String parameter) throws Exception;
}
