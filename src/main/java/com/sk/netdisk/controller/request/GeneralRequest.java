package com.sk.netdisk.controller.request;

import lombok.Data;

import java.util.Set;

/**
 * 通用请求体
 * @author lsj
 */
@Data
public class GeneralRequest {

    private Set<Integer> ids;

    private Set<Integer> sIds;

    private String code;

    private Integer targetFolderId;



}
