package com.tyz.util;

/**
 * 订阅者
 *
 * @author tyz
 */
public interface ISubscriber {
    /**
     * 增加一个发布者
     * @param publisher 发布者
     */
    void addPublisher(IPublisher publisher);

    /**
     * 删除一个发布者
     * @param publisher 发布者
     */
    void removePublisher(IPublisher publisher);

    /**
     * 订阅发布者要处理的消息
     * @param message 发布者要处理的消息
     */
    void speakOut(String message);
}
