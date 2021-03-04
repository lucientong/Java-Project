<<<<<<< HEAD
package com.tyz.csframework.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 收集服务器连接的 {@link ServerConversation}，这就
 * 代表了一个客户端。此类是对客户端的一个管理和记录，使用
 * 一个客户端池子，以客户端的ID为键，客户端为值，生成一个
 * 散列表。
 *
 * @author tyz
 */
public class ClientPool {
    /** 默认可承载的客户端数量 */
    public static final int DEFAULT_CAPACITY = 1 << 16;

    private final Map<String, ServerConversation> clientPool = new ConcurrentHashMap<>();;

    private int capacity;

    ClientPool() {
        this.capacity = DEFAULT_CAPACITY;
    }

    /**
     * 将一个客户端的会话信息加入进客户端池
     *
     * @param id 客户端ID
     * @param client 客户端
     */
    boolean addClient(String id, ServerConversation client) {
        if (this.clientPool.size() < this.capacity) {
            this.clientPool.put(id, client);
            return true;
        }
        return false;
    }

    /**
     * 删除一个客户端
     *
     * @param id 需要清除的客户端id
     */
    void removeClient(String id) {
        this.clientPool.remove(id);
    }

    /**
     * 设置客户端池子的容量
     * @param capacity 容量
     */
    void setClientPoolCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * @return 返回客户端池是否为空
     */
    boolean isEmpty() {
        return this.clientPool.isEmpty();
    }

    /**
     * @return 返回id为 {@code id} 的客户端
     */
    ServerConversation getClient(String id) {
        return this.clientPool.get(id);
    }

    /**
     * 得到所有客户端
     *
     * @return 所有客户端
     */
    List<ServerConversation> getAllClients() {
        return (List<ServerConversation>) this.clientPool.values();
    }
}
=======
package com.tyz.csframework.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 收集服务器连接的 {@link ServerConversation}，这就
 * 代表了一个客户端。此类是对客户端的一个管理和记录，使用
 * 一个客户端池子，以客户端的ID为键，客户端为值，生成一个
 * 散列表。
 *
 * @author tyz
 */
public class ClientPool {
    /** 默认可承载的客户端数量 */
    public static final int DEFAULT_CAPACITY = 1 << 16;

    private final Map<String, ServerConversation> clientPool = new ConcurrentHashMap<>();;

    private int capacity;

    ClientPool() {
        this.capacity = DEFAULT_CAPACITY;
    }

    /**
     * 将一个客户端的会话信息加入进客户端池
     *
     * @param id 客户端ID
     * @param client 客户端
     */
    boolean addClient(String id, ServerConversation client) {
        if (this.clientPool.size() < this.capacity) {
            this.clientPool.put(id, client);
            return true;
        }
        return false;
    }

    /**
     * 删除一个客户端
     *
     * @param id 需要清除的客户端id
     */
    void removeClient(String id) {
        this.clientPool.remove(id);
    }

    /**
     * 设置客户端池子的容量
     * @param capacity 容量
     */
    void setClientPoolCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * @return 返回客户端池是否为空
     */
    boolean isEmpty() {
        return this.clientPool.isEmpty();
    }

    /**
     * @return 返回id为 {@code id} 的客户端
     */
    ServerConversation getClient(String id) {
        return this.clientPool.get(id);
    }

    /**
     * 得到所有客户端
     *
     * @return 所有客户端
     */
    List<ServerConversation> getAllClients() {
        return (List<ServerConversation>) this.clientPool.values();
    }
}
>>>>>>> d4037e3d4c4890c361da0833e8f35f765b5789b1
