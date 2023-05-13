package com.sk.netdisk.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sk.netdisk.pojo.File;
import com.sk.netdisk.service.FileService;
import com.sk.netdisk.mapper.FileMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
* @author Administrator
* @description 针对表【file】的数据库操作Service实现
* @createDate 2023-05-04 22:05:26
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{


    public static void main(String[] args) {
        System.out.println(RandomUtil.randomString("_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789-",25));

    }

}





