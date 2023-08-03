package com.netdisk.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.netdisk.system.pojo.User;
import com.netdisk.system.dto.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;


/**
* @author lsj
* @description 针对表【user】的数据库操作Mapper
* @createDate 2022-11-29 10:50:12
* @Entity com.netdisk.system.pojo.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 遍历用户信息
     * @param userId userId
     * @return UserInfoDto
     */
    UserInfoDto findUserById(Integer userId);
}




