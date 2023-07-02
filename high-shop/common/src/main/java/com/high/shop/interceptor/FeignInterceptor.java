package com.high.shop.interceptor;

import com.high.shop.base.BaseController;
import com.high.shop.constant.CommonConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/*
    微服务调用时：
        必须将请求头的token信息传递下去
            浏览器 ----------> A ----------> B

        (第三方回调)生成一个全局的token信息，可以传递它
            MQ ----------> A ----------> B
            微信、支付 ----------> A ----------> B
 */
@Configuration
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {

        // 获取request对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) BaseController.getRequestAttributes();

        // 当前请求头向下传递token数据
        if(!ObjectUtils.isEmpty(attributes)){
            HttpServletRequest request = attributes.getRequest();

            String authorizationHeader = request.getHeader(CommonConstant.AUTHORIZATION_PREFIX);

            // 请求头不为空
            if(StringUtils.hasText(authorizationHeader)){
                // 通过请求头的方式向下传递token信息
                requestTemplate.header(CommonConstant.AUTHORIZATION_PREFIX,authorizationHeader);
            }

        } else {
            // 代表是第三方的回调，我们需要根据默认的token向下传递
            requestTemplate.header(CommonConstant.AUTHORIZATION_PREFIX, CommonConstant.COMMON_JWT_TOKEN);
        }
    }
}
