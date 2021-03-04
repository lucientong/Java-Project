package com.tyz.transmission.requester;

import com.tyz.registration.information.OwnerInformation;

import java.util.List;

/**
 * 定义资源请求者的分发策略
 *
 * @author tyz
 */
public interface IDistributionStrategy {
    /**
     * 实现分发策略，从可以发送的资源拥有者列表中选出一部分合适的资源拥有者
     *
     * @param ownerList 符合要求的资源拥有者信息列表
     * @return 选择好的资源拥有者信息列表
     */
    List<OwnerInformation> selectProperResourceOwner(List<OwnerInformation> ownerList);
}
