package com.tyz.rmi.core;

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
 * RMI服务器
 * @author tong
 */
public class RmiServer implements Runnable, IRmiInit, ISubscriber {
    private int port;
    private volatile boolean goOn;
    private ServerSocket serverSocket;

    private List<IPublisher> publisherList;

    private ThreadPoolExecutor threadPool;

    public RmiServer() {
        this(RmiInitializer.DEFAULT_PORT);
    }

    public RmiServer(int port) {
        this.port = port;
        this.goOn = false;
        this.publisherList = new ArrayList<>();
    }

    /**
     * 调用 RmiInitializer 类实现服务器端口号的初始化
     * @param configFilePath properties配置文件路径
     * @see RmiInitializer
     */
    public void initRmiServer(String configFilePath) {
        RmiInitializer.initialize(this, configFilePath);
    }

    /**
     * 启动服务器
     */
    public void startUp() throws IOException {
        if (this.goOn) {
            speakOut("RMI server has started yet.");
            return;
        }
        this.goOn = true;
        this.serverSocket = new ServerSocket(this.port);
        this.threadPool = new ThreadPoolExecutor(4, 10, 300, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(4));
        this.threadPool.execute(this);
        speakOut("RMI server started successfully.");
        speakOut("Thread pool is started successfully.");
    }

    /**
     * 关闭服务器
     */
    public void shutDown() {
        if (!this.goOn) {
            speakOut("RMI server is closed now.");
            return;
        }
        speakOut("RMI server closed successfully.");
        speakOut("Thread pool is closed successfully.");
        closeSocket();
        this.threadPool.shutdown();
    }

    /**
     * 处理关闭套接字，简化暴露给用户的关闭服务器接口的逻辑
     */
    private void closeSocket() {
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            this.goOn = false;
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.serverSocket = null;
            }
        }
    }

    public boolean isRunning() {
        return this.goOn;
    }

    /**
     * 返回服务器的线程池
     */
    ThreadPoolExecutor getThreadPool() {
        return this.threadPool;
    }

    /**
     * 建立一个单独线程做侦听器
     */
    @Override
    public void run() {
        speakOut("RMI server is going to listening...");
        while (this.goOn) {
            try {
                Socket clientSocket = this.serverSocket.accept();
                new ClientRequestProcessor(this, clientSocket);
            } catch (IOException e) {
                closeSocket();
            }
        }
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 服务器类不需要配置IP地址，这里做空实现
     * @param ip IP地址
     */
    @Override
    public void setIp(String ip) {
    }

    @Override
    public void addPublisher(IPublisher iPublisher) {
        if (this.publisherList.contains(iPublisher)) {
            return;
        }
        this.publisherList.add(iPublisher);
    }

    @Override
    public void removePublisher(IPublisher iPublisher) {
        if (!this.publisherList.contains(iPublisher)) {
            return;
        }
        this.publisherList.remove(iPublisher);
    }

    @Override
    public void speakOut(String s) {
        for (IPublisher publisher : this.publisherList) {
            publisher.dealMessage(s);
        }
    }
}
