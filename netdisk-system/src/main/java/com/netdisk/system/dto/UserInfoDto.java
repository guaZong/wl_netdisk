package com.netdisk.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lsj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    private Integer userId;

    private String username;

    private String nickname;

    private String email;

    private Integer sex;

    private String avatar;

    private Integer isVip;

    private String createDate;

    private Integer status;

    private Integer isLock;
}
