package com.tyz.transmission.receiver;

import com.tyz.transmission.protocol.UnreceivedSectionPool;

/**
 * 处理文件块传输失败时的异常情况，此时应完成断点续传
 *
 * @author tyz
 */
public interface IAfterTransferFailed {
    /**
     * 根据未接收到的文件块进行断点续传
     *
     * @param unreceivedSectionPool 维护未接受到的文件快的池子
     */
    void executeResumeFromBreakPoint(UnreceivedSectionPool unreceivedSectionPool);
}
