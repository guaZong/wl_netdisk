package com.netdisk.common.utils.upload;

import cn.hutool.core.util.RandomUtil;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.netdisk.common.enums.FilePathEnum;
import com.netdisk.common.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * OSS工具类
 *
 * @author lsj
 */
@Component
public class OSSUtil {

    private static String url;

    private static String endpoint;

    private static String accessKeyId;

    private static String accessKeySecret;

    private static String bucketName;

    @Value("${aliyun.url}")
    public void setUrl(String url) {
        OSSUtil.url = url;
    }

    @Value("${aliyun.endpoint}")
    public void setEndpoint(String endpoint) {
        OSSUtil.endpoint = endpoint;
    }

    @Value("${aliyun.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        OSSUtil.accessKeyId = accessKeyId;
    }

    @Value("${aliyun.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        OSSUtil.accessKeySecret = accessKeySecret;
    }

    @Value("${aliyun.bucketName}")
    public void setBucketName(String bucketName) {
        OSSUtil.bucketName = bucketName;
    }

    public OSS getOssClient(){
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public String getBucketName(){
        return bucketName;
    }

    public String getUrl(){
        return url;
    }

    /**
     * 上传图片
     *
     * @param file       文件
     * @param targetAddr 目标路径
     * @return
     */
    public String upload(MultipartFile file, String targetAddr) {
        // 获取不重复的随机名
        String fileName = RandomUtil.randomString(10);
        // 获取文件的扩展名如png,jpg等
        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        // 获取文件存储的相对路径(带文件名)
        String relativeAddr = targetAddr + fileName + extension;
        try {
            // 创建OSSClient实例
            OSS ossClient = getOssClient();
            // 上传文件
            ossClient.putObject(getBucketName(), relativeAddr, file.getInputStream());
//            // 关闭OSSClient。
//            ossClient.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getUrl() + relativeAddr;
    }

    /**
     * 获取输入文件流的扩展名
     *
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }



    public void easyMUpload(MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            easyUpload(file);
        }
    }


    public String easyUpload(MultipartFile file) throws Exception {
        // 获取不重复的随机名
        String fileName = RandomUtil.randomString(10);
        // 获取文件的扩展名如png,jpg等
        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        // 获取文件存储的相对路径(带文件名)
        String relativeAddr = FilePathEnum.DATA.getPath() + fileName + extension;
        try {
            // 创建OSSClient实例
            OSS ossClient = getOssClient();
            // 上传文件
            ossClient.putObject(getBucketName(), relativeAddr, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getUrl() + relativeAddr;
    }


    public void removeData(String objectName) {
        // 创建OSSClient实例
        OSS ossClient = getOssClient();
        // 删除文件
        try {
            ossClient.deleteObject(getBucketName(), objectName);
        } catch (OSSException e) {
            e.printStackTrace();
        }
    }




}
