package com.tyz.transmission.files;

import java.util.Objects;

/**
 * 描述一个文件的信息。
 *
 * @author tyz
 */
public class FileInformation {
    /** 文件编号 */
    private int fileId;

    /** 文件相对路径 */
    private String filePath;

    /** 文件大小 */
    private long fileSize;

    /**
     * 初始化一个文件描述类
     *
     * @param fileId 文件编号
     * @param filePath 文件相对路径
     * @param fileSize 文件大小
     */
    FileInformation(int fileId, String filePath, long fileSize) {
        this.fileId = fileId;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    /**
     * @return 返回文件的编号
     */
    int getFileId() {
        return fileId;
    }

    /**
     * @return 返回文件的相对路径
     */
    String getFilePath() {
        return filePath;
    }

    /**
     * @return 返回文件的大小
     */
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileInformation that = (FileInformation) o;
        return fileId == that.fileId && fileSize == that.fileSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, filePath, fileSize);
    }

    @Override
    public String toString() {
        return this.fileId + " : " + this.filePath + " : " + this.fileSize;
    }
}
