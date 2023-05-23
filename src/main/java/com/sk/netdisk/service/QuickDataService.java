package com.sk.netdisk.service;

import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.QuickData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【quick_data】的数据库操作Service
* @createDate 2023-05-23 09:22:35
*/
public interface QuickDataService extends IService<QuickData> {
    /**
     * 添加快速访问
     * @param dataId dataId
     * @return boolean
     */
    boolean addQuickAccess(List<Integer> dataId);

    /**
     * 获取快捷访问id集合
     * @return List<Data>
     */
    List<Data> getQuickDataIdList();

    /**
     * 删除快速访问
     * @param quickAccessId quickAccessId
     * @return boolean
     */
    boolean delQuickAccess(Integer quickAccessId);

}
