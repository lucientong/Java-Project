package com.tyz.csframework.protocol;

/**
 * 服务器和客户端的传输命令
 *
 * @author tyz
 */
public enum ETransferCommand {
    /**
     * 服务器命令，意为客户端成功连接
     */
    ONLINE,

    /**
     * 服务器命令，由于超过服务器最大载荷量，拒绝客户端上线，
     */
    REJECTED,

    /**
     * 客户端/服务器命令，发送一条一对一的消息
     */
    TALK_TO_ONE,

    /**
     * 服务器命令，客户端要送达信息的目标客户端不存在
     */
    TARGET_IS_NOT_EXIST,

    /**
     * 客户端/服务器命令，发送一条对所有客户的的消息
     */
    TALK_TO_ALL,

    /**
     * 客户端命令，执行下线操作
     */
    OFFLINE,

    /**
     * 服务器命令。强制宕机，将连接在这个服务器的客户端全部下线
     */
    FORCE_DOWN,

    /**
     * 服务器命令，指定一个客户端下线
     */
    KILL,

    /**
     * 客户端命令，向服务器发送请求
     */
    REQUEST,

    /**
     * 服务器命令，向客户端发送响应
     */
    RESPONSE,
}
