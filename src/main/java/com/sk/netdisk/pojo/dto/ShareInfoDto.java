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

    private Integer id;

    private String link;

    private String passCode;

    private String createTime;

    private Integer expireDays;

    private Integer saveNum;

    private Integer shareNum;

    private Integer downloadNum;

    private Integer lookNum;

    private List<Integer> dataIds;
}
