package com.tyz.nio.core;

import com.tyz.nio.useraction.IClientPoolAction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记录 {@link ServerConversation} 的散列表，以用户的Id为键
 *
 * @author tyz
 */
public class ClientPool {
    private Map<String, ServerConversation> clientPool;

    ClientPool() {
        this.clientPool = new ConcurrentHashMap<>();
    }

    /**
     * 向表中添加一条会话信息
     *
     * @param client 服务器和客户端建立的一个会话层
     */
    void addClient(ServerConversation client) {
        this.clientPool.put(client.getId(), client);
    }

    /**
     * 向表中删除一条会话信息
     *
     * @param client 服务器和客户端建立的一个会话层
     */
    void removeClient(ServerConversation client) {
        this.clientPool.remove(client.getId());
    }

    /**
     * 得到表中添加一条会话信息
     *
     * @param id 客户端的Id
     */
    ServerConversation getClient(String id) {
        return this.clientPool.get(id);
    }

    /**
     * check if {@code clientPool} is empty
     *
     * @return clientPool is empty
     */
    boolean isEmpty() {
        return this.clientPool.isEmpty();
    }

    void processClients(IClientPoolAction clientPoolAction) {
        for (String id : clientPool.keySet()) {
            clientPoolAction.processClient(clientPool.get(id));
        }
    }
}
