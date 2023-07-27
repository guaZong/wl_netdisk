package com.sk.netdisk.service;

import com.sk.netdisk.pojo.ShareDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author lsj
* @description 针对表【share_detail】的数据库操作Service
* @createDate 2023-07-27 13:35:29
*/
public interface ShareDetailService extends IService<ShareDetail> {
    /**
     * 创建分享详细信息
     * @param dataShareId 分享id
     */
    void createShareDetail(Integer dataShareId);


}
