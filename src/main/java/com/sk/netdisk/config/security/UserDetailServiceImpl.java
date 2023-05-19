package com.sk.netdisk.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sk.netdisk.mapper.UserMapper;
import com.sk.netdisk.pojo.security.SecurityUser;
import com.sk.netdisk.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
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
