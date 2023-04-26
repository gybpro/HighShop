package com.high.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.constant.AuthServerConstant;
import com.high.shop.domain.SysUser;
import com.high.shop.service.SysUserService;
import com.high.shop.mapper.SysUserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
* @author high
* @description 针对表【sys_user(系统用户)】的数据库操作Service实现
* @createDate 2023-04-26 18:02:25
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService, UserDetailsService {
    private final SysUserMapper sysUserMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /*
    通过用户名查询数据库，完成授权操作
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 获取请求对象
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        // 获取请求头中的用户标记
        String loginType = request.getHeader(AuthServerConstant.LOGIN_TYPE);

        if (StringUtils.isBlank(loginType)) {
            return null;
        }

        switch (loginType) {
            // 后台管理系统用户登录操作
            case AuthServerConstant.SYS_USER: {
                // 根据用户名查询用户信息
                SysUser user = sysUserMapper.selectOne(
                        new LambdaQueryWrapper<SysUser>()
                                .eq(StringUtils.isNotBlank(username),  SysUser::getUsername, username)
                );

                if (ObjectUtils.isEmpty(user)) {
                    return null;
                }

                // 根据用户id查询所属权限信息
                List<String> authList = sysUserMapper.selectPermsListByUserId(user.getUserId());

                user.setAuths(authList);

                return user;
            }
            // 前台用户登录操作
            case AuthServerConstant.User: {

            }
        }
        // 登录失败
        return null;
    }
}




