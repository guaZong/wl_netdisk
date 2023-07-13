package com.sk.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.netdisk.pojo.Share;
import com.sk.netdisk.pojo.Transfer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
* @author Administrator
* @description 针对表【transfer_log】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity gen.domain.TransferLog
*/
@Mapper
public interface TransferMapper extends BaseMapper<Transfer> {

}




