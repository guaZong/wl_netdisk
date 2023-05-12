package com.sk.netdisk.service.impl;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sk.netdisk.pojo.DataDel;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import com.sk.netdisk.service.DataDelService;
import com.sk.netdisk.mapper.DataDelMapper;
import com.sk.netdisk.util.ResponseResult;
import com.sk.netdisk.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【data_del】的数据库操作Service实现
* @createDate 2023-05-03 15:28:35
*/
@Service
public class DataDelServiceImpl extends ServiceImpl<DataDelMapper, DataDel>
    implements DataDelService{

   private final DataDelMapper dataDelMapper;

    @Autowired
    public DataDelServiceImpl(DataDelMapper dataDelMapper) {
        this.dataDelMapper = dataDelMapper;
    }

    @Override
    public DataDel insertDataDel(Integer dataId,Integer userId) {
        DataDel dataDel=new DataDel();
        dataDel.setDataId(dataId);
        dataDel.setCreateBy(userId);
        dataDel.setCreateTime(new Date());
        this.save(dataDel);
        return dataDel;
    }

    @Override
    public boolean deleteDataDel(Integer dataDelId) {
        return this.removeById(dataDelId);
    }

    @Override
    public boolean batchDeleteDataDel(List<Integer> dataDelIds) {
        return this.removeBatchByIds(dataDelIds);
    }

    @Override
    public List<DataDelInfoVo> infoAllDataDel(Integer userId) {
        return dataDelMapper.infoAllDataDel(userId);
    }



}




