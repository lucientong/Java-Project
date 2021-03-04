package com.tyz.transmission.receiver;

import com.tyz.transmission.files.RandomAccessFilePool;
import com.tyz.transmission.files.ResourceInformation;
import com.tyz.transmission.protocol.UnreceivedSectionPool;
import com.tyz.util.IPublisher;
import com.tyz.util.ISubscriber;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 接收端服务器程序
 *
 * @author tyz
 */
public class ReceivingServer implements Runnable, ISubscriber {
    /** 等待发送端连接的最大时间 */
    public static final int WAITING_TIME = 2000;

    /** 默认接收端服务器端口号 */
    public static final int DEFAULT_SERVER_PORT = 18322;

    /** 接收端服务器端口号 */
    private int port;

    /** 接收端服务器套接字 */
    private ServerSocket serverSocket;

    /** 服务器状态 */
    private volatile boolean goOn;

    /** 发布者列表 */
    private Set<IPublisher> publisherSet;

    /** 线程池 */
    private ExecutorService threadPool;

    /** 未接收到的文件块池 */
    private UnreceivedSectionPool unreceivedSectionPool;

    /** 随机文件访问流池 */
    private RandomAccessFilePool randomAccessFilePool;

    /** 发送端数量 */
    private int sendingEndCount;

    /** 处理接收文件时异常情况 */
    IAfterTransferFailed afterTransferFailed;

    public ReceivingServer(int sendingEndCount, ResourceInformation resourceInformation,
                                                    IAfterTransferFailed afterTransferFailed) {
        this(DEFAULT_SERVER_PORT, sendingEndCount, resourceInformation, afterTransferFailed);
    }

    public ReceivingServer(int port, int sendingEndCount, ResourceInformation resourceInformation,
                                                        IAfterTransferFailed afterTransferFailed) {
        this.port = port;
        this.publisherSet = new HashSet<>();
        this.sendingEndCount = sendingEndCount;
        this.afterTransferFailed = afterTransferFailed;
        this.unreceivedSectionPool = new UnreceivedSectionPool(resourceInformation);
        this.randomAccessFilePool = new RandomAccessFilePool(resourceInformation);
        this.threadPool = new ThreadPoolExecutor(1, 1, 0,
                                                TimeUnit.MILLISECONDS,
                                                new LinkedBlockingDeque<>(1),
                                                r -> new Thread(r, "receiving server"));
    }

    /**
     * 启动接收端服务器
     *
     * @throws IOException 启动接收端服务器失败
     */
    public void startUp() throws IOException {
        if (this.goOn) {
            speakOut("Failed to start: <Receiving server had already started.>");
            return;
        }
        this.serverSocket = new ServerSocket(this.port);
        this.goOn = true;
        this.threadPool.execute(this);
        speakOut("Receiving server started successfully.");
    }

    /**
     * 关闭接收端服务器
     */
    public void shutDown() {
        if (!this.goOn) {
            speakOut("Failed to shut down: <Receiving server had already closed.>");
            return;
        }
        close();
        speakOut("Receiving server closed successfully.");
    }

    @Override
    public void run() {
        int count = 0;
        long start = System.currentTimeMillis();

        while (count < this.sendingEndCount && this.goOn) {
            long now = System.currentTimeMillis();
            if (now - start >= WAITING_TIME) {
                this.goOn = false;
                this.afterTransferFailed.executeResumeFromBreakPoint(this.unreceivedSectionPool);
            }
            try {
                Socket socket = this.serverSocket.accept();
                new ReceivingEnd(socket, this.randomAccessFilePool, this.unreceivedSectionPool,
                                                                        this.afterTransferFailed);
                count++;
            } catch (IOException e) {
                this.goOn = false;
            }
        }
       shutDown();
    }

    /**
     * 关闭套接字和线程池
     */
    private void close() {
        this.goOn = false;
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            try {
                this.serverSocket.close();
            } catch (IOException ignored) {
            } finally {
                this.serverSocket = null;
            }
        }
        this.threadPool.shutdown();
    }

    @Override
    public void addPublisher(IPublisher publisher) {
        this.publisherSet.add(publisher);
    }

    @Override
    public void removePublisher(IPublisher publisher) {
        this.publisherSet.remove(publisher);
    }

    @Override
    public void speakOut(String message) {
        for (IPublisher publisher : this.publisherSet) {
            publisher.dealMessage(message);
        }
    }
}
