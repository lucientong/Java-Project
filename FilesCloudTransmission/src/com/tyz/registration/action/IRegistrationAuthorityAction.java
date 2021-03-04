package com.tyz.registration.action;

import com.tyz.registration.information.OwnerInformation;
import com.tyz.transmission.files.ResourceInformation;

import java.util.List;
import java.util.Map;

/**
 * 定义注册中心需要实现的方法
 *
 * @author tyz
 */
public interface IRegistrationAuthorityAction {
    /**
     * 资源最初拥有者在注册中心注册资源
     *
     * @param resourceId 资源编号
     * @param resourceName 资源名称
     * @param resourceInformation 资源详细信息
     */
    void registryResource(int resourceId, String resourceName, ResourceInformation resourceInformation);

    /**
     * 资源最初拥有者在注册中心注销编号为 {@code resourceId} 的资源
     *
     * @param resourceId 资源编号
     */
    void logoutResource(int resourceId);

    /**
     * 资源最初拥有者在注册中心注销名称为 {@code resourceName} 的资源
     *
     * @param resourceName 资源名称
     */
    void logoutResource(String resourceName);

    /**
     * 资源拥有者在注册中心注册资源
     *
     * @param resourceId 资源编号
     * @param ownerInformation 资源拥有者信息
     */
    void registryResourceOwner(int resourceId, OwnerInformation ownerInformation);

    /**
     * 资源拥有者在注册中心注销资源
     *
     * @param resourceId 资源编号
     * @param ownerInformation 资源拥有者信息
     */
    void logoutResourceOwner(int resourceId, OwnerInformation ownerInformation);

    /**
     * 资源拥有者在注册中心注销它注册过的所有资源
     *
     * @param ownerInformation 资源拥有者信息
     */
    void logoutResourceOwner(OwnerInformation ownerInformation);

    /**
     * 资源拥有者更新在注册中心中的健康值，{@code isIncrease} 为真时健康值加1，否则减1
     *
     * @param ownerInformation 资源拥有者信息
     * @param isIncrease 是否增加
     */
    void updateHealthValue(OwnerInformation ownerInformation, boolean isIncrease);

    /**
     * 获取拥有编号为 {@code resourceId} 的资源的资源拥有者列表
     *
     * @param resourceId 资源编号
     * @return 拥有该资源的资源拥有者列表
     */
    List<OwnerInformation> getResourceOwnerList(int resourceId);

    /**
     * 获取注册中心的资源名称和资源编号映射的资源表
     *
     * @return 资源表
     */
    Map<String, Integer> getResourceNameMap();

    /**
     * 获取注册中心中编号为 {@code resourceId} 的资源详细信息
     *
     * @param resourceId 资源编号
     * @return 资源详细信息
     */
    ResourceInformation getResourceInformation(int resourceId);
}
