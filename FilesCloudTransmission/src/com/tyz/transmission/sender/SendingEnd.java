package com.tyz.transmission.sender;

import com.tyz.transmission.files.FileAccessor;
import com.tyz.transmission.files.RandomAccessFilePool;
import com.tyz.transmission.files.ResourceInformation;
import com.tyz.transmission.protocol.SectionHeader;
import com.tyz.transmission.transfer.FileBlockTransmitter;

import java.io.*;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 完成发送端具体的文件块发送工作
 *
 * @author tyz
 */
public class SendingEnd implements Runnable {

    /** 连接接收端服务器的套接字 */
    private Socket socket;

    /** 输出流 */
    private OutputStream out;

    /** 发送任务列表 */
    private List<SectionHeader> assignmentList;

    /** 对文件块进行读写的工具类 */
    private FileBlockTransmitter fileBlockTransmitter;

    /** 管理随机文件访问流的池子 */
    private RandomAccessFilePool randomAccessFilePool;

    /** 线程池 */
    private ExecutorService threadPool;

    SendingEnd(Socket socket, List<SectionHeader> assignmentList,
                      ResourceInformation resourceInformation) throws IOException {
        this.socket = socket;
        this.out = new DataOutputStream(socket.getOutputStream());
        this.assignmentList = assignmentList;
        this.randomAccessFilePool = new RandomAccessFilePool(resourceInformation);
        this.fileBlockTransmitter = new FileBlockTransmitter();
        this.threadPool = new ThreadPoolExecutor(1, 1, 0,
                                                TimeUnit.MILLISECONDS,
                                                new LinkedBlockingDeque<>(1),
                                                r -> new Thread(r, "sending end"));
    }

    /**
     * 将发送任务按文件id排序后，开始发送文件块
     */
    void startToSend() {
        this.assignmentList.sort(Comparator.comparingInt(SectionHeader::getFileId));
        this.threadPool.execute(this);
    }

    @Override
    public void run() {
        int preFileId = -1;

        for (SectionHeader section : this.assignmentList) {
            int fileId = section.getFileId();
            long offset = section.getOffset();
            long length = section.getLength();

            try {
                if (preFileId != -1 && preFileId != fileId) {
                    this.randomAccessFilePool.closeRandomAccessFile(preFileId);
                }
                RandomAccessFile randomAccessFile = this.randomAccessFilePool.getRandomAccessFile(fileId, "r");
                byte[] bytes = FileAccessor.readFileSection(randomAccessFile, offset, length);

                this.fileBlockTransmitter.send(this.out, section, bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
              preFileId = fileId;
            }
        }
        try {
            this.fileBlockTransmitter.missionIsAccomplished(this.out);
            this.randomAccessFilePool.closeRandomAccessFile(preFileId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * 关闭输出流，套接字以及线程池
     */
    private void close() {
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                this.socket.close();
            } catch (IOException ignored) {
            } finally {
                this.socket = null;
            }
        }
        if (this.out != null) {
            try {
                this.out.close();
            } catch (IOException ignored) {
            } finally {
                this.out = null;
            }
        }
        this.threadPool.shutdown();
    }
}
