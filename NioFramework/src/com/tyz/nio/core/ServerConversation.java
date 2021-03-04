package com.tyz.nio.core;

import com.tyz.nio.actionbean.DefaultSessionImpl;
import com.tyz.nio.actionbean.ISessionProcessor;
import com.tyz.nio.protocol.TransferCommandProcessor;
import com.tyz.nio.communication.BaseCommunication;
import com.tyz.nio.communication.BaseServerCommunication;
import com.tyz.nio.protocol.ETransferCommand;
import com.tyz.nio.protocol.NetMessage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

/**
 * 服务器会话层，对于和服务器连接的每个客户端，服务器都会建立一个1对1的
 * ServerConversation，处理和单独一个客户端之间的通信任务。
 * {@link BaseCommunication} 第二层继承
 * @author tyz
 */
public class ServerConversation extends BaseServerCommunication {
    private String ip;
    private String id;
    private NioServer server;
    private volatile boolean alive;

    private ISessionProcessor sessionProcessor;

    ServerConversation(NioServer server, Socket socket) throws IOException {
        super(socket);
        this.server = server;
        this.alive = true;
        this.ip = socket.getLocalAddress().getHostAddress();
        this.sessionProcessor = new DefaultSessionImpl();

        creatIdForClientAndInform();

        this.server.addClient(this);
    }

    /**
     * 服务器处理客户端异常掉线的情况
     */
    @Override
    public void dealOppositeEndAbnormalDrop() {
        this.alive = false;
//        this.server.removeClient(this);
//        this.server.speakOut("Client [] is abnormally disconnected.");
    }

    /**
     * 处理服务器用轮询的方式读取的信息，为防止影响服务器的
     * 轮询，将会通过服务器 {@code server} 的线程池开辟一
     * 个新线程来处理客户端的请求。
     *
     * @param netMessage 接收的消息
     */
    @Override
    public void dealNetMessage(NetMessage netMessage) {
        this.server.executeThread(new NetMessageProcessor(netMessage));
    }

    /**
     * 根据连接进来的客户端的ip地址，生成一个随机的id，并将id作为此次连接
     * 对它的唯一标识发送回去，告知客户端分配给它的id。
     */
    void creatIdForClientAndInform() {
        this.id = this.ip + " : " + System.currentTimeMillis();

        send(new NetMessage(null, NioServer.SERVER_ID, id, ETransferCommand.ID));
    }

    /**
     * 用传输信息是否发生异常来判断客户端是否还在线，
     * 如果发生了异常说明客户端是已经异常掉线了。
     */
    void checkClientIsAlive() {
        if (!alive) {
            return;
        }
        send(new NetMessage(null, NioServer.SERVER_ID, null, ETransferCommand.WHAT_IS_UP));
    }

    /**
     * 处理服务器的强制宕机命令 {@code ETransferCommand.FORCE_DOWN}
     */
    void forceDown() {
        if (!alive) {
            return;
        }
        send(new NetMessage(null, NioServer.SERVER_ID, null, ETransferCommand.FORCE_DOWN));
        close();
    }

    /**
     * 通过与客户端建立起的通信信道，向客户端传送消息。
     * 最终的实现由 {@link BaseCommunication} 完成
     *
     * @param action 需要执行的行为映射
     * @param parameter 具体的数据
     */
    void sendMessage(String action, String parameter) {
        if (!alive) {
            return;
        }
        send(new NetMessage(action, NioServer.SERVER_ID, parameter, ETransferCommand.MESSAGE));
    }

    /**
     * 处理 {@link ETransferCommand} 中的 {@code MESSAGE}
     * 命令。这个方法最终会被服务器轮询时建立的线程，通过调用
     * {@link NetMessageProcessor} 类来执行。处理客户端发来
     * 的请求。
     *
     * 这里的权限设置为public，因为这个方法将被反射调用。
     *
     * @param netMessage 接收到的客户端传送的信息
     */
    public void dealMessage(NetMessage netMessage) throws Exception {
        String action = netMessage.getAction();
        String parameter = netMessage.getParameter();

        this.sessionProcessor.dealRequest(action, parameter);
    }

    /**
     * 处理 {@link ETransferCommand} 中的 {@code OFFLINE}
     * 命令。这个方法最终会被服务器轮询时建立的线程，通过调用
     * {@link NetMessageProcessor} 类来执行。处理客户端发来
     * 的请求。
     *
     * 这里的权限设置为public，因为这个方法将被反射调用。
     *
     * @param netMessage 接收到的客户端传送的信息
     */
    public void dealOffline(NetMessage netMessage) {
        this.alive = false;
        this.server.speakOut("Client [" +
                this.id + "] is offline normally");
    }


    /**
     * @return 当前ServerConversation对象对应的id
     */
    String getId() {
        return id;
    }

    /**
     * @return 与此会话连接的客户端是否还在线
     */
    boolean isAlive() {
        return this.alive;
    }

    /**
     * 调用父类 {@link BaseServerCommunication} 的方法，
     * 做一个封装，使得 {@link NioServer} 可以通过此类调用receive
     *
     * @return 是否接收到消息
     */
    @Override
    protected boolean receive() {
        return super.receive();
    }

    /**
     * 由于我们的NIO采取的是轮询接收客户端传来的消息，如果轮循到了有消息
     * 就处理，否则就继续轮询。所以如果一个任务执行所耗费的时间太长，也会
     * 造成阻塞，无法继续进行轮询。因此，在每轮询到一个消息的时候，就使用
     * 线程池开启一个新的线程，处理轮询到的信息。
     *
     * {@link NioServer}
     */
    class NetMessageProcessor implements Runnable {
        private NetMessage netMessage;

        NetMessageProcessor(NetMessage netMessage) {
            this.netMessage = new NetMessage(netMessage.getAction(), NioServer.SERVER_ID,
                    netMessage.getParameter(), netMessage.getCommand());
        }

        @Override
        public void run() {
            try {
                TransferCommandProcessor.
                        resolveTransferCommandAndInvoke(
                                ServerConversation.this, this.netMessage);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
