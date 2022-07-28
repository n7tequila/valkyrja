/*
 * PROJECT valkyrja2
 * util/FilesUtils.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.apache.commons.io.IOUtils.EOF;

/**
 * 文件操作工具类
 *
 * @author Tequila
 * @create 2022/05/20 12:38
 **/
public class FilesUtils {

    /** 默认的buffer size(4096) */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * 读文件字节数组
     *
     * @param file    文件
     * @param handler 读取过程中处理操作
     * @return {@link byte[] } 文件byte数组
     * @throws IOException IO操作错误时抛出
     * @author Tequila
     * @date 2022/05/20 11:44
     */
    public static byte[] readFileToByteArray(final File file, FileReadingHandler handler) throws IOException {
        return readFileToByteArray(file, handler, false);
    }

    /**
     * 读文件字节数组，并且可以定义在读取过程
     *
     * @param file        文件
     * @param onlyHandler 只处理程序，不把数据塞进返回的buffer[]中以节约内存开销
     * @param handler     读取过程中处理操作
     * @return {@link byte[] } 文件byte数组
     * @throws IOException IO操作错误时抛出
     * @author Tequila
     * @date 2022/05/20 11:18
     */
    public static byte[] readFileToByteArray(final File file, FileReadingHandler handler, boolean onlyHandler) throws IOException {
        try (InputStream in = FileUtils.openInputStream(file);
             ByteArrayOutputStream output = new ByteArrayOutputStream() ) {
            final long fileLength = file.length();

            if (fileLength > 0) {
                if (!onlyHandler && fileLength > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + fileLength);
                }

                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n;  // 读取的数据长度
                while (EOF != (n = in.read(buffer))) {
                    if (!onlyHandler) output.write(buffer, 0, n);  // 如果仅处理handler，则不降buffer写入返回值
                    if (handler != null) handler.read(buffer, n);
                }

                return output.toByteArray();
            } else {
                return IOUtils.toByteArray(in);
            }
        }
    }

    /**
     * 计算文件SHA256
     *
     * @param file 文件
     * @return {@link String } 文件的SHA256值
     * @throws IOException IO操作错误时抛出
     * @author Tequila
     * @date 2022/05/20 11:24
     */
    public static String computeFileSHA256(File file) throws IOException  {
        return computeFileHash(file, DigestConst.ALGORITHM_SHA256);
    }

    /**
     * 计算文件MD5
     *
     * @param file 文件
     * @return {@link String } 文件MD5值
     * @throws IOException IO操作错误时抛出
     * @author Tequila
     * @date 2022/05/20 11:38
     */
    public static String computeFileMD5(File file) throws IOException {
        return computeFileHash(file, DigestConst.ALGORITHM_MD5);
    }

    /**
     * 计算文件哈希
     *
     * @param file      文件
     * @param algorithm hash算法
     * @return {@link String } 文件hash值
     * @throws IOException IO操作错误时抛出
     * @author Tequila
     * @date 2022/05/20 11:35
     */
    private static String computeFileHash(File file, String algorithm) throws IOException {
        MessageDigest hashSum = null;
        try {
            hashSum = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) { /* nothing to do */ }

        if (hashSum == null) {
            throw new IllegalArgumentException("Can not create SHA256 MessageDigest object.");
        }

        MessageDigest finalHashSum = hashSum;
        readFileToByteArray(file, (buffer, len) -> finalHashSum.update(buffer, 0, len), true);

        return Hex.encodeHexString(finalHashSum.digest());
    }


    /**
     * 文件读取过程中处理操作
     *
     * @author Tequila
     * @create 2022/05/20 11:14
     **/
    @FunctionalInterface
    public interface FileReadingHandler {
        /**
         * 处理实现
         *
         * @param buffer 当前读取的一个块
         * @param len 块大小
         * @author Tequila
         * @date 2022/05/20 11:14
         */
        void read(byte[] buffer, int len);
    }

    private FilesUtils() {
        throw new IllegalStateException("Utility class");
    }
}
