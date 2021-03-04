<<<<<<< HEAD
package com.tyz.csframework.protocol;

/**
 * @author tyz
 */
public class NetMessage {
    /** 分发器的行为 */
    private String action;

    /** 数据 */
    private String parameter;

    /** 执行的命令 */
    private ETransferCommand command;

    public NetMessage(String action, String parameter, ETransferCommand command) {
        this.action = action;
        this.parameter = parameter;
        this.command = command;
    }

    /**
     * 对接收到的字符串进行解码，转换成{@link NetMessage}
     *
     * @param message 接收到的字符串
     */
    public NetMessage(String message) {
        String[] words = message.split(":");

        this.action = words[0];
        this.parameter = words[1];
        this.command = ETransferCommand.valueOf(words[2]);
    }

    /**
     * @return 分发器的行为
     */
    public String getAction() {
        return action;
    }

    /**
     * @return 对端传送的数据
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @return 对端执行的命令
     */
    public ETransferCommand getCommand() {
        return command;
    }

    /**
     * 将协议的信息进行编码
     *
     * @return 编码好的 {@link NetMessage}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.action == null ? "" : this.action).append(':')
            .append(this.parameter == null ? "" : this.parameter).append(':')
            .append(this.command == null ? "" : this.command.name());

        return sb.toString();
    }
}
=======
package com.tyz.csframework.protocol;

/**
 * @author tyz
 */
public class NetMessage {
    /** 分发器的行为 */
    private String action;

    /** 数据 */
    private String parameter;

    /** 执行的命令 */
    private ETransferCommand command;

    public NetMessage(String action, String parameter, ETransferCommand command) {
        this.action = action;
        this.parameter = parameter;
        this.command = command;
    }

    /**
     * 对接收到的字符串进行解码，转换成{@link NetMessage}
     *
     * @param message 接收到的字符串
     */
    public NetMessage(String message) {
        String[] words = message.split(":");

        this.action = words[0];
        this.parameter = words[1];
        this.command = ETransferCommand.valueOf(words[2]);
    }

    /**
     * @return 分发器的行为
     */
    public String getAction() {
        return action;
    }

    /**
     * @return 对端传送的数据
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @return 对端执行的命令
     */
    public ETransferCommand getCommand() {
        return command;
    }

    /**
     * 将协议的信息进行编码
     *
     * @return 编码好的 {@link NetMessage}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.action == null ? "" : this.action).append(':')
            .append(this.parameter == null ? "" : this.parameter).append(':')
            .append(this.command == null ? "" : this.command.name());

        return sb.toString();
    }
}
>>>>>>> d4037e3d4c4890c361da0833e8f35f765b5789b1
