package com.tyz.transmission.files;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * 保证每个线程对每个文件的读写操作时使用的都是同一个流，所以根据
 * 文件的id，为每个文件创建单例的 {@link RandomAccessFile},
 * 将其保存在散列表中，以文件的编号为键。
 *
 * @author tyz
 */
public class RandomAccessFilePool {
    private ResourceInformation resourceInformation;
    private volatile Map<Integer, RandomAccessFile> randomAccessFilePool;

    public RandomAccessFilePool(ResourceInformation resourceInformation) {
        this.resourceInformation = resourceInformation;
        this.randomAccessFilePool = new HashMap<>();
    }

    /**
     * 根据文件编号 {@code fileId} 取出对应的流，如果没有
     * 就创建一个新的。
     *
     * @param fileId 文件编号
     * @param mode 文件的操作方式
     * @return 该文件对应的流
     * @throws FileNotFoundException 创建流失败
     */
    public RandomAccessFile getRandomAccessFile(int fileId, String mode) throws FileNotFoundException {
        RandomAccessFile raf = this.randomAccessFilePool.get(fileId);

        if (raf == null) {
            synchronized (this) {
                raf = this.randomAccessFilePool.get(fileId);
                if (raf == null) {
                    raf = new RandomAccessFile(this.resourceInformation
                                                    .getFileAbsolutePath(fileId), mode);

                    this.randomAccessFilePool.put(fileId, raf);
                }
            }
        }
        return raf;
    }

    /**
     * 关闭文件对应的流
     *
     * @param fileId 文件编号
     * @throws IOException 关闭流失败
     */
    public void closeRandomAccessFile(int fileId) throws IOException {
        RandomAccessFile raf = this.randomAccessFilePool.get(fileId);

        if (raf != null) {
            raf.close();
            this.randomAccessFilePool.remove(fileId);
        }
    }

    @Override
    public String toString() {
        return this.randomAccessFilePool.toString();
    }
}
