package com.tyz.csframework.core;

import com.tyz.csframework.actionbean.DefaultSessionImpl;
import com.tyz.csframework.actionbean.ISessionProcessor;
import com.tyz.csframework.protocol.MessagePackage;
import com.tyz.csframework.useraction.IServerAction;
import com.tyz.csframework.useraction.ServerActionAdapter;
import com.tyz.util.IPublisher;
import com.tyz.util.ISubscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务器的实现
 *
 * @author tyz
 */
public class Server implements Runnable, ISubscriber {
    /** 默认端口号 */
    public static final int DEFAULT_PORT = 18322;

    private int port;
    private volatile boolean goOn;
    private ServerSocket serverSocket;

    private Set<IPublisher> publisherSet;

    private ThreadPoolExecutor threadPool;

    private ClientPool clientPool;

    /** 服务器完成的一些功能 */
    private IServerAction serverAction;

    /** 服务器对客户端的响应 */
    private ISessionProcessor sessionProcessor;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
        this.publisherSet = new HashSet<>();
        this.clientPool = new ClientPool();
        this.serverAction = new ServerActionAdapter();
        this.sessionProcessor = new DefaultSessionImpl();
    }

    /**
     * 启动服务器
     */
    public void startUp() throws IOException {
        if (isRunning()) {
            speakOut("Server had started yet, can't start again.");
            return;
        }
        this.serverSocket = new ServerSocket(this.port);
        this.goOn = true;
        speakOut("Server starts successfully.");

        //初始化线程池，将侦听客户端连接的线程启动
        this.threadPool = new ThreadPoolExecutor(1, 10, 3000L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<>(10),
                r -> new Thread(this.getClass().getSimpleName() + "-thread"));

        this.threadPool.execute(this);
    }

    /**
     * 侦听客户端的连接，若客户端的数量超过了{@code clientPool}
     * 的大小，则关闭与客户端建立的连接。如果客户端的数量还小于客户
     * 池的容量，则会把客户端加到客户池中，并为客户端 {@code client}
     * 设置服务器生成的{@code id}
     */
    @Override
    public void run() {
        speakOut("Start to listening...");
        while (this.goOn) {
            try {
                Socket socket = this.serverSocket.accept();
                ServerConversation client = new ServerConversation(this, socket);

                String id = socket.getLocalAddress().getHostAddress()
                                                + "-" + System.currentTimeMillis();
                if (this.clientPool.addClient(id, client)) {
                    client.setId(id);
                    speakOut("Client [" + id + "] connected with server successfully.");
                    client.clientIsOnline();
                } else {
                    client.rejectClient();
                    client.close();
                }
            } catch (IOException e) {
                this.goOn = false;
            }
        }
    }

    /**
     * @return 服务器是否在运行中
     */
    public boolean isRunning() {
        return this.goOn;
    }

    /**
     * 关闭服务器
     */
    public void shutDown() {
        if (!isRunning()) {
            speakOut("Server had closed yet, can't close again.");
            return;
        }
        if (!this.clientPool.isEmpty()) {
            speakOut("Still are some clients on, can't shut down.");
            return;
        }
        close();
        speakOut("Server has shut down successfully.");
    }

    /**
     * 设置可连接的客户端最大数量，如果不设置，默认为 (1 << 16)
     *
     * @param size 服务器最多可承载客户端的数量
     */
    public void setClientMaxCount(int size) {
        this.clientPool.setClientPoolCapacity(size);
    }

    /**
     * 服务器强制宕机，断开所有在线的客户端连接
     */
    public void forceDown() {
        if (!this.clientPool.isEmpty()) {
            for (ServerConversation client : this.clientPool.getAllClients()) {
                client.serverForceDown();
            }
        }
        close();
        speakOut("Server is force down.");
    }

    /**
     * 将id为 {@code id} 的客户端强制下线，给出理由 {@code reason}
     *
     * @param id 要强制下线的客户端id
     * @param reason 强制下线的理由
     */
    public void killClient(String id, String reason) {
        ServerConversation client = this.clientPool.getClient(id);
        if (client == null) {
            speakOut("Client [" + id +"] is not exist.");
            return;
        }
        client.killClient(id, reason);
        speakOut("Client [" + id + "] was killed because that [" + reason + "].");
    }

    /**
     * 获取所有在线客户端ID
     *
     * @return 所有在线客户端ID
     */
    public List<String> getAllOnlineClients() {
        List<String> clientList = new ArrayList<>();

        for (ServerConversation client : this.clientPool.getAllClients()) {
            clientList.add(client.getId());
        }
        return clientList;
    }

    /**
     * 服务器接收到客户端发送来要一对一传送的消息，判断要转发的另一个客户端是否存在，
     * 如果存在则将原消息转发，如果不存在则传回消息给发送消息的客户端，告知目标客户端
     * 不存在。
     *
     * @param mp 要传送的消息
     */
    void talkToOne(MessagePackage mp) {
        ServerConversation targetClient = this.clientPool.getClient(mp.getTarget());

        if (targetClient == null) {
            ServerConversation sourceClient = this.clientPool.getClient(mp.getSource());
            sourceClient.targetIsNotExist(mp.getTarget());
        } else {
            targetClient.talkToOne(mp);
        }
    }

    /**
     * 服务器将消息转发给除发送源以外的所有客户端
     *
     * @param mp 消息体
     */
    void talkToAll(MessagePackage mp) {
        String sourceId = mp.getSource();

        List<ServerConversation> list = this.clientPool.getAllClients();

        for (ServerConversation client : list) {
            if (client.getId().equals(sourceId)) {
                continue;
            }
            client.talkToAll(mp);
        }
    }

    /**
     * 服务器处理客户端正常下线
     *
     * @param id 下线客户端id
     */
    void clientOffline(String id) {
        this.clientPool.removeClient(id);
        speakOut("Client [" + id + "] is offline.");
        this.serverAction.clientOffline(id);
    }

    /**
     * 关闭socket和线程池，这里对异常无需做处理，直接使得指向它
     * 的指针为null就可以了。
     */
    private void close() {
        if (this.serverSocket != null && this.serverSocket.isClosed()) {
            try {
                this.serverSocket.close();
            } catch (IOException ignored) {
            } finally {
                this.serverSocket = null;
            }
        }
        this.goOn = false;
        this.threadPool.shutdown();
    }

    /**
     * 处理客户端异常掉线情况，该方法最终由 {@link ServerConversation}
     * 调用。
     *
     * @param client 异常掉线的客户端
     */
    void dealClientAbnormalDisconnected(ServerConversation client) {
        speakOut("Client [" +
                client.getId() + "] is abnormally disconnected.");
        this.serverAction.dealClientAbnormalDisconnected(client);
    }

    /**
     * 服务器处理客户端的请求
     *
     * @param action 请求的行为
     * @param parameter 参数
     * @return String 服务器响应的结果
     */
    String dealRequest(String action, String parameter) throws Exception {
        return this.sessionProcessor.dealRequest(action, parameter);
    }

    @Override
    public void addPublisher(IPublisher iPublisher) {
        this.publisherSet.add(iPublisher);
    }

    @Override
    public void removePublisher(IPublisher iPublisher) {
        this.publisherSet.remove(iPublisher);
    }

    @Override
    public void speakOut(String s) {
        for (IPublisher publisher : publisherSet) {
            publisher.dealMessage(s);
        }
    }
}
