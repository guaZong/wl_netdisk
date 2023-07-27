package com.sk.netdisk.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lsj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareInfoDto {

    /**
     * 分享id
     */
    private Integer dataShareId;
    /**
     * 分享链接
     */
    private String link;
    /**
     * 提取码
     */
    private String passCode;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 过期时间
     */
    private Integer expireDays;
    /**
     * 保存次数
     */
    private Integer saveNum;
    /**
     * 下载次数
     */
    private Integer downloadNum;
    /**
     * 浏览次数
     */
    private Integer lookNum;
    /**
     * 分享的文件id集合
     */
    private List<Integer> dataIds;
    /**
     * 分享的文件名字集合
     */
    private List<String> nameList;
    /**
     * 分享的文件类型集合
     */
    private List<Integer> type;
    /**
     * 允许查看人数
     */
    private Integer accessNum;
    /**
     * 允许查看人数状态(0是有人数限制,1是无人数限制,2是过期,3是被人删除)
     */
    private Integer accessStatus;
}
