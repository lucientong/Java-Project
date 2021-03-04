package com.tyz.registration.information;

import java.util.Objects;

/**
 * 描述资源拥有者的基本信息，在发送资源之前，资源请求者需要
 * 通过资源拥有者服务器的端口号和ip地址，向对应的资源拥有者
 * 发送资源请求。
 *
 * @author tyz
 */
public class OwnerInformation {
    /** 资源拥有者服务器端口号 */
    private int port;

    /** 资源拥有者服务器ip地址 */
    private String ip;

    /** 资源拥有者健康值，值越小越健康，超过阈值则不能被选作发送端 */
    private int healthValue;

    /** 资源拥有者所拥有的资源数 */
    private int resourceCount;

    public OwnerInformation(int port, String ip) {
        this.port = port;
        this.ip = ip;
        this.healthValue = 0;
        this.resourceCount = 0;
    }

    /**
     * 正在发送的资源数加一，增加资源拥有者的健康值
     */
    void incrementHealthValue() {
        this.healthValue++;
    }

    /**
     * 正在发送的资源数减一，减少资源拥有者的健康值
     */
    void decrementHealthValue() {
        this.healthValue--;
    }

    /**
     * 资源拥有者注册的资源数加一
     */
    void incrementResourceCount() {
        this.resourceCount++;
    }

    /**
     * 资源拥有者注册的资源数减一
     */
    void decrementResourceCount() {
        this.resourceCount--;
    }

    /**
     * @return 资源拥有者服务器端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * @return 资源拥有者服务器ip地址
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return 资源拥有者的健康值
     */
    public int getHealthValue() {
        return healthValue;
    }

    /**
     * @return 资源拥有者注册的资源数
     */
    public int getResourceCount() {
        return resourceCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OwnerInformation that = (OwnerInformation) o;
        return port == that.port &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, ip);
    }
}
