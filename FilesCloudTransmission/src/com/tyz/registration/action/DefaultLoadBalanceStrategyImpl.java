package com.tyz.registration.action;

import com.tyz.registration.information.OwnerInformation;

/**
 * 注册中心的默认负载均衡策略
 *
 * @author tyz
 */
public class DefaultLoadBalanceStrategyImpl implements ILoadBalanceStrategy {
    /** 健康值最大阈值 */
    public static final int MAX_HEALTH_VALUE = 5;

    @Override
    public boolean isResourceOwnerValid(OwnerInformation ownerInformation) {
        return ownerInformation.getHealthValue() < MAX_HEALTH_VALUE;
    }
}
