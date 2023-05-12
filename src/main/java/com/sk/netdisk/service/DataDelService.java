package com.sk.netdisk.service;

import com.sk.netdisk.pojo.DataDel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sk.netdisk.pojo.vo.DataDelInfoVo;
import com.sk.netdisk.util.ResponseResult;

import java.util.List;

/**
* @author Administrator
* @description 针对表【data_del】的数据库操作Service
* @createDate 2023-05-03 15:28:35
*/
public interface DataDelService extends IService<DataDel> {

    /**
     * 创建回收站删除文件
     * @param dataId 文件或文件夹id
     * @param userId 用户id
     * @return DataDel
     */
    DataDel insertDataDel(Integer dataId,Integer userId);

    /**
     * 删除回收站文件
     * @param dataDelId 文件夹或id
     * @return boolean
     */
    boolean deleteDataDel(Integer dataDelId);

    /**
     * 批量删除回收站文件
     * @param dataDelIds id集合
     * @return boolean
     */
    boolean batchDeleteDataDel(List<Integer> dataDelIds);

    /**
     * 遍历回收站
     * @param userId 用户id
     * @return List<DataDelInfoVo>
     */
    List<DataDelInfoVo> infoAllDataDel(Integer userId);


}
