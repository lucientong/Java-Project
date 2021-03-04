package com.tyz.csframework.core;

import com.tyz.csframework.communication.Communication;
import com.tyz.csframework.protocol.ETransferCommand;
import com.tyz.csframework.protocol.MessagePackage;
import com.tyz.csframework.protocol.NetMessage;
import com.tyz.util.ArgumentMaker;

import java.net.Socket;

/**
 * 客户端建立的与服务器通信的会话层，实现消息的发送与处理，以及
 * 对服务器的请求
 *
 * @author tyz
 */
public class ClientConversation extends Communication {
    private String id;
    private Client client;

    protected ClientConversation(Client client, Socket socket) {
        super(socket);
        this.client = client;
    }

    /**
     * 实现客户端一对一消息的传送
     * @param target 目标id
     * @param message 消息
     */
    void talkToOne(String target, String message) {
        MessagePackage mp =
                    new MessagePackage(this.id, target, message);
        send(new NetMessage(null,
                        ArgumentMaker.GSON.toJson(mp),
                        ETransferCommand.TALK_TO_ONE));
    }

    /**
     * 实现客户端消息的群发
     *
     * @param message 要发送的消息
     */
    void talkToAll(String message) {
        MessagePackage mp = new MessagePackage(this.id, null, message);
        send(new NetMessage(null, ArgumentMaker.GSON.toJson(mp),
                                            ETransferCommand.TALK_TO_ALL));
    }

    /**
     * 客户端下线
     */
    void offline() {
        send(new NetMessage(null, this.id, ETransferCommand.OFFLINE));
    }

    /**
     * 向服务器发送请求
     *
     * @param action 请求的行为
     * @param parameter 参数
     */
    void sendRequest(String action, String parameter) {
        send(new NetMessage(action, parameter, ETransferCommand.REQUEST));
    }

    @Override
    public void dealNetMessage(NetMessage netMessage) {
        String parameter = netMessage.getParameter();
        ETransferCommand command = netMessage.getCommand();
        MessagePackage mp = null;

        switch (command) {
            case ONLINE:
                this.id = parameter;
                this.client.afterConnectedSuccessfully();
                break;
            case REJECTED:
                close();
                this.client.afterConnectFailed();
                break;
            case TALK_TO_ONE:
                mp = ArgumentMaker.GSON.fromJson(parameter, MessagePackage.class);
                String source = mp.getSource();
                String message = mp.getMessage();
                this.client.dealPrivateMessage(source, message);
                break;
            case TARGET_IS_NOT_EXIST:
                mp = ArgumentMaker.GSON.fromJson(parameter, MessagePackage.class);
                this.client.dealTargetIsNotExist(mp.getTarget());
                break;
            case TALK_TO_ALL:
                mp = ArgumentMaker.GSON.fromJson(parameter, MessagePackage.class);
                this.client.dealPublicMessage(mp.getSource(), mp.getMessage());
                break;
            case FORCE_DOWN:
                close();
                this.client.dealServerExecuteForceDown();
                break;
            case KILL:
                close();
                this.client.killedByServer(parameter);
                break;
            case RESPONSE:
                String action = netMessage.getAction();
                try {
                    this.client.dealResponse(action, parameter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理服务器异常掉线，该抽象方法最终在 {@link Communication}
     * 中实现
     */
    @Override
    public void dealOppositeEndAbnormalDrop() {
        this.client.dealServerAbnormalDisconnected();
    }

    @Override
    protected void close() {
        super.close();
    }
}
