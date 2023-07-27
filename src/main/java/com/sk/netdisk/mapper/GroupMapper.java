package com.sk.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.netdisk.pojo.Group;
import org.apache.ibatis.annotations.Mapper;


/**
* @author lsj
* @description 针对表【group】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity com.sk.netdisk.pojo.Group
*/
@Mapper
public interface GroupMapper extends BaseMapper<Group> {

}




