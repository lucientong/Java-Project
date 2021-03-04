package com.tyz.transmission.requester;

/**
 * 资源请求者信息
 *
 * @author tyz
 */
public class RequesterInformation {
    /** 资源请求者服务器端口号 */
    private int port;

    /** 资源请求者服务器ip地址 */
    private String ip;

    public RequesterInformation(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    /**
     * @return 资源请求者服务器端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * @return 资源请求者服务器ip地址
     */
    public String getIp() {
        return ip;
    }
}
