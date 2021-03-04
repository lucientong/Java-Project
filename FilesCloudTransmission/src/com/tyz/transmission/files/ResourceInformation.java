package com.tyz.transmission.files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述一个资源的信息，包含该资源的编号、资源所在的根目录、资源包含的文件列表
 * 以及文件的目录列表。此类会被资源最初拥有者和资源请求者共同使用。
 *
 * 资源最初拥有者根据根目录找到对应的资源，扫描所有的文件，将该资源所拥有的所有
 * 文件信息注入到 {@code fileList} 中，将目录结构保存在 {@code directoryList}
 * 中。构筑成该资源的详细信息。
 *
 * 资源请求者根据注册中心发送的该类对象，创建对应的文件目录，并根据文件的详细信息确定
 * 接收到的文件块是否齐全。
 *
 * @author tyz
 */
public class ResourceInformation {
    /** 资源编号 */
    private int resourceId;

    /** 资源所在绝对路径 */
    private String absolutePath;

    /** 资源包含的文件表 */
    private Map<Integer, FileInformation> fileInformationMap;

    /** 资源中的目录 */
    private List<String> directoryList;

    /** 在遍历的过程中生成的文件编号 */
    private int fileId;

    /**
     * 资源最初拥有者使用，初始化一个空的文件列表和空的目录列表。将通过
     * 扫描资源将文件信息注入。
     *
     * @param resourceId 资源编号
     * @param absolutePath 资源所在绝对路径
     */
    public ResourceInformation(int resourceId, String absolutePath) {
        this.resourceId = resourceId;
        this.absolutePath = absolutePath;
        this.fileInformationMap = new HashMap<>();
        this.directoryList = new ArrayList<>();
    }

    /**
     * 根据文件的编号获取文件的绝对路径
     *
     * @param fileId 文件编号
     * @return 文件的绝对路径
     */
    public String getFileAbsolutePath(int fileId) {
        FileInformation file = this.fileInformationMap.get(fileId);

        return file == null ? null : (this.absolutePath + file.getFilePath());
    }

    /**
     * 和资源最初所记录的 {@code fileList} 相比，找到 {@code resourceName}
     * 文件中缺失或不完整的所有文件信息。
     *
     * @param resourceName 资源名称
     * @return 接收不完整的文件信息
     * @throws Exception 未找到对应资源文件
     */
    public List<FileInformation> findLostFileInResource(String resourceName) throws Exception {
        List<FileInformation> incompleteFiles = getFileInformationList();

        File root = creatRootFile(resourceName);

        scanResourceFiles(root, (currfile -> {
            for (FileInformation file : fileInformationMap.values()) {
                if (file.getFilePath().equals(currfile.getAbsolutePath()
                                        .substring(absolutePath.length()))
                            && file.getFileSize() == currfile.length()) {
                    incompleteFiles.remove(file);
                }
            }
        }));
        return incompleteFiles;
    }

    /**
     * 根据 {@code directoryList} 中的文件相对路径创建文件夹
     *
     * @param absolutePath 要创建文件夹的绝对路径
     */
    public void creatDirctories(String absolutePath) {
        File root = new File(absolutePath);
        this.absolutePath = absolutePath;

        if (!root.exists()) {
            root.mkdirs();
        }

        for (String dirPath : this.directoryList) {
            File file = new File(absolutePath + dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 扫描资源中的所有文件，记录每个文件和文件夹的信息，存放到 {@code fileList}
     * 和 {@code directoryList} 中，以便资源接收方根据这两个文件进行资源接收。
     *
     * @param resourceFileName 资源名称
     * @throws Exception 未能找到资源
     */
    public void scanResourceFiles(String resourceFileName) throws Exception {
        File resourceFile = creatRootFile(resourceFileName);
        this.fileId = 0;

        if (resourceFile.isDirectory()) {
            this.directoryList.add(resourceFile.getAbsolutePath()
                                .replace(this.absolutePath, ""));
        }
        scanResourceFiles(resourceFile, currfile -> {
            String fileRelativePath = currfile.getAbsolutePath()
                                                .substring(absolutePath.length());
            fileInformationMap.put(fileId, new FileInformation(fileId++,
                                            fileRelativePath, currfile.length()));
        });
    }

    /**
     * 扫描资源中的文件，被实现了文件处理策略 {@code processFiles} 的重载
     * 方法所调用，做实际的扫描工作。
     *
     * @param file 需要递归遍历的文件
     * @param processFiles 文件处理策略
     *                     
     * @see #scanResourceFiles(String)
     */
    private void scanResourceFiles(File file, IProcessFiles processFiles) {
        if (file.isDirectory()) {
            this.directoryList.add(file.getAbsolutePath()
                    .substring(this.absolutePath.length()));
            scanDirectory(file, processFiles);
        } else {
            processFiles.processFile(file);
        }
    }

    /**
     * 递归扫描文件夹中的文件，如果是文件夹就将该文件夹的相对
     * 路径添加到 {@code directoryList} 中，然后继续递归。
     * 如果是文件就将其加入到 {@code fileList} 中，并调用
     * {@code} processFiles 接口中的方法进行处理。
     *
     * 文件的相对路径会根据所在的资源 {@code root} 的绝对路径
     * 得到。
     *
     * @param direcroty 遍历到的文件夹
     * @param processFiles 文件处理策略
     */
    private void scanDirectory(File direcroty, IProcessFiles processFiles) {
        File[] files = direcroty.listFiles();

        for (File file : files != null ? files : new File[0]) {
            if (file.isDirectory()) {
                this.directoryList.add(file.getAbsolutePath()
                                        .substring(this.absolutePath.length()));
                scanDirectory(file, processFiles);
            } else {
                processFiles.processFile(file);
            }
        }
    }

    /**
     * 根据资源所在的绝对路径 {@code absolutePath} 生成资源的文件File
     *
     * @param resourceName 资源名
     * @return 生成的代表资源的文件
     * @throws Exception 生成文件失败
     */
    private File creatRootFile(String resourceName) throws Exception {
        File resourceRoot = new File(this.absolutePath + resourceName);
        if (!resourceRoot.exists()) {
            throw new Exception("Can't find file [" + resourceName + "] in ["
                                        + this.absolutePath + "].");
        }

        return resourceRoot;
    }

    /**
     * 获取资源中的文件信息列表
     *
     * @return 文件信息列表
     */
    public List<FileInformation> getFileInformationList() {
        return new ArrayList<>(this.fileInformationMap.values());
    }

    /**
     * 获取文件信息池
     *
     * @return 文件信息池
     */
    public Map<Integer, FileInformation> getFileInformationMap() {
        return new HashMap<>(this.fileInformationMap);
    }

    /**
     * @return 资源编号
     */
    public int getResourceId() {
        return this.resourceId;
    }
}
