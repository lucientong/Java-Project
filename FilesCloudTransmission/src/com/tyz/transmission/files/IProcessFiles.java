package com.tyz.transmission.files;

import java.io.File;

/**
 * 此接口方法将在 {@link ResourceInformation} 中被调用，解决
 * 处理文件的不同方式。
 *
 * @author tyz
 */
public interface IProcessFiles {
    /**
     * 处理当前遍历到的文件
     *
     * @param currfile 当前遍历到的文件
     */
    void processFile(File currfile);
}
