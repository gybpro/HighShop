package com.high.shop.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@FeignClient("member-service")
public interface ProductMemberFeign {

    /**
     * 根据用户id获取用户信息
     * @param ids
     * @return
     */
    @GetMapping("/p/user/getListByIds")
    List<Map<String, Object>> getListByIds(@RequestParam("ids") List<String> ids);

}
