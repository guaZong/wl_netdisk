package com.sk.netdisk.util.upload;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sk.netdisk.config.OssConfig;
import com.sk.netdisk.enums.FilePathEnum;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.util.CommonUtils;
import com.sk.netdisk.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * OSS工具类
 *
 * @author 11921
 */
@Component
public class OSSUtil {
    @Autowired
    OssConfig ossConfig;

    /**
     * 上传图片
     *
     * @param file       文件
     * @param targetAddr 目标路径
     * @return
     */
    public String upload(MultipartFile file, String targetAddr) {
        // 获取不重复的随机名
        String fileName = String.valueOf(IdWorker.getId());
        // 获取文件的扩展名如png,jpg等
        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        // 获取文件存储的相对路径(带文件名)
        String relativeAddr = targetAddr + fileName + extension;
        try {
            // 创建OSSClient实例
            OSS ossClient = ossConfig.getOssClient();
            // 上传文件
            ossClient.putObject(ossConfig.getBucketName(), relativeAddr, file.getInputStream());
//            // 关闭OSSClient。
//            ossClient.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ossConfig.getUrl() + relativeAddr;
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

    /**
     * 用户创建文件夹
     *
     * @param folderName 文件夹名字
     * @return
     */
    public String createFolder(String folderName) {
        OSS ossClient = ossConfig.getOssClient();
        try {
            String content = "";
            String userIndex = "/" + UserUtil.getLoginUserId() + "/";
            folderName = "netDisk" + userIndex + folderName;
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossConfig.getBucketName(), folderName
                    , new ByteArrayInputStream(content.getBytes()));
            ossClient.putObject(putObjectRequest);

        } catch (OSSException oe) {
            oe.printStackTrace();
        } catch (ClientException ce) {
            ce.printStackTrace();
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
        return folderName;
    }

    // 创建OSSClient实例。
    public void list() {
        // 指定前缀，例如exampledir/object。
        String keyPrefix = "netDisk/" + 1;
        // 创建OSSClient实例。
        OSS ossClient = ossConfig.getOssClient();

        try {
            // 列举包含指定前缀的文件。默认列举100个文件。
            ObjectListing objectListing =
                    ossClient.listObjects(new ListObjectsRequest(ossConfig.getBucketName())
                            .withPrefix(keyPrefix));
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
            }
        } catch (OSSException oe) {
            oe.printStackTrace();
        } catch (ClientException ce) {
            ce.printStackTrace();
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
        }
    }

    public void easyMUpload(MultipartFile[] files) throws Exception {
        for (MultipartFile file : files) {
            easyUpload(file);
        }
    }


    public String easyUpload(MultipartFile file) throws Exception {
        // 获取不重复的随机名
        String fileName = String.valueOf(IdWorker.getId());
        // 获取文件的扩展名如png,jpg等
        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        // 获取文件存储的相对路径(带文件名)
        String relativeAddr = FilePathEnum.DATA.getPath() + fileName + extension;
        try {
            // 创建OSSClient实例
            OSS ossClient = ossConfig.getOssClient();
            // 上传文件
            ossClient.putObject(ossConfig.getBucketName(), relativeAddr, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ossConfig.getUrl() + relativeAddr;
    }


    public void removeData(String objectName) {
        // 创建OSSClient实例
        OSS ossClient = ossConfig.getOssClient();
        // 删除文件
        try {
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
        } catch (OSSException e) {
            e.printStackTrace();
        }
    }


    public void copyData(String sourceObjectName, String targetObjectName) {
        // 创建OSSClient实例
        OSS ossClient = ossConfig.getOssClient();
        try {
            ossClient.copyObject(ossConfig.getBucketName(), sourceObjectName, ossConfig.getBucketName(), targetObjectName);
        } catch (OSSException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        String str="https://img.lovelsj.com/netDisk/1/1653947955631161346.jpg";
//        System.out.println(CommonUtils.getNewObjectName(str));
//        System.out.println(CommonUtils.getNewLink(str));
//    }

}
