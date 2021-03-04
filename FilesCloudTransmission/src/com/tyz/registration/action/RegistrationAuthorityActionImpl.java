package com.tyz.registration.action;

import com.tyz.registration.information.InformationTable;
import com.tyz.registration.information.OwnerInformation;
import com.tyz.transmission.files.ResourceInformation;

import java.util.List;
import java.util.Map;

/**
 * 注册中心所完成功能的实现类
 *
 * @author tyz
 */
public class RegistrationAuthorityActionImpl implements IRegistrationAuthorityAction {

    /** 注册中心的信息表 */
    private InformationTable informationTable;

    public RegistrationAuthorityActionImpl() {
        this.informationTable = new InformationTable();
    }

    @Override
    public void registryResource(int resourceId, String resourceName, ResourceInformation resourceInformation) {
        this.informationTable.addResource(resourceId, resourceName, resourceInformation);
    }

    @Override
    public void logoutResource(int resourceId) {
        this.informationTable.removeResource(resourceId);
    }

    @Override
    public void logoutResource(String resourceName) {
        this.informationTable.removeResource(resourceName);
    }

    @Override
    public void registryResourceOwner(int resourceId, OwnerInformation ownerInformation) {
        this.informationTable.addResouceOwner(resourceId, ownerInformation);
    }

    @Override
    public void logoutResourceOwner(int resourceId, OwnerInformation ownerInformation) {
        this.informationTable.removeResourceOwner(resourceId, ownerInformation);
    }

    @Override
    public void logoutResourceOwner(OwnerInformation ownerInformation) {
        this.informationTable.removeResourceOwner(ownerInformation);
    }

    @Override
    public void updateHealthValue(OwnerInformation ownerInformation, boolean isIncrease) {
        if (isIncrease) {
            this.informationTable.incrementHealthValue(ownerInformation);
        } else {
            this.informationTable.decrementHealthValue(ownerInformation);
        }
    }

    @Override
    public List<OwnerInformation> getResourceOwnerList(int resourceId) {
        return this.informationTable.getResourceOwnerList(resourceId);
    }

    @Override
    public Map<String, Integer> getResourceNameMap() {
        return this.informationTable.getResourceNameMap();
    }

    @Override
    public ResourceInformation getResourceInformation(int resourceId) {
        return this.informationTable.getResourceInformation(resourceId);
    }
}
