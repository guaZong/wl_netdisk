package com.sk.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sk.netdisk.pojo.File;
import com.sk.netdisk.service.FileService;
import com.sk.netdisk.mapper.FileMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【file】的数据库操作Service实现
* @createDate 2023-05-04 22:05:26
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{

}




