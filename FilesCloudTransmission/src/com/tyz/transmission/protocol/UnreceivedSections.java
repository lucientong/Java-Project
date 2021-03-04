package com.tyz.transmission.protocol;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 对每个文件维护一个UnreceivedSections，在接收的时候，记录还未
 * 收到的文件块信息。
 *
 * @author tyz
 */
public class UnreceivedSections {
    /** 没有找到需要插入的片段 */
    private static final int NOT_FOUND = -1;

    /** 文件编号 */
    private int fileId;

    /** 还未收到的文件块列表 */
    private List<SectionHeader> unreceivedSectonList;

    /**
     * 初始化 未收到文件块信息表，初始时默认没有收到过任何
     * 文件块，所以初始时从 0 到 {@code fileLength} 整
     * 个要接收的文件当作还未收到的一个文件块。
     *
     * @param fileId 文件编号
     * @param fileLength 文件长度
     */
    UnreceivedSections(int fileId, long fileLength) {
        this.fileId = fileId;
        this.unreceivedSectonList = new ArrayList<>();
        this.unreceivedSectonList = new LinkedList<>();

        this.unreceivedSectonList.add(new SectionHeader(fileId, 0, fileLength));
    }

    /**
     * 在 {@code unreceivedSectonList} 中清除对应 {@code receivedSection} 的片段。
     * 当此方法返回为true时，表明接收文件块已经完成，接收方可以关闭文件流。
     *
     * @param receivedSection 接收到的文件块
     * @return 当 {@code unreceivedSectonList} 为空时返回true，此时说明文件块已经全
     *         部接收完毕。
     */
    boolean receiveSection(SectionHeader receivedSection) {
        int index = search(receivedSection.getOffset(), receivedSection.getLength());

        if (index == NOT_FOUND) {
            return true;
        }

        // 得到文件块需要插入的片段
        SectionHeader unreceivedSection = this.unreceivedSectonList.get(index);

        this.unreceivedSectonList.remove(index);

        long rightOffset = receivedSection.getOffset() + receivedSection.getLength();
        int rightLength = (int) (unreceivedSection.getOffset() + unreceivedSection.getLength() - rightOffset);
        if (rightLength > 0) {
            SectionHeader rightSection = new SectionHeader(this.fileId, rightOffset, rightLength);
            this.unreceivedSectonList.add(index, rightSection);
        }

        long leftOffset = unreceivedSection.getOffset();
        int leftLength = (int) (receivedSection.getOffset() - unreceivedSection.getOffset());
        if (leftLength > 0) {
            SectionHeader leftSection = new SectionHeader(this.fileId, leftOffset, leftLength);
            this.unreceivedSectonList.add(index, leftSection);
        }

        return isEmpty();
    }

    /**
     * @return {@code unreceivedSectonList} 中第一个还未接收到的文件块
     */
    SectionHeader poll() {
        if (this.unreceivedSectonList.isEmpty()) {
            return null;
        }
        return this.unreceivedSectonList.remove(0);
    }

    /**
     * @return 返回 {@code unreceivedSectonList} 是否已经为空
     */
    boolean isEmpty() {
        return this.unreceivedSectonList.isEmpty();
    }

    /**
     * 根据新接收到的文件块的信息 偏移量{@code receivedOffset} 和文件块长度 {@code receivedLength}
     * 更新 {@code unreceivedSectonList} 中未接收到的文件块信息
     *
     * @param receivedOffset 接收到的文件块的偏移量
     * @param receivedLength 接收到的文件块的长度
     * @return 需要插入的 未接收到的文件块 的下标
     */
    private int search(long receivedOffset, long receivedLength) {
        for (int i = 0; i < this.unreceivedSectonList.size(); i++) {
            SectionHeader section = this.unreceivedSectonList.get(i);
            if (section.getOffset() + section.getLength() < receivedOffset
                    || section.getOffset() > receivedOffset + receivedLength) {
                continue;
            }
            return i;
        }
        return NOT_FOUND;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnreceivedSections that = (UnreceivedSections) o;
        return fileId == that.fileId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }
}
