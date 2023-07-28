package com.high.shop.log.aop;

import com.alibaba.fastjson.JSON;
import com.high.shop.base.BaseController;
import com.high.shop.domain.SysLog;
import com.high.shop.log.annotation.Log;
import com.high.shop.mapper.SysLogMapper;
import com.high.shop.mapper.SysUserMapper;
import com.high.shop.pool.ManagerThreadPool;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
// AOP = 切面(方法的增强) + 切入点(拦截点方法)
@Aspect // 切面
@Configuration
public class LogAop {
    private final SysLogMapper sysLogMapper;

    private final SysUserMapper sysUserMapper;

    public LogAop(SysLogMapper sysLogMapper, SysUserMapper sysUserMapper) {
        this.sysLogMapper = sysLogMapper;
        this.sysUserMapper = sysUserMapper;
    }

    // 环绕@Log注解的方法执行日志记录
    @Around("@annotation(com.high.shop.log.annotation.Log)")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        // 获取切入点方法的参数
        Object[] args = joinPoint.getArgs();

        // 记录执行时间
        long start = System.currentTimeMillis();

        // 执行切入点方法
        Object obj = null;
        try {
            obj = joinPoint.proceed(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        // 通过Security获取用户id
        Long userId = BaseController.getAuthenticationUserIdOfLong();

        // 获取用户名
        String username = sysUserMapper.selectById(userId).getUsername();

        // 获取注解的方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 获取注解的方法
        Method method = signature.getMethod();

        // 获取注解中的operation值(用户操作信息)
        Log annotation = method.getAnnotation(Log.class);
        String operation = annotation.operation();
        String value = annotation.value();

        // 获取ip地址
        String ip = BaseController.getRequestIp();

        // 通过日志线程池线程执行日志记录操作
        ManagerThreadPool.poolExecutor.execute(() -> sysLogMapper.insert(
                SysLog.builder()
                        .userId(userId)
                        .username(username)
                        .operation(
                                StringUtils.isEmpty(operation) ? value : operation
                        )
                        .method(
                                signature.getDeclaringTypeName() + "." + method.getName()
                        )
                        .params(JSON.toJSONString(args))
                        .time(end - start)
                        .ip(ip)
                        .createDate(LocalDateTime.now())
                        .build()
        ));

        return obj;
    }
}
