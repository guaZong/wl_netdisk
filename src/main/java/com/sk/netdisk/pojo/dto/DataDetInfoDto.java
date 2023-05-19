package com.sk.netdisk.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件普通信息
 *
 * @author lsj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDetInfoDto {

    /**
     * 文件id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;


    /**
     * 0代表文件夹
     * 1代表图片
     * 2代表视频
     * 3代表文档
     * 4代表音乐
     * 5代表种子
     * 6代表压缩包
     * 7代表其他
     */
    private Integer type;

    /**
     * 父级目录id
     */
    private Integer parentDataId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 创建人(上传文件的人)
     */
    private Integer createBy;
    /**
     * 文件链接
     */
    private String link;
    /**
     * 文件大小
     */
    private String size;

}
