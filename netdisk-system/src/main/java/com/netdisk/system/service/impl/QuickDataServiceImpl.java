package com.netdisk.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.netdisk.common.enums.AppExceptionCodeMsg;
import com.netdisk.common.exception.AppException;
import com.netdisk.common.utils.UserUtil;
import com.netdisk.system.mapper.QuickDataMapper;
import com.netdisk.system.pojo.Data;
import com.netdisk.system.pojo.QuickData;
import com.netdisk.system.service.QuickDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author lsj
 * @description 针对表【quick_data】的数据库操作Service实现
 * @createDate 2023-05-23 09:22:35
 */
@Service
public class QuickDataServiceImpl extends ServiceImpl<QuickDataMapper, QuickData>
        implements QuickDataService {

    @Autowired
    QuickDataMapper quickDataMapper;

    @Override
    public boolean addQuickAccess(List<Integer> dataIdList) {
        Integer userId = UserUtil.getLoginUserId();
        return this.saveBatch(quickDataMapper.findUserIdAndDataIdByIdList(dataIdList,userId));
    }

    @Override
    public List<Data> getQuickDataIdList() {
        Integer userId = UserUtil.getLoginUserId();
        return quickDataMapper.findDataByUserId(userId);
    }

    @Override
    public boolean delQuickAccess(Integer quickAccessId) {
        Integer userId=UserUtil.getLoginUserId();
        QuickData quickData = this.getById(quickAccessId);
        if(Objects.isNull(quickData) || !userId.equals(quickData.getUserId())){
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        return this.removeById(quickAccessId);
    }


}




