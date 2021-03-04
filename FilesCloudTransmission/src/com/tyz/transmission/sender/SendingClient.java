package com.tyz.transmission.sender;

import com.tyz.transmission.files.ResourceInformation;
import com.tyz.transmission.protocol.SectionHeader;
import com.tyz.transmission.receiver.ReceivingServer;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * 发送端客户端程序
 *
 * @author tyz
 */
public class SendingClient {

    /** 默认接收端服务器ip */
    public static final String DEFAULT_SERVER_IP = "127.0.0.1";

    /** 接收端服务器端口号 */
    private int receivingServerPort;

    /** 接收端服务器ip */
    private String receivingServerIp;

    /** 资源信息 */
    private ResourceInformation resourceInformation;

    /** 发送任务列表 */
    private List<SectionHeader> assignmentList;

    public SendingClient(ResourceInformation resourceInformation, List<SectionHeader> assignmentList) {
        this(ReceivingServer.DEFAULT_SERVER_PORT, DEFAULT_SERVER_IP, resourceInformation, assignmentList);
    }

    public SendingClient(int receivingServerPort, String receivingServerIp, ResourceInformation resourceInformation, List<SectionHeader> assignmentList) {
        this.receivingServerPort = receivingServerPort;
        this.receivingServerIp = receivingServerIp;
        this.resourceInformation = resourceInformation;
        this.assignmentList = assignmentList;
    }

    /**
     * 连接接收端服务器，调用 {@code sendingEnd}，发送文件块
     *
     * @throws IOException 连接服务器失败
     */
    public void send() throws IOException {
        Socket socket = new Socket(this.receivingServerIp, this.receivingServerPort);

        SendingEnd sendingEnd = new SendingEnd(socket, this.assignmentList, this.resourceInformation);

        sendingEnd.startToSend();
    }
}
