package com.tyz.transmission.transfer;

import com.tyz.transmission.protocol.SectionHeader;

import java.io.FileNotFoundException;

/**
 * 处理文件块
 *
 * @author tyz
 */
public interface IFileBlockProcessor {
    /**
     * 处理接收到的文件块
     *
     * @param sectionHeader 信息头
     * @param bytes 字节流信息体
     * @throws Exception 未找到文件块对应的流或者写入文件块失败
     */
    void dealFileBlock(SectionHeader sectionHeader, byte[] bytes) throws Exception;
}
