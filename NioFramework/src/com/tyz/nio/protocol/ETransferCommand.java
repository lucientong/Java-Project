package com.tyz.nio.protocol;

/**
 * 客户端和服务器通信时传递的控制信息
 * @author tyz
 */

public enum ETransferCommand {
    /**
     * 服务器命令。
     * 服务器对接入的客户端生成唯一标识id，服务器通过 {@code ID}
     * 命令告知客户端此时传输的数据是分配给它的id。客户端在接收到
     * {@code ID} 命令后会将收到的id信息设为自己的Id。
     */
    ID,

    /**
     * 服务器命令和客户端命令，客户端发送请求，
     * 服务器发送响应
     */
    MESSAGE,

    /**
     * 客户端命令，下线
     */
    OFFLINE,

    /**
     * 服务器命令，执行强制宕机
     */
    FORCE_DOWN,

    /**
     * 服务器命令，用以判断对端的客户端是否还在线，
     * 若发送成功，客户端不需要回应，若发送失败，说明客
     * 户端已经异常掉线。
     */
    WHAT_IS_UP,
}
