package com.netdisk.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.netdisk.system.mapper.GroupMapper;
import com.netdisk.system.pojo.Group;
import com.netdisk.system.service.GroupService;
import org.springframework.stereotype.Service;

/**
* @author lsj
* @description 针对表【group】的数据库操作Service实现
* @createDate 2022-11-29 10:50:12
*/
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group>
    implements GroupService {

}




