package com.tyz.transmission.requester;

import com.tyz.registration.action.IRegistrationAuthorityAction;
import com.tyz.registration.information.OwnerInformation;
import com.tyz.rmi.core.RmiClient;
import com.tyz.rmi.core.RmiProxy;
import com.tyz.transmission.files.FileInformation;
import com.tyz.transmission.files.FileSplitter;
import com.tyz.transmission.files.ResourceInformation;
import com.tyz.transmission.owner.IResourceOwnerAction;
import com.tyz.transmission.owner.ResourceOwner;
import com.tyz.transmission.protocol.SectionHeader;
import com.tyz.transmission.protocol.UnreceivedSectionPool;
import com.tyz.transmission.receiver.IAfterTransferFailed;
import com.tyz.transmission.receiver.ReceivingServer;
import com.tyz.util.PropertiesParse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author tyz
 */
public class ResourceRequester {
    /** 接收端服务器端口号 */
    private int receivingServerPort;

    /** 资源请求者信息 */
    private RequesterInformation requesterInformation;

    /** 资源详细信息 */
    private ResourceInformation resourceInformation;

    /** 分发策略 */
    private IDistributionStrategy distributionStrategy;

    /** 需要进行远程方法调用的注册中心功能 */
    private IRegistrationAuthorityAction registrationAuthorityAction;

    /** 需要进行远程方法调用的资源拥有者功能 */
    private IResourceOwnerAction resourceOwnerAction;

    /** 资源拥有者 */
    private ResourceOwner resourceOwner;

    /** 判断资源拥有者是否第一次注册资源 */
    private volatile boolean isFirstRegister;

    /**
     * 通过用户传进来的 {@code receivingServerPort} 构造接收端服务器
     * 通过用户的注册中心服务器配置文件 {@code configFilePath} 初始化
     *                      {@code registrationAuthorityAction}
     *
     * @param receivingServerPort 接收端服务器端口号
     * @param configFilePath 注册中心服务器配置文件路径
     */
    public ResourceRequester(int receivingServerPort, String configFilePath) throws UnknownHostException {
        this.receivingServerPort = receivingServerPort;
        this.distributionStrategy = new DefaultDistributionStrategyImpl();

        this.resourceOwner = new ResourceOwner(receivingServerPort, configFilePath);
        this.isFirstRegister = true;

        String ip = InetAddress.getLocalHost().getHostAddress();
        this.requesterInformation = new RequesterInformation(receivingServerPort, ip);

        initRegistrationAuthorityAction(configFilePath);
    }

    /**
     * 资源拥有者在注册中心注销资源
     *
     * @param resourceId 资源编号
     */
    public void logoutResourceOwner(int resourceId) {
        this.resourceOwner.logoutResourceOwner(resourceId);
    }

    /**
     * 资源拥有者在注册中心注销它注册过的所有资源
     */
    public void logoutResourceOwner() {
        this.resourceOwner.logoutResourceOwner();
    }

    /**
     * 从注册中心获取资源表，以供用户根据资源名称选择需要的资源
     */
    public void receiveResourceNameMap(IRequesterAction requesterAction) {
        requesterAction.dealResourceNameMap(
                            this.registrationAuthorityAction.getResourceNameMap());
    }

    /**
     * 接收资源编号为 {@code resourceId} 的资源
     *
     * @param resourceId 资源编号
     */
    public void receiveResource(int resourceId, String absolutePath) {
        // 获取资源详细信息并创建资源的目录结构
        getResourceInformationAndCreatDir(resourceId, absolutePath);

        // 获取资源中的文件信息列表
        List<FileInformation> fileInformations =
                        this.resourceInformation.getFileInformationList();

        // 获取经由分发策略选择后得到的资源拥有者列表
        List<OwnerInformation> owners = this.distributionStrategy
                                            .selectProperResourceOwner(
                                                getResourceOwnerList(resourceId));

        // 根据文件信息列表和资源拥有者数量分配发送每个资源拥有者的发送任务
        List<List<SectionHeader>> assignmentList = new FileSplitter()
                                        .splitResourceFiles(fileInformations, owners.size());
        receive(resourceId, owners, assignmentList);
    }

