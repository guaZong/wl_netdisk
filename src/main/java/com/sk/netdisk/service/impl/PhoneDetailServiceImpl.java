package com.sk.netdisk.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sk.netdisk.constant.RedisConstants;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.mapper.UserMapper;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.User;
import com.sk.netdisk.pojo.security.SecurityPhone;
import com.sk.netdisk.util.CommonUtils;
import com.sk.netdisk.util.Redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service("PhoneDetailServiceImpl")
public class PhoneDetailServiceImpl implements UserDetailsService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    DataMapper dataMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //验证码正确之后,根据手机号查询用户
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("username",username);
        User user= userMapper.selectOne(queryWrapper);
        //判断用户是否存在
        if(user==null){
            //不存在的话 创建新的用户
            user=createUserByPhoneCode(username);
            if(user==null){
                throw new UsernameNotFoundException("系统异常！");
            }
        }
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add(user.getStatus().toString());
        return new SecurityPhone(user.getUserId(),user.getUsername(),arrayList);
    }

    /**
     * 创建新用户
     * @param phoneNumber
     * @return 返回新用户的对象
     */
    public User createUserByPhoneCode(String phoneNumber){
        Date date=new Date();
        User user=new User();
        user.setStatus(0);
        user.setUsername(phoneNumber);
        user.setCreateTime(date);
        user.setPassword("");
        user.setAvatar("https://img.lovelsj.com/netDisk/avatar/detailAvatar.png");
        user.setNickname("用户"+RandomUtil.randomNumbers(6));
        userMapper.insert(user);
        Data data=new Data();
        data.setName("我的资源");
        data.setType(0);
        data.setParentDataId(0);
        data.setCreateTime(new Date());
        data.setCreateBy(user.getUserId());
        data.setIsDelete(0);
        dataMapper.insert(data);
        return user;
    }
}
