package com.netdisk.common.enums;


import com.netdisk.common.utils.UserUtil;
import lombok.AllArgsConstructor;

/**
 * @author: lsj
 * @date: 2023-01-29
 **/

@AllArgsConstructor
public enum FilePathEnum {
    /**
     * 头像路径
     */
    AVATAR("netDisk/avatar/", "头像路径"),

    /**
     * 文件存储路径
     */
    DATA("netDisk/", "文件存储路径");


    /**
     * 路径
     */
    private final String path;

    /**
     * 描述
     */
    private final String desc;


    public String getPath(){
        return path+ UserUtil.getLoginUserId()+"/";
    }

    public String getDesc(){
        return desc;
    }
}
