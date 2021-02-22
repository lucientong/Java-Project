package com.tyz.rmi.core;

import com.tyz.util.PropertiesParse;

/**
 * 实现端口号以及ip地址的初始化工作
 *
 * @author tyz
 */
public class RmiInitializer {
    static final int DEFAULT_PORT = 12710;
    static final String DEFAULT_IP = "127.0.0.1";

    public RmiInitializer() {}

    /**
     * 对接口类对象进行端口号和IP地址的初始化，会先从用户传入的cfgFilePath
     * 找对应的properties配置文件，解析端口号和IP地址，若出现异常或者用户
     * 没有进行配置，都会传入默认的端口号和IP地址。
     * @param rmiInit 初始化方法接口
     * @param cfgFilePath properties配置文件路径
     */
    static void initialize(IRmiInit rmiInit, String cfgFilePath) {
        PropertiesParse.loadPropreties(cfgFilePath);

        String portStr = PropertiesParse.getValue("rmi_server_port");

        if (portStr == null || portStr.length() <= 0) {
            rmiInit.setPort(DEFAULT_PORT);
        } else {
            try {
                rmiInit.setPort(Integer.parseInt(portStr));
            } catch (NumberFormatException e) {
                rmiInit.setPort(DEFAULT_PORT);
            }
        }

        String ip = PropertiesParse.getValue("rmi_server_ip");

        if (ip == null || ip.length() <= 0) {
            ip = DEFAULT_IP;
        }
        rmiInit.setIp(ip);
    }
}
