package com.netdisk.common.core.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lsj
 * 手机验证码登录的security实习类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityPhone implements UserDetails {
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "账号")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "用户权限列表")
    private List<String> permissionValueList;

    public SecurityPhone(Integer adminId, String username, List<String> permissionValueList) {
        this.userId = adminId;
        this.username=username;
        this.permissionValueList=permissionValueList;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> athorities = new ArrayList<>();
        for (String permissionValue :
                permissionValueList) {
            if (StringUtils.hasLength(permissionValue)) {
                athorities.add(new SimpleGrantedAuthority("ROLE_" + permissionValue));
            }
        }
        return athorities;
    }




    @Override
    public String getPassword() {
        return password;

    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}