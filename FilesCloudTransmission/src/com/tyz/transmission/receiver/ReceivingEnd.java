package com.tyz.transmission.receiver;

import com.tyz.transmission.files.FileAccessor;
import com.tyz.transmission.files.RandomAccessFilePool;
import com.tyz.transmission.protocol.UnreceivedSectionPool;
import com.tyz.transmission.protocol.SectionHeader;
import com.tyz.transmission.transfer.FileBlockTransmitter;
import com.tyz.transmission.transfer.IFileBlockProcessor;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 完成接收端具体的文件块接收处理工作，当 {@link ReceivingServer} 的侦听器
 * 接收到一个发送端的连接后，建立此类完成对该发送端发送的文件块的处理。
 *
 * @author tyz
 */
public class ReceivingEnd implements Runnable {
    /** 连接到的发送端的套接字 */
    private Socket socket;

    /** 读数据的流 */
    private InputStream in;

    /** 对文件块进行读写的工具类 */
    private FileBlockTransmitter fileBlockTransmitter;

    /** 接收端的状态 */
    private volatile boolean goOn;

    /** 线程池 */
    private ExecutorService threadPool;

    /** 未接收到的文件块池 */
    private UnreceivedSectionPool unreceivedSectionPool;

    /** 处理接收文件时异常情况 */
    IAfterTransferFailed afterTransferFailed;

    ReceivingEnd(Socket socket, RandomAccessFilePool randomAccessFilePool,
                                            UnreceivedSectionPool unreceivedSectionPool,
                                            IAfterTransferFailed afterTransferFailed) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.unreceivedSectionPool = unreceivedSectionPool;
        this.afterTransferFailed = afterTransferFailed;
        this.fileBlockTransmitter = new FileBlockTransmitter(
                new FileBlockProcessorImpl(unreceivedSectionPool, randomAccessFilePool));
        this.threadPool = new ThreadPoolExecutor(1, 1, 0,
                                                TimeUnit.MILLISECONDS,
                                                new LinkedBlockingDeque<>(1),
                                                r -> new Thread(r, "receiving end"));
        this.goOn = true;
        this.threadPool.execute(this);
    }

    @Override
    public void run() {
        while (this.goOn) {
            try {
                this.fileBlockTransmitter.recive(this.in);
            } catch (Exception e) {
                this.goOn = false;
                this.afterTransferFailed.executeResumeFromBreakPoint(this.unreceivedSectionPool);
            }
        }
        close();
    }

    /**
     * 关闭输入流，套接字以及线程池
     */
    private void close() {
        this.goOn = false;

        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException ignored) {
            } finally {
                this.socket = null;
            }
        }
        if (this.in != null) {
            try {
                this.in.close();
            } catch (IOException ignored) {
            } finally {
                this.in = null;
            }
        }
        this.threadPool.shutdown();
    }

    /**
     * 实现 {@link IFileBlockProcessor} 接口，处理接收到的文件块
     */
    class FileBlockProcessorImpl implements IFileBlockProcessor {
        private UnreceivedSectionPool unreceivedSectionPool;
        private RandomAccessFilePool randomAccessFilePool;

        public FileBlockProcessorImpl(UnreceivedSectionPool unreceivedSectionPool, RandomAccessFilePool randomAccessFilePool) {
            this.unreceivedSectionPool = unreceivedSectionPool;
            this.randomAccessFilePool = randomAccessFilePool;
        }

        /**
         * 将接收到的文件块写入文件中，并更新未接收到的文件块列表
         *
         * @param sectionHeader 信息头
         * @param bytes 字节流信息体
         * @throws Exception 未找到需要写入的相应文件或者写入文件异常
         */
        @Override
        public void dealFileBlock(SectionHeader sectionHeader, byte[] bytes) throws Exception {
            int fileId = sectionHeader.getFileId();

            if (fileId == FileBlockTransmitter.END_FLAG) {
                ReceivingEnd.this.close();
                return;
            }
            RandomAccessFile randomAccessFile = this.randomAccessFilePool
                                                .getRandomAccessFile(fileId, "rw");
            FileAccessor.writeFileSection(randomAccessFile, sectionHeader.getOffset(), bytes);

            boolean isCurrentFileComplete = this.unreceivedSectionPool.receiveSection(sectionHeader);

            if (isCurrentFileComplete) {
                this.randomAccessFilePool.closeRandomAccessFile(fileId);
            }
        }
    }
}
