package com.sk.netdisk.util;

import com.sk.netdisk.util.upload.UploadUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    /**
     * 判断文件是否存在
     *
     * @param fileMd5
     * @return
     */
    public static boolean judgeExistFile(String fileName, String fileMd5) {
        String realPath = UploadUtil.FILE_PATH + UploadUtil.getStringFileType(fileName)
                + File.separator + fileMd5 + File.separator + fileName;
        File fileFolder = new File(realPath);
        return fileFolder.exists();
    }


    /**
     * 根据字节值来计算文件大小
     *
     * @param sizeInBytes 文件字节大小
     * @return String
     */
    public static String getFileSize(long sizeInBytes) {
        double sizeInKb = sizeInBytes / 1024.0;
        if (sizeInKb < 1) {
            return sizeInBytes + " B";
        }
        double sizeInMb = sizeInKb / 1024.0;
        if (sizeInMb < 1) {
            return String.format("%.2f KB", sizeInKb);
        }
        double sizeInGb = sizeInMb / 1024.0;
        if (sizeInGb < 1) {
            return String.format("%.2f MB", sizeInMb);
        }
        return String.format("%.2f GB", sizeInGb);
    }

    /**
     * 查询是否重名并重命名为  file(x) x为数字
     *
     * @param fileName      文件名字
     * @param existingNames 要查询的文件名字集合
     * @return String
     */
    public static String renameFile(String fileName, List<String> existingNames) {
        Set<String> existingSet = new HashSet<>(existingNames);
        String newFileName = fileName;
        int counter = 1;
        while (existingSet.contains(newFileName)) {
            int dotIndex = fileName.lastIndexOf(".");
            String baseName = (dotIndex != -1) ? fileName.substring(0, dotIndex) : fileName;
            String extension = (dotIndex != -1) ? fileName.substring(dotIndex) : "";
            String candidateName = baseName + "(" + counter + ")" + extension;
            if (existingSet.contains(candidateName)) {
                counter++;
            } else {
                newFileName = candidateName;
            }
        }
        return newFileName;
    }
}
