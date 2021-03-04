package com.tyz.transmission.protocol;

import com.tyz.transmission.files.FileInformation;
import com.tyz.transmission.files.ResourceInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理 {@link UnreceivedSections} 的容器，以文件编号为键
 *
 * @author tyz
 */
public class UnreceivedSectionPool {
    /** 资源编号 */
    private int resourceId;

    /** 未接收到的文件块 */
    private Map<Integer, UnreceivedSections> unreceivedSectionPool;

    public UnreceivedSectionPool() {
        this.unreceivedSectionPool = new ConcurrentHashMap<>();
    }

    /**
     * 根据 {@code resourceInformation} 中的文件列表生成 UnreceivedSectionPool
     *
     * @param resourceInformation 资源信息
     */
    public UnreceivedSectionPool(ResourceInformation resourceInformation) {
        this();
        this.resourceId = resourceInformation.getResourceId();
        Map<Integer, FileInformation> map = resourceInformation.getFileInformationMap();

        for (Integer fileId : map.keySet()) {
            addTargetFile(fileId, map.get(fileId).getFileSize());
        }
    }

    /**
     * 向 {@code unreceivedSectionPool} 池中添加一个要维护的未接收文件块信息表
     *
     * @param fileId 要接收的文件编号
     * @param fileLength 要接收的文件长度
     */
    public void addTargetFile(int fileId, long fileLength) {
        this.unreceivedSectionPool.put(fileId, new UnreceivedSections(fileId, fileLength));
    }

    /**
     * 更新 {@code receivedSection} 对应文件的未接收文件块列表
     *
     * @param receivedSection 接收到的文件块
     * @return {@code receivedSection} 对应文件的片段是否已接收完
     */
    public boolean receiveSection(SectionHeader receivedSection) {
        return this.unreceivedSectionPool.get(receivedSection.getFileId())
                                                .receiveSection(receivedSection);
    }

    /**
     * 获取还没有接收到完整信息块的所有文件信息
     *
     * @return 还没有接收到完整信息块的所有文件信息
     */
    public List<SectionHeader> getUnreceivedFileBlocks() {
        List<SectionHeader> res = new ArrayList<>();

        for (Integer fileId : this.unreceivedSectionPool.keySet()) {
            UnreceivedSections unreceivedSections = this.unreceivedSectionPool.get(fileId);
            if (unreceivedSections.isEmpty()) {
                continue;
            }

            while (!unreceivedSections.isEmpty()) {
                res.add(unreceivedSections.poll());
            }
        }
        return res;
    }

    /**
     * @return 此类维护的资源编号
     */
    public int getResourceId() {
        return this.resourceId;
    }
}
