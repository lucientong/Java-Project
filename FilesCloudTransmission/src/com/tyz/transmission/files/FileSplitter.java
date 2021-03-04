package com.tyz.transmission.files;

import com.tyz.transmission.protocol.SectionHeader;
import com.tyz.transmission.protocol.UnreceivedSectionPool;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件分片的工具，接收方将调用此类，把需要接收的资源或者找到的未接收完全
 * 的文件，根据资源发送方的数量，分成文件块并给每个发送端分配好。
 *
 * 在使用分割器的时候，可以通过单参构造更改文件分块大小，但是文件块不能超过
 * 64K，否则在后面的发送中可能会出错，建议32K比较合适。
 *
 * @author tyz
 */
public class FileSplitter {
    /** 最大的文件块大小为64K */
    private static final int MAX_SECTION_SIZE = 1 << 16;

    /** 默认的文件块大小为32K */
    private static final int SECTION_SIZE = 1 << 15;

    /** 文件块大小 */
    private int sectionSize;

    /**
     * 默认分割器分片的文件块大小为 {@code SECTION_SIZE},aka 32K
     */
    public FileSplitter() {
        this.sectionSize = SECTION_SIZE;
    }

    /**
     * 用户设置文件分块的大小，最大为 {@code MAX_SECTION_SIZE}
     *
     * @param sectionSize 用户设置的分割成的文件块大小
     */
    public FileSplitter(int sectionSize) {
        this.sectionSize = Math.min(sectionSize, MAX_SECTION_SIZE);
    }

    /**
     * 根据发送端数量 {@code count} 分割文件 {@code fileList}，这个
     * 文件列表是根据注册中心发来的详细资源信息得到的。
     *
     * @param fileList 需要分割的文件列表
     * @param count 发送端数量
     * @return 给每个发送端分配的应发送的文件块信息
     */
    public List<List<SectionHeader>> splitResourceFiles(List<FileInformation> fileList, int count) {
        List<List<SectionHeader>> assignmentList = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            assignmentList.add(new ArrayList<>());
        }

        int index = 0;

        for (FileInformation file : fileList) {
            long fileSize = file.getFileSize();
            int fileId = file.getFileId();

            if (fileSize <= this.sectionSize) {
                assignmentList.get(index).add(new SectionHeader(
                                    fileId, 0, (int) fileSize));
                index = (index + 1) % count;
            } else {
                long restSize = fileSize;
                long offset = 0;
                int len;

                while (restSize > 0) {
                    len = restSize > this.sectionSize ? this.sectionSize : (int) restSize;
                    assignmentList.get(index).add(new SectionHeader(fileId, offset, len));

                    index = (index + 1) % count;
                    restSize -= len;
                    offset += len;
                }
            }
        }
        return assignmentList;
    }

    /**
     * 根据发送端的数量 {@code count} 将文件块 {@code unreceivedSectionList}
     * 分割，分配给发送端。这个文件块列表是接收方统计出的未接收到的文件块信息，是通过
     * {@link UnreceivedSectionPool} 得到的。如果连续好几个文件块没有收到，那统
     * 计到的未接收到文件块的大小就可能很大，所以需要进行分割。
     *
     * @param unreceivedSectionList 未接收到的文件块信息列表
     * @param count 发送端数量
     * @return 给每个发送端分配的应发送的文件块信息
     */
    public List<List<SectionHeader>> splitUnreceivedFiles(List<SectionHeader> unreceivedSectionList, int count) {
        List<List<SectionHeader>> assignmentList = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            assignmentList.add(new ArrayList<>());
        }

        int index = 0;

        for (SectionHeader unreceivedSection : unreceivedSectionList) {
            long sectionSize = unreceivedSection.getLength();
            int fileId = unreceivedSection.getFileId();

            if (sectionSize <= this.sectionSize) {
                assignmentList.get(index).add(new SectionHeader(fileId,
                                                        unreceivedSection.getOffset(),
                                                        unreceivedSection.getLength()));
                index = (index + 1) % count;
            } else {
                long restSize = sectionSize;
                long offset = 0;
                int len;

                while (restSize > 0) {
                    len = restSize > this.sectionSize ? this.sectionSize : (int) restSize;
                    assignmentList.get(index).add(new SectionHeader(fileId, offset, len));

                    restSize -= len;
                    offset += len;
                    index = (index + 1) % count;
                }
            }
        }
        return assignmentList;
    }

}
