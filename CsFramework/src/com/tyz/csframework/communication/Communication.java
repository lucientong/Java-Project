package com.tyz.csframework.communication;

import com.tyz.csframework.protocol.NetMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 会话层的基类，完成服务器和客户端共有的功能。
 *
 * @author tyz
 */
public abstract class Communication implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private volatile boolean goOn;

    private ThreadPoolExecutor threadPool;

    /**
     * 处理接收到的消息
     *
     * @param netMessage 规范的信息
     */
    public abstract void dealNetMessage(NetMessage netMessage);

    /**
     * 处理对端异常掉线
     */
    public abstract void dealOppositeEndAbnormalDrop();

    protected Communication(Socket socket) {
        this.socket = socket;
        this.goOn = true;
        try {
            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());

            this.threadPool = new ThreadPoolExecutor(1, 10, 3000L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(10),
                    r -> new Thread(this.getClass().getSimpleName() + "-thread"));

            this.threadPool.execute(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (this.goOn) {
            try {
                String message = this.dis.readUTF();
                dealNetMessage(new NetMessage(message));
            } catch (IOException e) {
                //读数据异常说明对端掉线，如果goOn已经是false，
                //说明是自己下线的，正常下线即可。如果goOn是true，
                //说明发生了对端异常掉线，这里需要处理。
                if (this.goOn) {
                    this.goOn = false;
                    dealOppositeEndAbnormalDrop();
                }
            }
        }
    }

    /**
     * 向对端发送框架规范的信息
     *
     * @param netMessage 需要传送的信息
     */
    protected void send(NetMessage netMessage) {
        try {
            this.dos.writeUTF(netMessage.toString());
        } catch (IOException e) {
            //发送数据失败说明是对端异常掉线
            close();
            dealOppositeEndAbnormalDrop();
        }
    }

    /**
     * 关闭通信信道和线程池
     */
    protected void close() {
        this.goOn = false;
        if (this.socket != null && this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException ignored) {
            } finally {
               this.socket = null;
            }
        }
        if (this.dis != null) {
            try {
                this.dis.close();
            } catch (IOException ignored) {
            } finally {
                this.dis = null;
            }
        }
        if (this.dos != null) {
            try {
                this.dos.close();
            } catch (IOException ignored) {
            } finally {
                this.dos = null;
            }
        }
        this.threadPool.shutdown();
    }
}
