package com.tyz.nio.core;

import com.tyz.nio.actionbean.DefaultSessionImpl;
import com.tyz.nio.actionbean.ISessionProcessor;
import com.tyz.nio.communication.BaseClientCommunication;
import com.tyz.nio.communication.BaseCommunication;
import com.tyz.nio.protocol.ETransferCommand;
import com.tyz.nio.protocol.NetMessage;
import com.tyz.nio.protocol.TransferCommandProcessor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 * 客户端会话层，和 {@link ServerConversation} 一对一的连接，并和
 * 服务器会话层生成的 {@code id} 保持一致，作为会话的唯一标识。完成和
 * 服务器之间的通信功能。
 *
 * {@link BaseCommunication} 第二层继承
 * @author tyz
 */
public class ClientConversation extends BaseClientCommunication {
    private String id;
    private Client client;

    private ISessionProcessor sessionProcessor;

    ClientConversation(Client client, Socket socket) throws IOException {
        super(socket);
        this.client = client;
        this.sessionProcessor = new DefaultSessionImpl();
    }

    /**
     * 向服务器发送请求
     * @param action 请求服务器完成的动作
     * @param parameter 动作完成需要的参数
     */
    void sendMessage(String action, String parameter) {
        send(new NetMessage(action, this.id, parameter, ETransferCommand.MESSAGE));
    }

    /**
     * 向服务器发送下线命令 {@code OFFLINE}
     */
    void offline() {
        send(new NetMessage(null, this.id, null,
                            ETransferCommand.OFFLINE));
    }

    /**
     * 处理 {@link ETransferCommand} 中的 {@code MESSAGE}命令。
     * 此方法最终会被 {@link TransferCommandProcessor} 类的方法
     * 反射执行，处理服务器对自己请求之后的响应。
     *
     * @param netMessage 服务器传送的消息
     * @throws Exception 未找到处理的action对应的方法或者
     *                   方法在反射调用时失败
     */
    public void dealMessage(NetMessage netMessage) throws Exception {
        String action = netMessage.getAction();
        String parameter = netMessage.getParameter();

        this.sessionProcessor.dealResponse(action, parameter);
    }

    /**
     * 处理 {@link ETransferCommand} 中的 {@code FORCE_DOWN}命令。
     * 此方法最终会被 {@link TransferCommandProcessor} 类的方法
     * 反射执行，处理服务器的强制宕机命令。
     * 。
     * @param netMessage 服务器传送的消息
     */
    public void dealForceDown(NetMessage netMessage) {
        close();
        this.client.dealServerForceDown();
    }

    /**
     * 处理 {@link ETransferCommand} 中的 {@code ID}命令。
     * 此方法最终会被 {@link TransferCommandProcessor} 类
     * 的方法反射执行。将自己的 {@code id} 设置为服务器分配的id。
     *
     * @param netMessage 接收到的服务器的消息
     */
    public void dealId(NetMessage netMessage) {
        this.id = netMessage.getParameter();
    }

    /**
     * 处理 {@link ETransferCommand} 中的 {@code WHAT_IS_UP}命令。
     * 此方法最终会被 {@link TransferCommandProcessor} 类的方法反射
     * 执行。
     * 这条命令是服务器用来确认客户端是否还存活的，若此客户端掉线，服务器
     * 将会发生异常，所以这里可以不对服务器的消息做处理，这样的效率更高。
     *
     * @param netMessage 接收到的服务器的消息
     */
    public void dealWhatIsUp(NetMessage netMessage) {}

    /**
     * 实现 {@link BaseCommunication} 中的抽象方法，也就是在接收到服务器
     * 发送的消息之后，将会调用 {@link TransferCommandProcessor} 的方法
     * 进行解析。
     *
     * @param netMessage 接收的消息
     */
    @Override
    public void dealNetMessage(NetMessage netMessage) {
        try {
            TransferCommandProcessor.resolveTransferCommandAndInvoke(this, netMessage);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理服务器异常掉线情况
     */
    @Override
    public void dealOppositeEndAbnormalDrop() {
        this.client.dealServerAbnormalDrop();
    }

    @Override
    protected void close() {
        super.close();
    }
}
