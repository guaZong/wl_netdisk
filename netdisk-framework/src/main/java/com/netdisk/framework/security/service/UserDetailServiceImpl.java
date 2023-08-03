package com.netdisk.framework.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netdisk.common.core.domain.SecurityUser;
import com.netdisk.system.mapper.UserMapper;
import com.netdisk.system.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lsj
 * @description 账号密码登录的实现验证类,主要实现loadUserByUsername,验证成功返回用户信息(包括权限等)
 */
@Service("UserDetailServiceImpl")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",username);
        user=userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new UsernameNotFoundException("账号不存在！");
        }
        List<String> list = new ArrayList<>();
        list.add(user.getStatus() + "");
        return new SecurityUser(user.getUserId(),username,user.getPassword(),list);
    }
}
