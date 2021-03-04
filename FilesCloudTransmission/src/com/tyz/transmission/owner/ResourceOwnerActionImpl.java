package com.tyz.transmission.owner;

import com.tyz.transmission.files.ResourceInformation;
import com.tyz.transmission.protocol.SectionHeader;
import com.tyz.transmission.requester.RequesterInformation;
import com.tyz.transmission.sender.SendingClient;

import java.io.IOException;
import java.util.List;

/**
 * 资源拥有者RMI服务器执行远程方法调用的具体方法实现
 *
 * @author tyz
 */
public class ResourceOwnerActionImpl implements IResourceOwnerAction {

    public ResourceOwnerActionImpl() {}

    /**
     * 根据资源请求者发来的发送任务 {@code assignmentList} 和资源编号 {@code resourceId}
     * 向资源请求者 {@code requester} 发送文件块。
     *
     * @param resourceId 资源编号
     * @param requester 资源请求者
     * @param assignmentList 资源拥有者分配到的发送任务
     */
    @Override
    public void send(int resourceId, RequesterInformation requester, List<SectionHeader> assignmentList) {
        ResourceInformation resourceInformation = ResourceOwner.getResourceInformationMap().get(resourceId);
        SendingClient sendingClient = new SendingClient(requester.getPort(), requester.getIp(),
                                            resourceInformation, assignmentList);

        ResourceOwner.getAction().updateHealthValue(ResourceOwner.getOwnerInformation(), true);

        try {
            sendingClient.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResourceOwner.getAction().updateHealthValue(ResourceOwner.getOwnerInformation(), false);
    }
}

