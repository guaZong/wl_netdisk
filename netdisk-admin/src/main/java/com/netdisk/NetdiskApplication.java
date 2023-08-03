package com.netdisk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lsj
 * @description NetdiskApplication
 * @createDate 2023/8/2 16:13
 */
@SpringBootApplication
@MapperScan("com.netdisk.system.mapper")
public class NetdiskApplication {
    public static void main(String[] args) {
        SpringApplication.run(NetdiskApplication.class, args);
    }
}
