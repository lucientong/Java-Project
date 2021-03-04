package com.tyz.nio.useraction;

import com.tyz.nio.core.ServerConversation;

/**
 * 对客户端表中的客户端的处理
 *
 * @author tyz
 */
public interface IClientPoolAction {
    /**
     * 对{@link com.tyz.nio.core.ClientPool} 中的
     * 客户端 {@code client} 进行处理。
     *
     * @param client 与客户端建立的会话
     */
    void processClient(ServerConversation client);
}
