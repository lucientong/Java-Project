package com.tyz.nio.communication;

import com.tyz.nio.protocol.MessageCanNotBeResolvedException;
import com.tyz.nio.protocol.NetMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 客户端会话层的中间层，调用一个线程侦听和服务器的通信信道，完成消息处理
 * 的逻辑实现以及客户端异常掉线处理的逻辑实现。
 * {@link BaseCommunication} 第一层继承
 * @author tyz
 */
public abstract class BaseClientCommunication extends BaseCommunication implements Runnable {
    private volatile boolean goOn;

    private ThreadPoolExecutor threadPool;

    protected BaseClientCommunication(Socket socket) throws IOException {
        super(socket);
        this.goOn = true;
        this.threadPool = new ThreadPoolExecutor(1, 10,3000L,
                                TimeUnit.MILLISECONDS,
                                new LinkedBlockingDeque<>(10),
                                r -> new Thread(r, this.getClass().getSimpleName() + "-thread"));
        this.threadPool.execute(this);
    }

    /**
     * 按照NioFramework的通信协议接收到的信息
     * @param netMessage 接收的消息
     */
    public abstract void dealNetMessage(NetMessage netMessage);

    @Override
    protected void close() {
        this.goOn = false;
        this.threadPool.shutdown();
        super.close();
    }

    /**
     * 侦听服务器传送的消息
     */
    @Override
    public void run() {
        while (this.goOn) {
            try {
                String message = this.dis.readUTF();
                NetMessage netMessage = new NetMessage(message);

                dealNetMessage(netMessage);
            } catch (IOException e) {
                //这里出现异常，有可能是goOn被改成了false，这说明是客户端
                //正常下线。只有当goOn还是true的时候，才说明是对端的服务器
                //掉线了，这样就需要处理服务器掉线的情况。
                if (this.goOn) {
                    this.goOn = false;
                    dealOppositeEndAbnormalDrop();
                }
            } catch (MessageCanNotBeResolvedException e) {
                e.printStackTrace();
                this.goOn = false;
            }
        }
        close();
    }
}
