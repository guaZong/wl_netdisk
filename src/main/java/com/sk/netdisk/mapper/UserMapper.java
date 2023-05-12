package com.sk.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sk.netdisk.pojo.User;
import com.sk.netdisk.pojo.dto.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;


/**
* @author Administrator
* @description 针对表【user】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity gen.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    UserInfoDto findUserById(Integer userId);
}




