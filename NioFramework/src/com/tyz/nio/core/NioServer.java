package com.tyz.nio.core;

import com.tyz.timer.core.IAssignment;
import com.tyz.timer.core.Timer;
import com.tyz.util.IPublisher;
import com.tyz.util.ISubscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tyz
 */
public class NioServer implements Runnable, ISubscriber {
    /** 服务器默认的端口号 */
    private static final int DEFAULT_SERVER_PORT = 18322;

    /** 服务器传送消息时的标识 */
    public static final String SERVER_ID = "SERVER";

    private int port;
    private volatile boolean goOn;
    private ServerSocket serverSocket;

    private ClientPool clientPool;

    private Timer timer;

    private List<IPublisher> publisherList;

    private ThreadPoolExecutor threadPool;

    public NioServer() {
        this(DEFAULT_SERVER_PORT);
    }

    public NioServer(int port) {
        this.port = port;
        this.goOn = false;
        this.timer = new Timer();
        this.clientPool = new ClientPool();
        this.publisherList = new ArrayList<>();
    }

    /**
     * 启动服务器
     */
    public void startUp() {
        if (this.goOn) {
            speakOut("Server had started already, can't start it again.");
            return;
        }
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.goOn = true;
            this.threadPool = new ThreadPoolExecutor(1, 1, 3000L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(),
                                                        r -> new Thread(r, "pool-" + r.getClass()));
            this.threadPool.execute(this);
            speakOut("Server start successfully.");

            //启动检测客户端状态的轮询器
            this.timer.setAssignment(new ClientMonitor()).setDelayTime(3000L);
            this.timer.startUp();

            //启动检测客户端消息的轮询器
            this.threadPool.execute(new ClientMessageListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭服务器
     */
    public void shutDown() {
        if (!this.goOn) {
            speakOut("Server had already closed, can't close again.");
            return;
        }
        if (!this.clientPool.isEmpty()) {
            speakOut("There still are clients online, please disconnected with them first.");
            return;
        }
        this.timer.stop();
        close();
        speakOut("Server is closed successfully.");
    }

    public void forceDown() {
        if (!this.goOn) {
            speakOut("Server had already closed, can't close again.");
            return;
        }
        if (this.clientPool.isEmpty()) {
            this.timer.stop();
            close();
            speakOut("Server is closed successfully.");
            return;
        }
        this.clientPool.processClients(ServerConversation::forceDown);
        close();
        speakOut("Server is force down.");
    }

    /**
     * 向 {@code clientPool} 中存在的单独一个客户端发送信息，服务器会将所需的信息
     * 传递到下一层，交由会话层 {@link ServerConversation} 完成具体的信息传送
     *
     * @param id 客户端id
     * @param action 需要执行的行为映射
     * @param parameter 具体的数据
     */
    public void sendMessage(String id, String action, String parameter) {
        this.clientPool.getClient(id).sendMessage(action, parameter);
    }

    /**
     * 向 {@code clientPool} 中存在的全部客户端发送信息，服务器会将所需的信息
     * 传递到下一层，交由会话层 {@link ServerConversation} 完成具体的信息传送
     *
     * @param action 需要执行的行为映射
     * @param parameter 具体的数据
     */
    public void sendMessage(String action, String parameter) {
        this.clientPool.processClients(client -> client.sendMessage(action, parameter));
    }

    /**
     * 向 {@code clientPool} 中存在的和 {@code targetList} 重合的客户端发送
     * 信息，服务器会将所需的信息传递到下一层，交由会话层 {@link ServerConversation}
     * 完成具体的信息传送
     *
     * @param targetList 要发送的客户端id列表
     * @param action 需要执行的行为映射
     * @param parameter 具体的数据
     */
    public void sendMessage(List<String> targetList, String action, String parameter) {
        this.clientPool.processClients(client -> {
            if (targetList.contains(client.getId())) {
                sendMessage(action, parameter);
            }
        });
    }

    /**
     * 侦听服务器接口，连接客户端
     */
    @Override
    public void run() {
        while (this.goOn) {
            try {
                Socket client = this.serverSocket.accept();
                ServerConversation serverConversation = new ServerConversation(this, client);

                serverConversation.creatIdForClientAndInform();
            } catch (IOException e) {
                this.goOn = false;
            }
        }
        close();
    }

    /**
     * 将一个服务器对客户端建立的会话 {@code client} 加入到连接
     * 着同样服务器的客户端的集合中 {@code clientPool}
     * @param client 对新连接的客户端建立的会话
     */
    void addClient(ServerConversation client) {
        this.clientPool.addClient(client);
    }

    /**
     * 从 {@code clientPool} 中将传入的 {@code client}
     * 移除
     * @param client 要删除的与客户端建立的会话层
     */
    void removeClient(ServerConversation client) {
        this.clientPool.removeClient(client);
    }

    void executeThread(Runnable task) {
        this.threadPool.execute(task);
    }

    @Override
    public void addPublisher(IPublisher iPublisher) {
        if (!this.publisherList.contains(iPublisher)) {
            this.publisherList.add(iPublisher);
        }
    }

    @Override
    public void removePublisher(IPublisher iPublisher) {
        this.publisherList.remove(iPublisher);
    }

    @Override
    public void speakOut(String s) {
        for (IPublisher publisher : this.publisherList) {
            publisher.dealMessage(s);
        }
    }

    /**
     * 关闭服务器socket和线程池
     */
    private void close() {
        this.goOn = false;
        if (this.serverSocket != null && this.serverSocket.isClosed()) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.serverSocket = null;
            }
        }
        this.threadPool.shutdown();
    }

    class ClientMonitor implements IAssignment {
        public ClientMonitor() {}

        @Override
        public void excute() {
            NioServer.this.clientPool.processClients(client -> {
                if (client.isAlive()) {
                    return;
                }
                client.checkClientIsAlive();
            });
            NioServer.this.clientPool.processClients(client -> {
                if (client.isAlive()) {
                    removeClient(client);
                }
            });
        }
    }

    /**
     * 对客户端进行轮询，如果在某个客户端的通信信道中收到了消息，
     * 就开启一个线程对消息进行处理，然后继续轮询。
     */
    class ClientMessageListener implements Runnable {
        public ClientMessageListener() {}

        @Override
        public void run() {
            while (NioServer.this.goOn) {
                NioServer.this.clientPool.processClients(c -> {
                    if (!c.isAlive()) {
                        return;
                    }
                    c.receive();
                });
            }
        }
    }
}
