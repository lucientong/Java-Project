package com.tyz.nio.core;

import com.tyz.nio.protocol.ETransferCommand;
import com.tyz.nio.protocol.NetMessage;
import com.tyz.nio.useraction.IClientAction;
import sun.nio.ch.Net;

import java.io.IOException;
import java.net.Socket;

/**
 * @author tyz
 */
public class Client {
    private int serverPort;
    private String serverIp;

    private IClientAction clientAction;

    private ClientConversation clientConversation;

    public Client(int serverPort, String serverIp) {
        this.serverPort = serverPort;
        this.serverIp = serverIp;
    }

    /**
     * 连接服务器，并与服务器建立一个会话 {@code clientConversation}
     * @return 连接服务器是否成功
     */
    public boolean connectToServer() {
        try {
            Socket socket = new Socket(this.serverIp, this.serverPort);
            this.clientConversation = new ClientConversation(this, socket);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 确认是否下线，如果确认，调用会话层 {@code clientConversation}
     * 向服务器发送下线命令
     */
    public void offline() {
        if (this.clientAction.ensureIfOffline()) {
            this.clientConversation.offline();
            this.clientConversation.close();
            this.clientAction.afterOffline();
        }
    }

    /**
     * 向服务器发送信息（请求）
     * @param action 需要执行的动作
     * @param parameter 执行所需要的参数
     */
    public void sendMessage(String action, String parameter) {
        this.clientConversation.sendMessage(action, parameter);
    }

    /**
     * 获取客户端连接的服务器的端口号
     *
     * @return 服务器的端口号
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * 获取客户端连接的服务器的ip地址
     *
     * @return 服务器的ip地址
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * 处理服务器异常掉线情况，该方法由 {@link ClientConversation}
     * 调用。
     */
    void dealServerAbnormalDrop() {
        this.clientAction.dealServerAbnormalDrop();
    }

    /**
     * 处理服务器异常掉线情况，该方法由 {@link ClientConversation}
     * 最终调用。
     */
    void dealServerForceDown() {
        this.clientAction.dealServerExecuteForceDown();
    }

    public void setClientAction(IClientAction clientAction) {
        this.clientAction = clientAction;
    }
}
