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

    private Set<Integer> sids;

    private String code;

    private Integer targetFolderId;

    private String password;

    private String rePassword;

    private String email;

    private String phoneNumber;

    private Integer shareId;

    private String passCode;

    private String link;



}
