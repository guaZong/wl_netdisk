package com.sk.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sk.netdisk.mapper.TransferMapper;
import com.sk.netdisk.pojo.Transfer;
import com.sk.netdisk.service.TransferService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【transfer_log】的数据库操作Service实现
* @createDate 2022-11-29 10:50:12
*/
@Service
public class TransferServiceImpl extends ServiceImpl<TransferMapper, Transfer>
    implements TransferService {

    @Override
    public Transfer insertTransfer() {
        return null;
    }
}




