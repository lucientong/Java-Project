package com.tyz.transmission.requester;

import java.util.Map;

/**
 * 定义资源接收者中用户可以自行配置的功能
 *
 * @author tyz
 */
public interface IRequesterAction {
    /**
     * 处理注册中心发来的资源名称与资源编号的映射表
     *
     * @param resourceNameMap 资源表
     */
    void dealResourceNameMap(Map<String, Integer> resourceNameMap);
}
