package com.sk.netdisk.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 平常工具类
 *
 * @author lsj
 */
public class CommonUtils {
    /**
     * 返回格式化之后的日期
     * yyyy年MM月dd日,HH:mm
     *
     * @param date date对象
     * @return String
     */
    public static String getStringDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日,HH:mm");
        return sdf.format(date);
    }

    /**
     * 获取oss存储的ObjectName
     *
     * @param url 完整链接
     * @return String
     */
    public static String getObjectName(String url) {
        URI uri = URI.create(url);
        return uri.getPath().split("/", 2)[1];
    }

    /**
     * 获取一个新的链接
     *
     * @param link 旧链接
     * @return String
     */
    public static String getNewLink(String link) {
        int i = link.lastIndexOf("/");
        String oldLink = link.substring(0, i);
        String str = link.substring(i + 1);
        String name = String.valueOf(IdWorker.getId());
        String last = str.split("\\.")[1];
        return oldLink + "/" + name + "." + last;
    }

    /**
     * 获取文件的MD5值
     *
     * @param file File对象
     * @return String MD5值
     */
    public static String getFileMd5(File file) throws IOException, NoSuchAlgorithmException {
        // 检查文件是否存在
        if (!file.exists() || !file.isFile()) {
            throw new IOException("文件不存在或不是一个文件");
        }

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) != -1) {
            md5.update(buffer, 0, length);
        }
        fis.close();

        byte[] digest = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * 获取文件的MD5值
     *
     * @param file MultipartFile对象
     * @return String MD5值
     */
    public static String getFileMd5(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件不存在或为空");
        }
        // 获取文件流
        byte[] fileBytes = file.getBytes();

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(fileBytes);

        byte[] digest = md5.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
