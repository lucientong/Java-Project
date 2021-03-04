package com.tyz.nio.communication;

import com.tyz.nio.protocol.NetMessage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 会话层基类，实现基本的网络通信功能。完成socket之间通信信道的建立以及
 * 一个按照协议传输数据的规范 {@link com.tyz.nio.protocol.NetMessage}
 * @author tyz
 */
public abstract class BaseCommunication {
    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;

    protected BaseCommunication(Socket socket) throws IOException {
        this.socket = socket;
        this.dis = new DataInputStream(this.socket.getInputStream());
        this.dos = new DataOutputStream(this.socket.getOutputStream());
    }

    /**
     * 处理对端异常掉线的情况
     */
    public abstract void dealOppositeEndAbnormalDrop();

    /**
     * 向对端发送信息，如果此时发生了异常，说明应该是对端异常掉线了。
     * @param netMessage 协议化的信息 {@link com.tyz.nio.protocol.ETransferCommand}
     */
    protected void send(NetMessage netMessage) {
        try {
            this.dos.writeUTF(netMessage.toString());
        } catch (IOException e) {
            dealOppositeEndAbnormalDrop();
        }
    }

    /**
     * 关闭socket以及通信信道
     */
    protected void close() {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.socket = null;
            }
        }
        if (this.dis != null) {
            try {
                this.dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.dis = null;
            }
        }
        if (this.dos != null) {
            try {
                this.dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.dos = null;
            }
        }
    }
}
