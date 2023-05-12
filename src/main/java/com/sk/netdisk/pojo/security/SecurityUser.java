package com.sk.netdisk.pojo.security;

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
 * @author Administrator
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUser implements UserDetails {
    @ApiModelProperty(value = "用户id")
    private Integer userId;
    @ApiModelProperty(value = "账号")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "用户权限列表")
    private List<String> permissionValueList;

    public SecurityUser(Integer userId, List<String> permissionValueList) {
        this.userId = userId;
        this.permissionValueList = permissionValueList;
    }
    public SecurityUser(Integer userId, String username, String password) {
        this.userId = userId;
        this.username=username;
        this.password=password;
    }


    public SecurityUser(Integer userId) {
        this.userId = userId;
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
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
