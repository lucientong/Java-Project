package com.tyz.transmission.owner;

import com.tyz.transmission.protocol.SectionHeader;
import com.tyz.transmission.requester.RequesterInformation;

import java.util.List;

/**
 * 定义需要进行远程方法调用的资源拥有者的方法
 *
 * @author tyz
 */
public interface IResourceOwnerAction {
    /**
     * 根据资源请求者发来的发送任务 {@code assignmentList} 和资源编号 {@code resourceId}
     * 向资源请求者 {@code requester} 发送文件块。
     *
     * @param resourceId 资源编号
     * @param requester 资源请求者信息
     * @param assignmentList 资源拥有者分配到的发送任务
     */
    void send(int resourceId, RequesterInformation requester, List<SectionHeader> assignmentList);
}
