package com.sk.netdisk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sk.netdisk.pojo.DataShare;

import java.util.List;


/**
* @author Administrator
* @description 针对表【data_share】的数据库操作Service
* @createDate 2022-11-29 10:50:12
*/
public interface DataShareService extends IService<DataShare> {

    /**
     * 分享文件
     * @param dataId 文件id
     * @param passCode 文件提取码
     * @param accessNum 文件访问人数
     * @param accessStatus 访问权限
     * @return
     */
   Integer createShareFile(Integer dataId,String passCode,Integer accessNum,Integer accessStatus);

    /**
     * 取消分享
     * @param shareId
     * @return
     */
   Integer cancelShare(Integer shareId);



}
