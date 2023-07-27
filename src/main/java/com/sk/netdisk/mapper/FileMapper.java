package com.sk.netdisk.mapper;

import com.sk.netdisk.pojo.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author lsj
* @description 针对表【file】的数据库操作Mapper
* @createDate 2023-05-04 22:05:26
* @Entity com.sk.netdisk.pojo.File
*/
@Mapper
public interface FileMapper extends BaseMapper<File> {

}




