package com.netdisk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netdisk.system.pojo.MailList;
import org.apache.ibatis.annotations.Mapper;


/**
* @author lsj
* @description 针对表【mail_list】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity com.netdisk.system.pojo.MailList
*/
@Mapper
public interface MailListMapper extends BaseMapper<MailList> {

}




