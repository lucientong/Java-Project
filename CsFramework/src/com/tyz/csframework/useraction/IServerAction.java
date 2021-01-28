package com.tyz.csframework.useraction;

import com.tyz.csframework.core.ServerConversation;

/**
 * 服务器功能接口
 *
 * @author tyz
 */
public interface IServerAction {
    /**
     * 处理客户端异常掉线
     *
     * @param client 掉线的客户端
     */
    void dealClientAbnormalDisconnected(ServerConversation client);

    /**
     * 处理客户端正常下线
     *
     * @param id 下线的客户端id
     */
    void clientOffline(String id);
}
