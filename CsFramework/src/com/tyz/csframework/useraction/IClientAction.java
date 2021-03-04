<<<<<<< HEAD
package com.tyz.csframework.useraction;

/**
 * 客户端需要实现的一些功能接口
 *
 * @author tyz
 */
public interface IClientAction {
    /**
     * 处理服务器异常掉线的情况
     */
    void dealServerAbnormalDisconnected();

    /**
     * 客户端处理连接服务器成功之后的状态
     */
    void afterConnectedSuccessfully();

    /**
     * 客户端处理连接失败之后的状态
     */
    void afterConnectFailed();

    /**
     * 处理其他客户端传送的一对一消息
     *
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPrivateMessage(String source, String message);

    /**
     * 处理发送消息的对端不存在的情况
     *
     * @param target 传送消息的目标id
     */
    void dealTargetIsNotExist(String target);

    /**
     * 处理其他客户端发来的群发消息
     *
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPublicMessage(String source, String message);

    /**
     * 客户端确实是否真的下线
     *
     * @return 客户端确认要下线
     */
    boolean beSureOffline();

    /**
     * 在客户端离线前系统需要执行的操作
     */
    void beforeOffline();

    /**
     * 在客户端离线后系统需要执行的操作
     */
    void afterOffline();

    /**
     * 处理服务器执行了强制宕机命令之后的状态
     */
    void dealServerExecuteForceDown();

    /**
     * 处理被服务器因 {@code reason} 强制下线的状态
     *
     * @param reason 被服务器强制下线的原因
     */
    void killedByServer(String reason);
}
=======
package com.tyz.csframework.useraction;

/**
 * 客户端需要实现的一些功能接口
 *
 * @author tyz
 */
public interface IClientAction {
    /**
     * 处理服务器异常掉线的情况
     */
    void dealServerAbnormalDisconnected();

    /**
     * 客户端处理连接服务器成功之后的状态
     */
    void afterConnectedSuccessfully();

    /**
     * 客户端处理连接失败之后的状态
     */
    void afterConnectFailed();

    /**
     * 处理其他客户端传送的一对一消息
     *
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPrivateMessage(String source, String message);

    /**
     * 处理发送消息的对端不存在的情况
     *
     * @param target 传送消息的目标id
     */
    void dealTargetIsNotExist(String target);

    /**
     * 处理其他客户端发来的群发消息
     *
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPublicMessage(String source, String message);

    /**
     * 客户端确实是否真的下线
     *
     * @return 客户端确认要下线
     */
    boolean beSureOffline();

    /**
     * 在客户端离线前系统需要执行的操作
     */
    void beforeOffline();

    /**
     * 在客户端离线后系统需要执行的操作
     */
    void afterOffline();

    /**
     * 处理服务器执行了强制宕机命令之后的状态
     */
    void dealServerExecuteForceDown();

    /**
     * 处理被服务器因 {@code reason} 强制下线的状态
     *
     * @param reason 被服务器强制下线的原因
     */
    void killedByServer(String reason);
}
>>>>>>> d4037e3d4c4890c361da0833e8f35f765b5789b1
