package com.tyz.transmission.files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 文件访问器，完成文件的读写工作
 *
 * @author tyz
 */
public class FileAccessor {

    public FileAccessor() {}

    /**
     * 从 {@code offset} 处开始读取长度为 {@code length} 的文件
     *
     * @param raf 文件访问流
     * @param offset 偏移量
     * @param length 需要读取的流的长度
     * @return 读到的字节流
     * @throws IOException 读取数据失败
     */
    public static byte[] readFileSection(RandomAccessFile raf, long offset, long length) throws IOException {
        byte[] buffer = new byte[(int) length];

        raf.seek(offset);
        raf.read(buffer);

        return buffer;
    }

    /**
     * 从 {@code offset} 处开始写 {@code bytes} 字节流，这里
     * 使用的是同一个 {@code raf}，这样比每次写文件打开一个新的
     * RandomAccessFile 写完再关闭要效率更高。
     *
     * @param raf 文件访问流
     * @param offset 偏移量
     * @throws IOException 写数据失败
     */
    public static void writeFileSection(RandomAccessFile raf, long offset, byte[] bytes) throws IOException {
        synchronized (raf) {
            raf.seek(offset);
            raf.write(bytes);
        }
    }
}