    /**
     * 具体的接收资源逻辑
     *
     * @param resourceId 资源编号
     * @param owners 资源拥有者信息列表
     * @param assignmentList 发送任务
     */
    private void receive(int resourceId, List<OwnerInformation> owners, List<List<SectionHeader>> assignmentList) {
        @SuppressWarnings("AlibabaThreadShouldSetName")
        ExecutorService threadPool = new ThreadPoolExecutor(5,
                                                        8,
                                                        3,
                                                        TimeUnit.MILLISECONDS,
                                                        new LinkedBlockingDeque<>(3));

        // 初始化接收端服务器
        ReceivingServer receivingServer = new ReceivingServer(this.receivingServerPort,
                owners.size(), this.resourceInformation,
                new AfterTransferFailedImpl());
        try {
            receivingServer.startUp();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < owners.size(); i++) {
            initResourceOwnerAction(owners.get(i).getPort(), owners.get(i).getIp());

            List<SectionHeader> list = assignmentList.get(i);
            threadPool.execute(() ->
                    resourceOwnerAction.send(resourceId, requesterInformation, list));
        }
        this.resourceOwner.registryResourceOwner(resourceId);
        try {
            if (this.isFirstRegister) {
                this.resourceOwner.startUp();
            }
            this.isFirstRegister = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool.shutdown();
    }

    /**
     * 从注册中心获取 {@code resourceId} 对应的资源详细信息，并根据
     * 资源详细信息创建目录结构。
     *
     * @param resourceId 资源编号
     * @param absolutePath 资源将被存储的绝对路径
     */
    private void getResourceInformationAndCreatDir(int resourceId, String absolutePath) {
        this.resourceInformation = this.registrationAuthorityAction
                                        .getResourceInformation(resourceId);
        this.resourceInformation.creatDirctories(absolutePath);

        this.resourceOwner.addResourceInformation(resourceId, this.resourceInformation);
    }

    /**
     * 获取拥有编号为 {@code resourceId} 的资源的资源拥有者信息列表
     *
     * @param resourceId 资源编号
     * @return 拥有该资源的资源拥有者信息列表
     */
    private List<OwnerInformation> getResourceOwnerList(int resourceId) {
        return this.registrationAuthorityAction.getResourceOwnerList(resourceId);
    }

    /**
     * 根据用户配置的注册中心服务器的配置文件，初始化 {@code registrationAuthorityAction}
     *
     * @param configFilePath 注册中心服务器的配置文件路径
     */
    private void initRegistrationAuthorityAction(String configFilePath) {
        PropertiesParse.loadPropreties(configFilePath);

        String ip = PropertiesParse.getValue("rmi_server_ip");
        int port = Integer.parseInt(PropertiesParse.getValue("rmi_server_port"));

        this.registrationAuthorityAction = new RmiProxy(new RmiClient(port, ip))
                .getProxy(IRegistrationAuthorityAction.class);
    }

    /**
     * 通过资源拥有者的RMI服务器的端口号和ip地址初始化 {@code resourceOwnerAction}
     *
     * @param port 资源拥有者RMI服务器的端口号
     * @param ip 资源拥有者RMI服务器的ip地址
     */
    private void initResourceOwnerAction(int port, String ip) {
        this.resourceOwnerAction = new RmiProxy(
                            new RmiClient(port, ip)).getProxy(IResourceOwnerAction.class);
    }

    class AfterTransferFailedImpl implements IAfterTransferFailed {
        public AfterTransferFailedImpl() {}

        @Override
        public void executeResumeFromBreakPoint(UnreceivedSectionPool unreceivedSectionPool) {
            List<SectionHeader> unreceivedFileBlocks = unreceivedSectionPool
                                                        .getUnreceivedFileBlocks();

            List<OwnerInformation> owners = distributionStrategy.selectProperResourceOwner(
                                    getResourceOwnerList(unreceivedSectionPool.getResourceId()));

            List<List<SectionHeader>> assignments = new FileSplitter()
                                .splitUnreceivedFiles(unreceivedFileBlocks, owners.size());

            receive(unreceivedSectionPool.getResourceId(), owners, assignments);
        }
    }
}
