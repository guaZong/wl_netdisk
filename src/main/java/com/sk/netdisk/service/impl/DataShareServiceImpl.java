package com.sk.netdisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.mapper.DataMapper;
import com.sk.netdisk.mapper.DataShareMapper;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.DataShare;
import com.sk.netdisk.service.DataShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
* @author Administrator
* @description 针对表【data_share】的数据库操作Service实现
* @createDate 2022-11-29 10:50:12
*/
@Service
public class DataShareServiceImpl extends ServiceImpl<DataShareMapper, DataShare>
    implements DataShareService {

    private DataMapper dataMapper;

    @Autowired
    public DataShareServiceImpl(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    @Override
    public Integer createShareFile(Integer dataId, String passCode, Integer accessNum, Integer accessStatus) {
        Data data = dataMapper.selectById(dataId);
        if(Objects.isNull(data)){
            throw new AppException(AppExceptionCodeMsg.DATA_NOT_EXISTS);
        }
        if(accessStatus==1){

        }

        return null;
    }

    @Override
    public Integer cancelShare(Integer shareId) {
        return null;
    }
}




