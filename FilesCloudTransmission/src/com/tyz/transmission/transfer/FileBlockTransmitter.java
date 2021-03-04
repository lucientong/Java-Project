package com.tyz.transmission.transfer;

import com.tyz.transmission.protocol.SectionHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 继承 {@link BaseTransmitter},完成协议具体的发送和接收信息块的功能
 *
 * @author tyz
 */
public class FileBlockTransmitter extends BaseTransmitter {

    /** 文件块已经发送完毕的标识 */
    public static final int END_FLAG = -1;

    private IFileBlockProcessor fileBlockProcessor;

    public FileBlockTransmitter() {}

    /**
     * 初始化文件块传输器，用户必须实现处理文件块的接口
     *
     * @param fileBlockProcessor 处理文件块的接口
     */
    public FileBlockTransmitter(IFileBlockProcessor fileBlockProcessor) {
        this.fileBlockProcessor = fileBlockProcessor;
    }

    /**
     * 按照规则发送对端发来的文件数据，发送两次，第一次发送信息头，第二次发送真正的文件块
     *
     * @param out 发送数据的输出流
     * @throws IOException 发送数据失败
     */
    public void send(OutputStream out, SectionHeader sectionHeader, byte[] bytes) throws IOException {
        send(out, sectionHeader.toBytes());
        send(out, bytes);
    }

    /**
     * 文件块传输任务完成时调用此方法，向接收端告知发送任务完成。
     *
     * @param out 发送数据的输出流
     * @throws IOException 发送数据失败
     */
    public void missionIsAccomplished(OutputStream out) throws IOException {
        send(out, new SectionHeader(END_FLAG, 0L, 0L), new byte[0]);
    }

    /**
     * 按照规则接收对端发来的文件数据，接收两次，第一次接收信息头，第二次接收真正的文件块
     *
     * @param in 接收数据的输入流
     * @throws IOException 接收数据失败
     */
    public void recive(InputStream in) throws Exception {
        byte[] headerBytes = recive(in, SectionHeader.SECTION_HEADER_LENGTH);
        SectionHeader sectionHeader = new SectionHeader(headerBytes);

        byte[] fileBlock = recive(in, sectionHeader.getLength());
        this.fileBlockProcessor.dealFileBlock(sectionHeader, fileBlock);
    }
}
