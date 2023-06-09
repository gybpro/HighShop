package com.high.shop.base;

import com.high.shop.enums.State;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class BaseController {
    // 正常响应信息
    public static <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    // 通过Security上下文对象获取授权用户信息
    public static Authentication getAuthenticationUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 获取授权用户ID
    public static String getAuthenticationUserId() {
        return getAuthenticationUser().getName();
    }

    // 获取Long类型的授权用户ID
    public static Long getAuthenticationUserIdOfLong() {
        return Long.valueOf(getAuthenticationUser().getName());
    }

    // 获取授权用户权限列表
    public static List<String> getAuthenticationUserPerms() {
        return getAuthenticationUser().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    // 获取请求上下文对象
    public static RequestAttributes getRequestAttributes(){
        return RequestContextHolder.getRequestAttributes();
    }

    // 检查操作
    public static void checked(boolean flag, State state) {
        if (!flag) throw new RuntimeException(state.getCode() + " : " + state.getMsg());
    }
}
