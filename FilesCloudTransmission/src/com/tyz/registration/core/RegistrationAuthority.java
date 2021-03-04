package com.tyz.registration.core;

import com.tyz.rmi.core.RemoteMethodScanner;
import com.tyz.rmi.core.RmiServer;
import com.tyz.util.IPublisher;
import com.tyz.util.ISubscriber;

import java.io.IOException;

/**
 * 注册中心
 *
 * @author tyz
 */
public class RegistrationAuthority implements ISubscriber {
    /** RMI 服务器 */
    private RmiServer rmiServer;

    /**
     * 使用默认的端口号 12710 构造服务器
     */
    public RegistrationAuthority() {
        this.rmiServer = new RmiServer();
    }

    /**
     * 根据用户传入的端口号构造服务器
     *
     * @param port 端口号
     */
    public RegistrationAuthority(int port) {
        this.rmiServer = new RmiServer(port);
    }

    /**
     * 根据用户配置的 properties 文件构造服务器
     *
     * @param serverConfigFilePath xml配置文件的路径
     */
    public RegistrationAuthority(String serverConfigFilePath) {
        this.rmiServer = new RmiServer();
        this.rmiServer.initRmiServer(serverConfigFilePath);
    }

    /**
     * 启动注册中心的RMI服务器
     *
     * @throws IOException 启动服务器异常
     */
    public void startUp() throws IOException {
        RemoteMethodScanner.scan("/interfaces.orm.xml");
        this.rmiServer.startUp();
    }

    /**
     * 关闭注册中心的RMI服务器
     */
    public void shutDown() {
        this.rmiServer.shutDown();
    }

    /**
     * @return 注册中心RMI服务器是否处在运行状态
     */
    public boolean isRunning() {
        return this.rmiServer.isRunning();
    }

    @Override
    public void addPublisher(IPublisher iPublisher) {
        this.rmiServer.addPublisher(iPublisher);
    }

    @Override
    public void removePublisher(IPublisher iPublisher) {
        this.rmiServer.removePublisher(iPublisher);
    }

    @Override
    public void speakOut(String s) {
        this.rmiServer.speakOut(s);
    }
}
