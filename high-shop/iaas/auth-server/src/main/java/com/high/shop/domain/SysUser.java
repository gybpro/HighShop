package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.*;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @TableName sys_user
 */
@TableName(value = "sys_user")
@Data
public class SysUser implements Serializable, UserDetails {
    private Long userId;

    private String username;

    private String password;

    private String email;

    private String mobile;

    private Integer status;

    private Long createUserId;

    private Date createTime;

    private Long shopId;

    private static final long serialVersionUID = 1L;

    // 用户权限信息列表
    @TableField(exist = false)
    private List<String> auths;

    /*
    授权操作：
        将用户权限信息列表转换为框架所需的SimpleGrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<>();

        auths.forEach(auth -> {
            // 判断是否为空
            if (StringUtils.isNotBlank(auth)) {
                // 判断是否有多个权限
                if (auth.contains(",")) {
                    String[] authArr = auth.split(",");
                    Arrays.stream(authArr).forEach(authStr -> authList.add(new SimpleGrantedAuthority(authStr)));
                } else {
                    authList.add(new SimpleGrantedAuthority(auth));
                }
            }
        });
        return authList;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status == 1;
    }

    @Override
    public boolean isEnabled() {
        return status == 1;
    }
}
