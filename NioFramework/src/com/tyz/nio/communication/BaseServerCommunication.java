package com.tyz.nio.communication;

import com.tyz.nio.protocol.MessageCanNotBeResolvedException;
import com.tyz.nio.protocol.NetMessage;

import java.io.IOException;
import java.net.Socket;

/**
 * 服务器会话层的中间层，实现用NIO的方式接受来自客户端的数据。
 * {@link BaseCommunication} 的第一层继承
 * @author tyz
 */
public abstract class BaseServerCommunication extends BaseCommunication {

    protected BaseServerCommunication(Socket socket) throws IOException {
        super(socket);
    }

    /**
     * 按照NioFramework的通信协议接收到的信息
     * @param netMessage 接收的消息
     */
    public abstract void dealNetMessage(NetMessage netMessage);

    /**
     * NIO实现的逻辑是，在服务器的更高层，轮询执行receive方法，当
     * 前轮循到的通信信道中有消息了就读入，没消息就继续轮询下一个
     *
     * @return 是否成功读到了消息
     */
    protected boolean receive() {
        try {
            int count = this.dis.available();

            if (count > 0) {
                String message = this.dis.readUTF();
                NetMessage netMessage = new NetMessage(message);

                dealNetMessage(netMessage);
            }
        } catch (IOException | MessageCanNotBeResolvedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
