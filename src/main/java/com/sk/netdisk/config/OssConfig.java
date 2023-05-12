package com.sk.netdisk.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.sk.netdisk.util.upload.OSSUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * oss配置类
 * @author lsj
 */
@Configuration
public class OssConfig {

    @Value("${aliyun.url}")
    private  String url;
    @Value("${aliyun.endpoint}")
    private  String endpoint;
    @Value("${aliyun.accessKeyId}")
    private  String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private  String accessKeySecret;
    @Value("${aliyun.bucketName}")
    private  String bucketName;


    @Bean
    public OSS getOssClient(){
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }
    @Bean
    public String getBucketName(){
        return bucketName;
    }
    @Bean
    public String getUrl(){
        return url;
    }

}
