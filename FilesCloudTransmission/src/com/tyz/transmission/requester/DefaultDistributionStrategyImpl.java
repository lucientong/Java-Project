package com.tyz.transmission.requester;

import com.tyz.registration.information.OwnerInformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 实现资源请求者的默认分发策略，如果健康的资源拥有者数量超过 MAX_SENDING_END，
 * 则根据健康值进行升序排序，选出前 MAX_SENDING_END 个最健康的资源拥有者。
 *
 * @author tyz
 */
public class DefaultDistributionStrategyImpl implements IDistributionStrategy {
    /** 默认最大发送端数量 */
    public static final int MAX_SENDING_END = 5;

    @Override
    public List<OwnerInformation> selectProperResourceOwner(List<OwnerInformation> ownerList) {
        if (ownerList.size() < MAX_SENDING_END) {
            return ownerList;
        }
        ownerList.sort(Comparator.comparingInt(OwnerInformation::getHealthValue));

        List<OwnerInformation> res = new ArrayList<>();

        for (int i = 0; i < MAX_SENDING_END; i++) {
            res.add(ownerList.get(i));
        }
        return res;
    }
}
