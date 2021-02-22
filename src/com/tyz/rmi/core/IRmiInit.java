package com.tyz.rmi.core;

/**
 * @author tyz
 */
public interface IRmiInit {
    /**
     * 初始化Rmi服务器的端口号
     * @param port 端口号
     */
    void setPort(int port);

    /**
     * 初始化Rmi服务器的IP
     * @param ip IP地址
     */
    void setIp(String ip);
}
