package com.tyz.registration.information;

import com.tyz.registration.action.DefaultLoadBalanceStrategyImpl;
import com.tyz.registration.action.ILoadBalanceStrategy;
import com.tyz.transmission.files.ResourceInformation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心中需要维护的信息映射表
 *
 * @author tyz
 */
public class InformationTable {
    /** 资源编号与资源详细信息的映射 */
    private static final Map<Integer, ResourceInformation> RESOURCE_INFORMATION_MAP;

    /** 资源名称与资源编号的映射 */
    private static final Map<String, Integer> RESOURCE_NAME_MAP;

    /** 资源编号与资源拥有者列表的映射 */
    private static final Map<Integer, Set<OwnerInformation>> RESOURCE_OWNER_MAP;

    /** 资源拥有者哈希值和对应的对象的映射 */
    private static final Map<Integer, OwnerInformation> OWNER_INFORMATION_MAP;

    /** 锁 */
    private static final Object LOCK;

    /** 负载均衡策略 */
    private static ILoadBalanceStrategy loadBalanceStrategy;

    static {
        RESOURCE_INFORMATION_MAP = new ConcurrentHashMap<>();
        RESOURCE_NAME_MAP = new ConcurrentHashMap<>();
        RESOURCE_OWNER_MAP = new ConcurrentHashMap<>();
        OWNER_INFORMATION_MAP = new ConcurrentHashMap<>();
        LOCK = new Object();
        loadBalanceStrategy = new DefaultLoadBalanceStrategyImpl();
    }

    /**
     * 资源最初拥有者注册资源信息
     *
     * @param resourceId 资源编号
     * @param resourceName 资源名称
     * @param resourceInformation 资源详细信息
     */
    public void addResource(int resourceId, String resourceName,
                                                ResourceInformation resourceInformation) {
        RESOURCE_INFORMATION_MAP.put(resourceId, resourceInformation);
        RESOURCE_NAME_MAP.put(resourceName, resourceId);
    }

    /**
     * 按照资源编号删除资源
     *
     * @param resourceId 资源编号
     */
    public void removeResource(int resourceId) {
        RESOURCE_INFORMATION_MAP.remove(resourceId);

        for (String resourceName : RESOURCE_NAME_MAP.keySet()) {
            if (RESOURCE_NAME_MAP.get(resourceName) == resourceId) {
                RESOURCE_NAME_MAP.remove(resourceName);
            }
        }
    }

    /**
     * 根据资源名称删除资源
     *
     * @param resourceName 资源名称
     */
    public void removeResource(String resourceName) {
        RESOURCE_INFORMATION_MAP.remove(RESOURCE_NAME_MAP.get(resourceName));
        RESOURCE_NAME_MAP.remove(resourceName);
    }

    /**
     * 资源拥有者注册资源，将资源拥有者信息添加到 {@code resourceId}资源 对应的资源拥有者列表中
     *
     * @param resourceId 资源编号
     * @param ownerInformation 资源拥有者信息
     */
    public void addResouceOwner(int resourceId, OwnerInformation ownerInformation) {
        Set<OwnerInformation> set = RESOURCE_OWNER_MAP.getOrDefault(resourceId, new HashSet<>());
        set.add(ownerInformation);
        RESOURCE_OWNER_MAP.put(resourceId, set);

        int key = ownerInformation.hashCode();

        OwnerInformation owner = OWNER_INFORMATION_MAP.get(key);
        if (owner == null) {
            synchronized (LOCK) {
                owner = OWNER_INFORMATION_MAP.get(key);
                if (owner == null) {
                    owner = ownerInformation;
                    OWNER_INFORMATION_MAP.put(key, ownerInformation);
                }
            }
        }
        owner.incrementResourceCount();
    }

