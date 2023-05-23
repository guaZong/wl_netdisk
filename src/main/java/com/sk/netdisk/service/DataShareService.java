package com.sk.netdisk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sk.netdisk.pojo.Data;
import com.sk.netdisk.pojo.DataShare;
import com.sk.netdisk.pojo.dto.DataDetInfoDto;
import com.sk.netdisk.pojo.dto.ShareInfoDto;

import java.util.List;


/**
 * @author Administrator
 * @description 针对表【data_share】的数据库操作Service
 * @createDate 2022-11-29 10:50:12
 */
public interface DataShareService extends IService<DataShare> {

    /**
     * 分享文件
     *
     * @param dataId       文件id集合
     * @param passCode     文件提取码
     * @param accessNum    文件访问人数
     * @param accessStatus 访问权限
     * @param expireDays   过期天数
     * @return DataShare
     */
    DataShare createShareFile(List<Integer> dataId, String passCode, Integer accessNum,
                              Integer accessStatus, Integer expireDays);

    /**
     * 取消分享
     *
     * @param shareId shareId
     * @param userId  userId
     */
    void cancelShare(Integer shareId, Integer userId);

    /**
     * 批量取消分享文件
     *
     * @param shareIds shareIds
     */
    void batchCancelShare(List<Integer> shareIds);

    /**
     * 遍历我的分享
     *
     * @return List<ShareInfoDto>
     */
    List<ShareInfoDto> traverseShares();

    /**
     * 将文件保存到我的资源
     *
     * @param dataIds        文件id集合
     * @param shareId        分享id
     * @param targetFolderId 目标文件夹id
     * @param code           保存所需要的文件提取码
     */
    void saveToMyResource(List<Integer> dataIds, Integer shareId, Integer targetFolderId, String code);

    /**
     * 通过链接中的uuid获取分享文件
     *
     * @param uuid     uuid
     * @param passCode passCode
     * @return List<DataDetInfoDto>
     */
    List<DataDetInfoDto> getShareData(String uuid, String passCode);

    /**
     * 无权限遍历分享文件
     *
     * @param parentDataId parentDataId
     * @param passCode     passCode
     * @param shareId      shareId
     * @return List<DataDetInfoDto>
     */
    List<DataDetInfoDto> infoShareData(Integer parentDataId, String passCode, Integer shareId);

    /**
     * 通过uuid和code返回分享id
     *
     * @param uuid     uuid
     * @param passCode passCode
     * @return Integer
     */
    Integer findIdByUidAndCode(String uuid, String passCode);
}
