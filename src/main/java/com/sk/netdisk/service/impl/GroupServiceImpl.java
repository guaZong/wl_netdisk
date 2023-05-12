package com.sk.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sk.netdisk.mapper.GroupMapper;
import com.sk.netdisk.pojo.Group;
import com.sk.netdisk.service.GroupService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【group】的数据库操作Service实现
* @createDate 2022-11-29 10:50:12
*/
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group>
    implements GroupService {

}




