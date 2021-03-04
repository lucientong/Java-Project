<<<<<<< HEAD
package com.tyz.csframework.core;

import com.tyz.csframework.actionbean.DefaultSessionImpl;
import com.tyz.csframework.actionbean.ISessionProcessor;
import com.tyz.csframework.useraction.ClientActionAdapter;
import com.tyz.csframework.useraction.IClientAction;

import java.io.IOException;
import java.net.Socket;

/**
 * 客户端的实现
 *
 * @author tyz
 */
public class Client {
    private int port;
    private String ip;

    private ClientConversation clientConversation;

    private IClientAction clientAction;

    private ISessionProcessor sessionProcessor;

    public Client(int port, String ip) {
        this.port = port;
        this.ip = ip;
        this.clientAction = new ClientActionAdapter();
        this.sessionProcessor = new DefaultSessionImpl();
    }

    /**
     * 连接服务器，若出现异常或失败，返回false，连接成功返回true
     *
     * @return 是否成功连接服务器
     */
    public boolean connectToServer() {
        try {
            Socket socket = new Socket(this.ip, this.port);
            this.clientConversation = new ClientConversation(this, socket);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 客户端一对一传送消息，底层的通信由{@link ClientConversation} 实现
     *
     * @param target 目标id
     * @param message 消息内容
     */
    public void talkToOne(String target, String message) {
        this.clientConversation.talkToOne(target, message);
    }

    /**
     * 客户端群发消息，底层的通信由{@link ClientConversation} 实现
     *
     * @param message 消息内容
     */
    public void talkToAll(String message) {
        this.clientConversation.talkToAll(message);
    }

    /**
     * 客户端下线
     */
    public void offline() {
        if (this.clientAction.beSureOffline()) {
            this.clientAction.beforeOffline();
            this.clientConversation.offline();
            this.clientConversation.close();
            this.clientAction.afterOffline();
        }
    }

    /**
     * 向服务器发送请求
     *
     * @param action 请求的行为
     * @param parameter 参数
     */
    public void sendRequest(String action, String parameter) {
        this.clientConversation.sendRequest(action, parameter);
    }

    /**
     * 设置 {@code clientAction}，对 {@link ClientActionAdapter}
     * 进行覆盖
     *
     * @param clientAction 新的客户端功能接口
     */
    public void setClientAction(IClientAction clientAction) {
        this.clientAction = clientAction;
    }

    /**
     * 获取客户端连接的服务器的端口号
     *
     * @return 服务器的端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取客户端连接的服务器的ip地址
     *
     * @return 服务器的ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * 处理客户端异常掉线情况，该方法最终由 {@link ClientConversation}
     * 调用。
     */
    void dealServerAbnormalDisconnected() {
        this.clientAction.dealServerAbnormalDisconnected();
    }

    /**
     * 处理客户端连接成功之后的状态，该方法最终由 {@link ClientConversation}
     * 调用。
     */
    void afterConnectedSuccessfully() {
        this.clientAction.afterConnectedSuccessfully();
    }

    /**
     * 处理客户端连接失败之后的状态，该方法最终由 {@link ClientConversation}
     * 调用。
     */
    void afterConnectFailed() {
        this.clientAction.afterConnectFailed();
    }

    /**
     * 处理其他客户端传送的一对一消息
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPrivateMessage(String source, String message) {
        this.clientAction.dealPrivateMessage(source, message);
    }

    /**
     * 处理发送消息的对端不存在的情况
     *
     * @param target 传送消息的目标id
     */
    void dealTargetIsNotExist(String target) {
        this.clientAction.dealTargetIsNotExist(target);
    }

    /**
     * 处理其他客户端传送的群发消息
     *
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPublicMessage(String source, String message) {
        this.clientAction.dealPublicMessage(source, message);
    }

    /**
     * 处理服务器执行了强制宕机命令之后的状态
     */
    void dealServerExecuteForceDown() {
        this.clientAction.dealServerExecuteForceDown();
    }

    /**
     * 处理被服务器因 {@code reason} 强制下线的状态
     *
     * @param reason 被服务器强制下线的原因
     */
    void killedByServer(String reason) {
        this.clientAction.killedByServer(reason);
    }

    /**
     * 处理服务器的响应
     *
     * @param action 响应的行为
     * @param parameter 响应的结果
     */
    void dealResponse(String action, String parameter) throws Exception {
        this.sessionProcessor.dealResponse(action, parameter);
    }
}
=======
package com.tyz.csframework.core;

import com.tyz.csframework.actionbean.DefaultSessionImpl;
import com.tyz.csframework.actionbean.ISessionProcessor;
import com.tyz.csframework.useraction.ClientActionAdapter;
import com.tyz.csframework.useraction.IClientAction;

import java.io.IOException;
import java.net.Socket;

/**
 * 客户端的实现
 *
 * @author tyz
 */
public class Client {
    private int port;
    private String ip;

    private ClientConversation clientConversation;

    private IClientAction clientAction;

    private ISessionProcessor sessionProcessor;

    public Client(int port, String ip) {
        this.port = port;
        this.ip = ip;
        this.clientAction = new ClientActionAdapter();
        this.sessionProcessor = new DefaultSessionImpl();
    }

    /**
     * 连接服务器，若出现异常或失败，返回false，连接成功返回true
     *
     * @return 是否成功连接服务器
     */
    public boolean connectToServer() {
        try {
            Socket socket = new Socket(this.ip, this.port);
            this.clientConversation = new ClientConversation(this, socket);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 客户端一对一传送消息，底层的通信由{@link ClientConversation} 实现
     *
     * @param target 目标id
     * @param message 消息内容
     */
    public void talkToOne(String target, String message) {
        this.clientConversation.talkToOne(target, message);
    }

    /**
     * 客户端群发消息，底层的通信由{@link ClientConversation} 实现
     *
     * @param message 消息内容
     */
    public void talkToAll(String message) {
        this.clientConversation.talkToAll(message);
    }

    /**
     * 客户端下线
     */
    public void offline() {
        if (this.clientAction.beSureOffline()) {
            this.clientAction.beforeOffline();
            this.clientConversation.offline();
            this.clientConversation.close();
            this.clientAction.afterOffline();
        }
    }

    /**
     * 向服务器发送请求
     *
     * @param action 请求的行为
     * @param parameter 参数
     */
    public void sendRequest(String action, String parameter) {
        this.clientConversation.sendRequest(action, parameter);
    }

    /**
     * 处理客户端异常掉线情况，该方法最终由 {@link ClientConversation}
     * 调用。
     */
    void dealServerAbnormalDisconnected() {
        this.clientAction.dealServerAbnormalDisconnected();
    }

    /**
     * 处理客户端连接成功之后的状态，该方法最终由 {@link ClientConversation}
     * 调用。
     */
    void afterConnectedSuccessfully() {
        this.clientAction.afterConnectedSuccessfully();
    }

    /**
     * 处理客户端连接失败之后的状态，该方法最终由 {@link ClientConversation}
     * 调用。
     */
    void afterConnectFailed() {
        this.clientAction.afterConnectFailed();
    }

    /**
     * 处理其他客户端传送的一对一消息
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPrivateMessage(String source, String message) {
        this.clientAction.dealPrivateMessage(source, message);
    }

    /**
     * 处理发送消息的对端不存在的情况
     *
     * @param target 传送消息的目标id
     */
    void dealTargetIsNotExist(String target) {
        this.clientAction.dealTargetIsNotExist(target);
    }

    /**
     * 处理其他客户端传送的群发消息
     *
     * @param source 发送消息的客户端id
     * @param message 接收到的消息
     */
    void dealPublicMessage(String source, String message) {
        this.clientAction.dealPublicMessage(source, message);
    }

    /**
     * 处理服务器执行了强制宕机命令之后的状态
     */
    void dealServerExecuteForceDown() {
        this.clientAction.dealServerExecuteForceDown();
    }

    /**
     * 处理被服务器因 {@code reason} 强制下线的状态
     *
     * @param reason 被服务器强制下线的原因
     */
    void killedByServer(String reason) {
        this.clientAction.killedByServer(reason);
    }

    /**
     * 处理服务器的响应
     *
     * @param action 响应的行为
     * @param parameter 响应的结果
     */
    void dealResponse(String action, String parameter) throws Exception {
        this.sessionProcessor.dealResponse(action, parameter);
    }
}
>>>>>>> d4037e3d4c4890c361da0833e8f35f765b5789b1
