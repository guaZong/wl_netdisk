package com.sk.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sk.netdisk.pojo.Share;
import com.sk.netdisk.service.ShareService;
import com.sk.netdisk.mapper.ShareMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【share】的数据库操作Service实现
* @createDate 2023-05-14 08:46:56
*/
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share>
    implements ShareService{

}




