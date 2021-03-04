package com.tyz.transmission.owner;

import com.tyz.registration.action.IRegistrationAuthorityAction;
import com.tyz.registration.information.OwnerInformation;
import com.tyz.rmi.core.RmiClient;
import com.tyz.rmi.core.RmiProxy;
import com.tyz.rmi.core.RmiServer;
import com.tyz.transmission.files.ResourceInformation;
import com.tyz.util.PropertiesParse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 资源拥有者，服务器端以及客户端程序。
 * 作为服务器，需要接受资源请求者的连接，并向资源接收者发送文件块；
 * 作为客户端，需要连接注册中心，完成资源的注册与注销。
 *
 * @author tyz
 */
public class ResourceOwner {
    /** RMI服务器 */
    private RmiServer rmiServer;

    /** 资源拥有者信息 */
    private static OwnerInformation ownerInformation;

    /** 注册中心功能 */
    private static IRegistrationAuthorityAction action;

    /** 资源拥有者所拥有的资源表 */
    private static Map<Integer, ResourceInformation> resourceInformationMap;

    /** 记录已经在注册中心注册的资源 */
    private Set<Integer> registeredResource;

    /**
     * 用户通过传入的端口号构造服务器
     *
     * @param port 端口号
     */
    public ResourceOwner(int port, String centerConfigFilePath) throws UnknownHostException {
        this.rmiServer = new RmiServer(port);
        this.registeredResource = new HashSet<>();

        String ip = InetAddress.getLocalHost().getHostAddress();

        ownerInformation = new OwnerInformation(port, ip);
        resourceInformationMap = new HashMap<>();

        initRegistrationAuthorityAction(centerConfigFilePath);
    }

    /**
     * 通过用户传入的properties配置文件构造服务器
     *
     * @param ownerConfigFilePath 资源拥有者服务器配置文件
     */
    public ResourceOwner(String ownerConfigFilePath, String centerConfigFilePath) {
        this.rmiServer = new RmiServer();
        this.rmiServer.initRmiServer(ownerConfigFilePath);

        int port = Integer.parseInt(PropertiesParse.getValue("rmi_server_port"));
        String ip = PropertiesParse.getValue("rmi_server_ip");

        ownerInformation = new OwnerInformation(port, ip);
        resourceInformationMap = new HashMap<>();
        this.registeredResource = new HashSet<>();

        initRegistrationAuthorityAction(centerConfigFilePath);
    }

    /**
     * 启动资源拥有者RMI服务器
     *
     * @throws IOException 启动服务器异常
     */
    public void startUp() throws IOException {
        this.rmiServer.startUp();
    }

    /**
     * 关闭资源拥有者RMI服务器
     */
    public void shutDown() {
        this.rmiServer.shutDown();
    }

    /**
     * 资源最初拥有者在注册中心注册资源
     *
     * @param resourceId 资源编号
     * @param resourceName 资源名称
     * @param absolutePath 资源绝对路径
     * @param fileName 资源文件名
     * @throws Exception 未找到对应的资源
     */
    public void registryResource(int resourceId, String resourceName, String absolutePath,
                                                            String fileName) throws Exception {
        ResourceInformation resourceInformation =
                                new ResourceInformation(resourceId, absolutePath);
        resourceInformation.scanResourceFiles(fileName);
        action.registryResource(resourceId, resourceName, resourceInformation);
    }

    /**
     * 资源最初拥有者在注册中心注销编号为 {@code resourceId} 的资源
     *
     * @param resourceId 资源编号
     */
    public void logoutResource(int resourceId) {
        action.logoutResource(resourceId);
    }

    /**
     * 资源最初拥有者在注册中心注销名称为 {@code resourceName} 的资源
     *
     * @param resourceName 资源名称
     */
    public void logoutResource(String resourceName) {
        action.logoutResource(resourceName);
    }

    /**
     * 资源拥有者在注册中心注册资源
     *
     * @param resourceId 资源编号
     */
    public void registryResourceOwner(int resourceId) {
        action.registryResourceOwner(resourceId, ownerInformation);
        this.registeredResource.add(resourceId);
    }

    /**
     * 资源拥有者在注册中心注销资源
     *
     * @param resourceId 资源编号
     */
    public void logoutResourceOwner(int resourceId) {
        action.logoutResourceOwner(resourceId, ownerInformation);
        this.registeredResource.remove(resourceId);

        if (this.registeredResource.isEmpty()) {
            shutDown();
        }
    }

    /**
     * 资源拥有者在注册中心注销它注册过的所有资源
     */
    public void logoutResourceOwner() {
        action.logoutResourceOwner(ownerInformation);
        shutDown();
    }

    /**
     * 在资源拥有者的资源表中添加一个资源
     *
     * @param resourceId 资源编号
     * @param resourceInformation 资源详细信息
     */
    public void addResourceInformation(int resourceId, ResourceInformation resourceInformation) {
        resourceInformationMap.put(resourceId, resourceInformation);
    }

    /**
     * 在资源拥有者的资源表中删除一个资源
     *
     * @param resourceId 资源编号
     */
    public void removeResourceInformation(int resourceId) {
        resourceInformationMap.remove(resourceId);
    }

    /**
     * @return 资源拥有者信息
     */
    static OwnerInformation getOwnerInformation() {
        return ownerInformation;
    }

    /**
     * @return 注册中心远程方法调用的接口
     */
    static IRegistrationAuthorityAction getAction() {
        return action;
    }

    /**
     * @return 资源拥有者拥有的资源编号和资源详细信息的映射
     */
    static Map<Integer, ResourceInformation> getResourceInformationMap() {
        return resourceInformationMap;
    }

    /**
     * 根据用户配置的注册中心服务器的配置文件，初始化 {@code action}
     *
     * @param configFilePath 注册中心服务器的配置文件路径
     */
    private void initRegistrationAuthorityAction(String configFilePath) {
        PropertiesParse.loadPropreties(configFilePath);

        String ip = PropertiesParse.getValue("rmi_server_ip");
        int port = Integer.parseInt(PropertiesParse.getValue("rmi_server_port"));

        action = new RmiProxy(new RmiClient(port, ip))
                                .getProxy(IRegistrationAuthorityAction.class);
    }
}
