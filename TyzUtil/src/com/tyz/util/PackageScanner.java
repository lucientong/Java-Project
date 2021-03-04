package com.tyz.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 包扫描
 *
 * @author tyz
 */
public abstract class PackageScanner {
    public PackageScanner() {
    }

    /**
     * 处理扫描到的类
     * @param klass 包扫描器扫描到的类
     */
    public abstract void dealClass(Class<?> klass);

    /**
     * 扫描jar包
     * @param url jar包的路径
     */
    private void scanJar(URL url) {
        try {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            JarFile jarFile = connection.getJarFile();
            Enumeration<JarEntry> entryList = jarFile.entries();

            while (entryList.hasMoreElements()) {
                JarEntry jarEntry = entryList.nextElement();
                if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                    continue;
                }
                String className = jarEntry.getName();
                className = className.replace(".class", "");
                className = className.replace("/", ".");

                Class<?> klass = Class.forName(className);
                dealClass(klass);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描文件夹
     * @param curFile 当前扫描到的文件夹名
     * @param packageName 包名
     */
    private void scanDirectory(File curFile, String packageName) {
        File[] files = curFile.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.endsWith(".class")) {
                    fileName = fileName.replace(".class", "");
                    String className = packageName + "." + fileName;

                    try {
                        Class<?> klass = Class.forName(className);
                        dealClass(klass);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 暴露给public包扫描方法
     * @param packageName 需要扫描的包名
     */
    public void packageScanner(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String pathName = packageName.replace(".", "/");

        try {
            Enumeration<URL> urls = classLoader.getResources(pathName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if ("jar".equals(url.getProtocol())) {
                    scanJar(url);
                } else {
                    File curFile = new File(url.toURI());
                    scanDirectory(curFile, packageName);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
