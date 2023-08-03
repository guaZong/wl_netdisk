package com.netdisk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.netdisk.system.pojo.Transfer;
import org.apache.ibatis.annotations.Mapper;


/**
* @author lsj
* @description 针对表【transfer_log】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity com.sk.netdisk.pojo.TransferLog
*/
@Mapper
public interface TransferMapper extends BaseMapper<Transfer> {

}




