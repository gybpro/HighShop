package com.high.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.constant.AuthServerConstant;
import com.high.shop.domain.SysUser;
import com.high.shop.domain.User;
import com.high.shop.mapper.UserMapper;
import com.high.shop.properties.WxLoginProperties;
import com.high.shop.service.SysUserService;
import com.high.shop.mapper.SysUserMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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

    private final UserMapper userMapper;

    private final RestTemplate restTemplate;

    private final WxLoginProperties wxLoginProperties;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, UserMapper userMapper, RestTemplate restTemplate, WxLoginProperties wxLoginProperties) {
        this.sysUserMapper = sysUserMapper;
        this.userMapper = userMapper;
        this.restTemplate = restTemplate;
        this.wxLoginProperties = wxLoginProperties;
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
            // 前台用户登录操作(小程序用户登录)
            case AuthServerConstant.User: {
                // 当微信小程序启动成功后，会发送一个请求，进行微信的第三方登录
                // http://127.0.0.1/oauth/token?grant_type=password&username=011E4Hll2Oi8ia4JOvnl2EowQA1E4HlA&password=WECHAT
                // username属性是在微信端生成的JSCode码，它(微信)也是使用oauth2来做权限的校验和认证的
                // 之前在学习oauth2的时候，有一种授权方式是验证码授权，通过请求来获取code码，再根据code码，获取token令牌
                // 微信也是一样的，只不过我们在微信的客户端生成的code码，然后调用微信的接口，将code码传递过来，微信会将token响应回来
                // 进行第三方登录操作
                // 给发送的第三方登录的url进行占位符赋值操作
                // https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code
                String url = String.format(
                        wxLoginProperties.getResourceUrl(),
                        wxLoginProperties.getAppId(),
                        wxLoginProperties.getAppSecret(),
                        username
                );

                // 使用restTemplate发送get请求
                String jsonStr = restTemplate.getForObject(url, String.class);

                // 解析json
                JSONObject jsonObj = JSON.parseObject(jsonStr);

                // 如果包含openid，则证明第三方登录成功，如果不包含，返回null
                if (jsonObj.containsKey("openid")) {

                    // 通过openid(user主键)来查询是否有当前用户
                    String openid = jsonObj.getString("openid");
                    User user = userMapper.selectById(openid);

                    // 获取用户的ip地址
                    String ip = request.getRemoteAddr();

                    if (ObjectUtils.isEmpty(user)) {
                        // 当前用户没有注册到数据库，完成注册(新增)操作
                        user = new User().setUserId(openid)
                                .setModifyTime(LocalDateTime.now())
                                .setUserRegtime(LocalDateTime.now())
                                .setUserLasttime(LocalDateTime.now())
                                .setStatus(1)
                                .setUserLastip(ip)
                                .setUserRegip(ip);

                        int flag = userMapper.insert(user);

                        if (flag <= 0) {
                            throw new RuntimeException("用户注册失败");
                        }
                    }

                    // 不需要授权，因为所有前端用户的权限是统一的
                    return user;
                }
            }
            default: {

            }
        }

        // 登录失败
        return null;
    }

}




