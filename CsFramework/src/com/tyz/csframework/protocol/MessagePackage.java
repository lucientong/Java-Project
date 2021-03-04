package com.tyz.csframework.protocol;

/**
 * 封装一条消息，根据此类可以得到发送消息的源id，
 * 要送达的目标id以及消息本身
 *
 * @author tyz
 */
public class MessagePackage {
    /** 消息源id */
    private String source;

    /** 目标id */
    private String target;

    /** 消息 */
    private String message;

    public MessagePackage(String source, String target, String message) {
        this.source = source;
        this.target = target;
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }
}
