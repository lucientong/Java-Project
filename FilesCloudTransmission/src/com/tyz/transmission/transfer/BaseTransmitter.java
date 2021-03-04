package com.tyz.transmission.transfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 传输文件的基类，实现基本的向对端传送数据的功能
 *
 * @author tyz
 */
public class BaseTransmitter {

    protected BaseTransmitter() {}

    /**
     * 使用 {@code out} 向对端发送字节流文件 {@code bytes}
     *
     * @param out 发送数据的输出流
     * @param bytes 需要发送的字节流
     * @throws IOException 向对端发送数据失败
     */
    protected void send(OutputStream out, byte[] bytes) throws IOException {
        out.write(bytes);
    }

    /**
     * 使用 {@code in} 接收数据，需要接收的长度为 {@code length}
     *
     * @param in 接收数据的输入流
     * @param length 接收数据的长度
     * @return 接收的二进制字节流数组
     * @throws IOException 从对端接收数据失败
     */
    protected byte[] recive(InputStream in, long length) throws IOException {
        byte[] bytes = new byte[(int) length];

        in.read(bytes);

        return bytes;
    }
}
