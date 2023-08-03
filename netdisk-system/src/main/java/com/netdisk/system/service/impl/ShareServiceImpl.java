package com.netdisk.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.netdisk.system.mapper.ShareMapper;
import com.netdisk.system.pojo.Share;
import com.netdisk.system.service.ShareService;
import org.springframework.stereotype.Service;

/**
* @author lsj
* @description 针对表【share】的数据库操作Service实现
* @createDate 2023-05-14 08:46:56
*/
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share>
    implements ShareService{

}




