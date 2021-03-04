package com.tyz.transmission.protocol;

import com.tyz.util.BytesTranslator;

/**
 * 每次传输文件块时的信息头，接收方需要解析这个信息头，
 * 获取需要接收的文件的编号、文件块的偏移量和长度。
 *
 * @author tyz
 */
public class SectionHeader {
    /** 信息头的总字节数 */
    public static final int SECTION_HEADER_LENGTH = 20;

    /** 信息头中文件编号的总字节数 */
    public static final int HEADER_ID_LENGTH = 4;

    /** 信息头中偏移量的总字节数 */
    public static final int HEADER_OFFSET_LENGTH = 8;

    /** 文件块所属的文件编号 */
    private int fileId;

    /** 文件块的起始偏移量 */
    private long offset;

    /** 文件块的长度 */
    private long length;

    /**
     * 将二进制字节流 {@code bytes} 转换成文件编号、文件块偏移量和文件块长度
     *
     * @param bytes 二进制数组
     */
    public SectionHeader(byte[] bytes) {
        this.fileId = BytesTranslator.toInt(bytes, 0);
        this.offset = BytesTranslator.toLong(bytes, HEADER_ID_LENGTH);
        this.length = BytesTranslator.toLong(bytes, HEADER_ID_LENGTH + HEADER_OFFSET_LENGTH);
    }

    /**
     * 初始化信息头
     *
     * @param fileId 文件块所属文件编号
     * @param offset 文件块初始偏移量
     * @param length 文件块长度
     */
    public SectionHeader(int fileId, long offset, long length) {
        this.fileId = fileId;
        this.offset = offset;
        this.length = length;
    }

    /**
     * 将文件编号、文件块偏移量和文件块长度按顺序转换成字节流
     * 4B + 8B + 8B = 20B
     *
     * @return 文件编号、文件块偏移量和文件块长度转换成的字节流
     */
    public byte[] toBytes() {
        byte[] bytes = new byte[SECTION_HEADER_LENGTH];

        BytesTranslator.toBytes(bytes, 0, this.fileId);
        BytesTranslator.toBytes(bytes, HEADER_ID_LENGTH, this.offset);
        BytesTranslator.toBytes(bytes, HEADER_ID_LENGTH + HEADER_OFFSET_LENGTH, this.length);

        return bytes;
    }

    /**
     * @return 文件块所属的文件编号
     */
    public int getFileId() {
        return fileId;
    }

    /**
     * @return 文件块的起始偏移量
     */
    public long getOffset() {
        return offset;
    }

    /**
     * @return 文件块的长度
     */
    public long getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "[ " + this.fileId + "--" + this.offset + "--" + this.length + " ]";
    }
}
