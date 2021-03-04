package com.tyz.csframework.core;

import com.tyz.csframework.communication.Communication;
import com.tyz.csframework.protocol.ETransferCommand;
import com.tyz.csframework.protocol.MessagePackage;
import com.tyz.csframework.protocol.NetMessage;
import com.tyz.util.ArgumentMaker;
import sun.nio.ch.Net;

import java.net.Socket;

/**
 * 服务器建立的与客户端通信的会话层，实现对消息的处理，
 * 以及对客户端的响应。
 *
 * @author tyz
 */
public class ServerConversation extends Communication {
    private String id;
    private Server server;

    protected ServerConversation(Server server, Socket socket) {
        super(socket);
        this.server = server;
    }

    /**
     * 客户端连接成功，向客户端发送分配的id
     */
    void clientIsOnline() {
        send(new NetMessage(null, this.id, ETransferCommand.ONLINE));
    }

    /**
     * 由于超过服务器最大载荷量，拒绝客户端上线
     */
    void rejectClient() {
        send(new NetMessage(null, this.id, ETransferCommand.REJECTED));
    }

    /**
     * 服务器将消息转发给与自己相连的客户端
     *
     * @param messagePackage 需要转发的信息
     */
    void talkToOne(MessagePackage messagePackage) {
        send(new NetMessage(null,
                ArgumentMaker.GSON.toJson(messagePackage),
                ETransferCommand.TALK_TO_ONE));
    }

    /**
     * 服务器告知客户端，要发送的信息无法找到目标
     *
     * @param target 客户端试图发送消息的另一个客户端
     */
    void targetIsNotExist(String target) {
        send(new NetMessage(null, target, ETransferCommand.TARGET_IS_NOT_EXIST));
    }

    /**
     * 服务器将群发消息转发给客户端
     *
     * @param mp 需要转发的消息
     */
    void talkToAll(MessagePackage mp) {
        send(new NetMessage(null, ArgumentMaker.GSON.toJson(mp),
                                            ETransferCommand.TALK_TO_ALL));
    }

    /**
     * 服务器执行强制宕机命令
     */
    void serverForceDown() {
        send(new NetMessage(null, null, ETransferCommand.FORCE_DOWN));
    }

    /**
     * 因为 {@code reason} 使得id为 {@code id} 的客户端下线
     *
     * @param id 要强制下线的客户端id
     * @param reason 需要客户端强制下线的原因
     */
    void killClient(String id, String reason) {
        send(new NetMessage(null, reason, ETransferCommand.KILL));
    }

    @Override
    public void dealNetMessage(NetMessage netMessage) {
        ETransferCommand command = netMessage.getCommand();
        String parameter = netMessage.getParameter();
        MessagePackage mp = null;

        switch (command) {
            case TALK_TO_ONE:
                mp = ArgumentMaker.GSON.fromJson(parameter, MessagePackage.class);
                this.server.talkToOne(mp);
                break;
            case TALK_TO_ALL:
                mp = ArgumentMaker.GSON.fromJson(parameter, MessagePackage.class);
                this.server.talkToAll(mp);
                break;
            case OFFLINE:
                this.server.clientOffline(parameter);
                close();
                break;
            case REQUEST:
                try {
                    String action = netMessage.getAction();
                    String result = this.server.dealRequest(action, parameter);

                    send(new NetMessage(action, result, ETransferCommand.RESPONSE));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理客户端异常掉线，该抽象方法最终在 {@link Communication}
     * 中实现
     */
    @Override
    public void dealOppositeEndAbnormalDrop() {
        this.server.dealClientAbnormalDisconnected(this);
    }

    /**
     * 调用 {@link Communication}的方法，关闭通信信道和socket
     */
    @Override
    protected void close() {
        super.close();
    }

    /**
     * 设置 {@code id}
     * @param id 服务器生成的id
     */
    void setId(String id) {
        this.id = id;
    }

    /**
     * 获得客户端的ID
     *
     * @return 客户端ID
     */
    String getId() {
        return id;
    }
}
