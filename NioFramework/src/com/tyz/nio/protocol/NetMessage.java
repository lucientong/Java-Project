package com.tyz.nio.protocol;

import sun.nio.ch.Net;

import java.util.Arrays;
import java.util.List;

/**
 * 定义端口之间通信的信息格式
 * @author tyz
 */
public class NetMessage {
    /** 成员变量的数量 */
    private static final int FIELD_NUM = 4;

    private String action;
    private String source;
    private String parameter;
    private ETransferCommand command;

    /**
     * 初始化一个空信息体
     */
    public NetMessage() {}

    /**
     * 将传递进来的 {@code message} 按照规定解码，解析出四个成员变量
     *
     * @param message action, source, parameter, command编码得到的字符串
     * @throws MessageCanNotBeResolvedException 编码格式无法解析
     */
    public NetMessage(String message) throws MessageCanNotBeResolvedException {
        String[] args = message.split(".");

        if (args.length != FIELD_NUM) {
            throw new MessageCanNotBeResolvedException("Can't resolve message like [" +
                    message + "].");
        }

        this.action = args[0];
        this.source = args[1];
        this.parameter = args[2];
        this.command = ETransferCommand.valueOf(args[3]);
    }

    /**
     * 初始化NetMessage，使得按照协议进行传输
     * @param action 需要执行的功能
     * @param parameter 执行 {@code action} 需要的参数
     * @param command 协议规定的命令 {@link ETransferCommand}
     */
    public NetMessage(String action, String source, String parameter, ETransferCommand command) {
        this.action = action;
        this.source = source;
        this.parameter = parameter;
        this.command = command;
    }

    /**
     * 将信息体编码成一个字符串
     * @return 编码后的字符串
     */
    @Override
    public String toString() {
        List<String> list = Arrays.asList(action == null ? "" : action,
                                        source == null ? "" : source,
                                        parameter == null ? "" : parameter,
                                        command == null ? "" : command.toString());
        return String.join(".", list);
    }

    public String getAction() {
        return action;
    }

    public String getParameter() {
        return parameter;
    }

    public ETransferCommand getCommand() {
        return command;
    }
}
