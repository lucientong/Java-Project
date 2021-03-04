package com.tyz.util;

/**
 * 发布者
 *
 * @author tyz
 */
public interface IPublisher {
    /**
     * 处理订阅者的消息
     * @param message 订阅者的消息
     */
    void dealMessage(String message);
}
