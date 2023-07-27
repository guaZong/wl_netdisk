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
     *
     * @param dataShareId 分享id
     */
    void createShareDetail(Integer dataShareId);

    /**
     * 删除分享详细信息
     *
     * @param dataShareId 分享id
     */
    void deleteShareDetail(Integer dataShareId);

    /**
     * 分享信息浏览次数+1
     *
     * @param dataShareId 分享id
     * @return boolean
     */
    boolean addLookNum(Integer dataShareId);

    /**
     * 保存次数+1
     *
     * @param dataShareId 分享id
     * @return boolean
     */
    boolean addSaveNum(Integer dataShareId);

    /**
     * 下载次数+1
     *
     * @param dataShareId 分享id
     * @return boolean
     */
    boolean addDownLoadNum(Integer dataShareId);

    /**
     * 获取分享详细信息
     * @param dataShareId dataShareId
     * @return ShareDetail
     */
    ShareDetail getShareDetailInfo(Integer dataShareId);
}
