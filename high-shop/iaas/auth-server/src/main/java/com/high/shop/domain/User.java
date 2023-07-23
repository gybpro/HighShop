package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.*;
import java.util.ArrayList;
import java.util.Collection;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
* 用户表
* @TableName user
*/
@TableName(value ="user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class User implements Serializable, UserDetails {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotBlank(message="主键不能为空")
    @Size(max= 36, message="长度不能超过36")
    private String userId;

    /**
     * 用户昵称
     */
    @Size(max= 50, message="长度不能超过50")
    private String nickName;

    /**
     * 真实姓名
     */
    @Size(max= 50, message="长度不能超过50")
    private String realName;

    /**
     * 用户邮箱
     */
    @Size(max= 100, message="长度不能超过100")
    private String userMail;

    /**
     * 登录密码
     */
    @Size(max= 50, message="长度不能超过50")
    private String loginPassword;

    /**
     * 支付密码
     */
    @Size(max= 50, message="长度不能超过50")
    private String payPassword;

    /**
     * 手机号码
     */
    @Size(max= 50, message="长度不能超过50")
    private String userMobile;

    /**
     * 修改时间
     */
    @NotNull(message="修改时间不能为空")
    private LocalDateTime modifyTime;

    /**
     * 注册时间
     */
    @NotNull(message="注册时间不能为空")
    private LocalDateTime userRegtime;

    /**
     * 注册IP
     */
    @Size(max= 50, message="长度不能超过50")
    private String userRegip;

    /**
     * 最后登录时间
     */
    private LocalDateTime userLasttime;

    /**
     * 最后登录IP
     */
    @Size(max= 50, message="长度不能超过50")
    private String userLastip;

    /**
     * 备注
     */
    @Size(max= 500, message="长度不能超过500")
    private String userMemo;

    /**
     * M(男) or F(女)
     */
    private String sex;

    /**
     * 例如：2009-11-27
     */
    private String birthDate;

    /**
     * 头像图片路径
     */
    @Size(max= 255, message="长度不能超过255")
    private String pic;

    /**
     * 状态 1 正常 0 无效
     */
    @TableLogic
    @NotNull(message="状态 1 正常 0 无效不能为空")
    private Integer status;

    /**
     * 用户积分
     */
    private Integer score;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 客户端认证，前端都是同一个权限，不返回空即可
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 前端都是一个权限
        return new ArrayList<>();
    }

    /**
     * 微信小程序密码一般都是统一的，客户端认证
     *
     * @return
     */
    @Override
    public String getPassword() {
        return new BCryptPasswordEncoder().encode("WECHAT");
    }

    /**
     * 微信返回的openId是String类型的，我们将它作为用户的唯一标识来使用
     *
     * @return
     */
    @Override
    public String getUsername() {
        return this.userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }
}
