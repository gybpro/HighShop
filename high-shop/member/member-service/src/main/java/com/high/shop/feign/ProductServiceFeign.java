package com.high.shop.feign;

import com.high.shop.domain.Prod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
// 尽可能一个微服务一个Feign对象，这样方便使用和维护
// 一个微服务如果对应多个Feign对象会报Bean重复错误，需要指定Feign对象，且会出现覆盖，可维护性变低
@FeignClient("product-service")
public interface ProductServiceFeign {

    @GetMapping("/prod/prod/getListByIds")
    List<Prod> getListByIds(@RequestParam("ids") List<Long> ids);

}