    /**
     * 资源拥有者注销资源，从 {@code resourceId}资源 对应的资源拥有者列表中删除
     * 资源拥有者信息 {@code ownerInformation}，若资源拥有者注册的资源数为0，
     * 则将资源拥有者从 {@code OWNER_INFORMATION_MAP} 中删除。
     *
     * @param resourceId 资源编号
     * @param ownerInformation 资源拥有者信息
     */
    public void removeResourceOwner(int resourceId, OwnerInformation ownerInformation) {
        int key = ownerInformation.hashCode();
        OwnerInformation owner = OWNER_INFORMATION_MAP.get(key);

        if (owner == null) {
            return;
        }

        Set<OwnerInformation> set = RESOURCE_OWNER_MAP.get(resourceId);

        if (set != null) {
            synchronized (LOCK) {
                set = RESOURCE_OWNER_MAP.get(resourceId);
                owner = OWNER_INFORMATION_MAP.get(key);
                if (set != null && owner != null) {
                    set.remove(ownerInformation);
                    if (set.isEmpty()) {
                        RESOURCE_OWNER_MAP.remove(resourceId);
                        removeResource(resourceId);
                    }
                    owner.decrementResourceCount();

                    if (owner.getResourceCount() <= 0) {
                        OWNER_INFORMATION_MAP.remove(key);
                    }
                }
            }
        }
    }

    /**
     * 注销资源拥有者 {@code ownerInformation}，将其注册的所有资源全部注销
     *
     * @param ownerInformation 要注销的资源拥有者
     */
    public void removeResourceOwner(OwnerInformation ownerInformation) {
        for (int resourceId : RESOURCE_OWNER_MAP.keySet()) {
            removeResourceOwner(resourceId, ownerInformation);
        }
        OWNER_INFORMATION_MAP.remove(ownerInformation.hashCode());
    }

    /**
     * 资源拥有者 {@code ownerInformation} 正在发送的资源数加一，增加资源拥有者的健康值
     *
     * @param ownerInformation 资源拥有者信息
     */
    public void incrementHealthValue(OwnerInformation ownerInformation) {
        int key = ownerInformation.hashCode();
        OwnerInformation owner = OWNER_INFORMATION_MAP.get(key);

        if (owner != null) {
            synchronized (LOCK) {
                owner.incrementHealthValue();
            }
        }
    }

    /**
     * 资源拥有者 {@code ownerInformation} 正在发送的资源数减一，减小资源拥有者的健康值
     *
     * @param ownerInformation 资源拥有者信息
     */
    public void decrementHealthValue(OwnerInformation ownerInformation) {
        int key = ownerInformation.hashCode();
        OwnerInformation owner = OWNER_INFORMATION_MAP.get(key);

        if (owner != null) {
            synchronized (LOCK) {
                owner.decrementHealthValue();
            }
        }
    }

    /**
     * 获取拥有编号为 {@code resourceId} 资源的资源拥有者列表
     *
     * @param resourceId 资源编号
     * @return 拥有该资源的资源拥有者列表
     */
    public List<OwnerInformation> getResourceOwnerList(int resourceId) {
        Set<OwnerInformation> ownerSet = RESOURCE_OWNER_MAP.get(resourceId);
        Set<OwnerInformation> set = new HashSet<>();

        for (OwnerInformation var : ownerSet) {
            OwnerInformation owner = OWNER_INFORMATION_MAP.get(var.hashCode());
            if (owner != null && loadBalanceStrategy.isResourceOwnerValid(owner)) {
                set.add(owner);
            }
        }
        return new ArrayList<>(set);
    }

    /**
     * 获取注册中心的资源名称和资源编号映射的资源表
     *
     * @return 资源表
     */
    public Map<String, Integer> getResourceNameMap() {
        return new HashMap<>(RESOURCE_NAME_MAP);
    }

    /**
     * 获取注册中心中编号为 {@code resourceId} 的资源详细信息
     *
     * @param resourceId 资源编号
     * @return 资源详细信息
     */
    public ResourceInformation getResourceInformation(int resourceId) {
        return RESOURCE_INFORMATION_MAP.get(resourceId);
    }

    /**
     * 配置负载均衡策略
     *
     * @param loadBalanceStrategy 负载均衡策略
     */
    public static void setLoadBalanceStrategy(ILoadBalanceStrategy loadBalanceStrategy) {
        InformationTable.loadBalanceStrategy = loadBalanceStrategy;
    }
}
