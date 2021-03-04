package com.tyz.nio.useraction;

/**
 * 定义客户端需要实现的接口
 * @author tyz
 */
public interface IClientAction {
    /** 客户端处理服务器异常断开连接的情况 */
    void dealServerExecuteForceDown();

    /**
     * 客户端确认是否下线
     *
     * @return 客户端确认下线
     */
    boolean ensureIfOffline();

    /** 客户端处理下线之后的状态 */
    void afterOffline();

    /** 客户端处理服务器异常掉线的情况 */
    void dealServerAbnormalDrop();
}
