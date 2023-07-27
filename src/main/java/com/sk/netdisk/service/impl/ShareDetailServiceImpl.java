package com.sk.netdisk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sk.netdisk.enums.AppExceptionCodeMsg;
import com.sk.netdisk.exception.AppException;
import com.sk.netdisk.pojo.ShareDetail;
import com.sk.netdisk.service.ShareDetailService;
import com.sk.netdisk.mapper.ShareDetailMapper;
import org.springframework.stereotype.Service;

/**
 * @author lsj
 * @description 针对表【share_detail】的数据库操作Service实现
 * @createDate 2023-07-27 13:35:29
 */
@Service
public class ShareDetailServiceImpl extends ServiceImpl<ShareDetailMapper, ShareDetail>
        implements ShareDetailService {


    @Override
    public void createShareDetail(Integer dataShareId) {
        ShareDetail detail = this.getOne(new QueryWrapper<ShareDetail>().eq("data_share_id", dataShareId));
        if (detail != null) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        ShareDetail shareDetail = new ShareDetail();
        //查看次数
        shareDetail.setLookNum(0);
        //保存次数
        shareDetail.setSaveNum(0);
        //下载次数
        shareDetail.setDownloadNum(0);
        shareDetail.setDataShareId(dataShareId);
        this.save(shareDetail);
    }

    @Override
    public void deleteShareDetail(Integer dataShareId) {
        QueryWrapper<ShareDetail> shareDetailQueryWrapper = new QueryWrapper<ShareDetail>().eq("data_share_id", dataShareId);
        ShareDetail detail = this.getOne(shareDetailQueryWrapper);
        if (detail == null) {
            throw new AppException(AppExceptionCodeMsg.BUSY);
        }
        this.remove(shareDetailQueryWrapper);
    }


    @Override
    public boolean addLookNum(Integer dataShareId) {
        return this.update(new UpdateWrapper<ShareDetail>().setSql("look_num=look_num+1")
                .eq("data_share_id", dataShareId));
    }

    @Override
    public boolean addSaveNum(Integer dataShareId) {
        return this.update(new UpdateWrapper<ShareDetail>().setSql("save_num=save_num+1")
                .eq("data_share_id", dataShareId));
    }

    @Override
    public boolean addDownLoadNum(Integer dataShareId) {
        return this.update(new UpdateWrapper<ShareDetail>().setSql("download_num=download_num+1")
                .eq("data_share_id", dataShareId));
    }

    @Override
    public ShareDetail getShareDetailInfo(Integer dataShareId) {
        QueryWrapper<ShareDetail> shareDetailQueryWrapper = new QueryWrapper<ShareDetail>().eq("data_share_id", dataShareId);
        return this.getOne(shareDetailQueryWrapper);
    }

}




