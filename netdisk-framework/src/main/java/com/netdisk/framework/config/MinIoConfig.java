package com.netdisk.framework.config;

import com.netdisk.common.utils.upload.MinIoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lsj
 * @description MinIoConfig
 * @createDate 2023/8/15 14:20
 */
@Configuration
public class MinIoConfig {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.bucketName}")
    private String bucketName;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public void initClint(){
        new MinIoUtil(endpoint,bucketName,accessKey,secretKey);
    }

}
