package com.tyz.registration.action;

import com.tyz.registration.information.OwnerInformation;

/**
 * 注册中心实现资源发送端的负载均衡策略
 *
 * @author tyz
 */
public interface ILoadBalanceStrategy {
    /**
     * 检测资源拥有者的信息，判断是否可以作为发送端
     *
     * @param ownerInformation 资源拥有者信息
     * @return 若该资源拥有者可以作为发送端则返回true
     */
    boolean isResourceOwnerValid(OwnerInformation ownerInformation);
}
