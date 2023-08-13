package com.high.shop.feign;

import com.high.shop.domain.UserAddr;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@FeignClient("member-service")
public interface OrderMemberFeign {

    /**
     * 查询用户默认收获地址
     * @param userId
     * @return
     */
    @GetMapping("/p/address/defaultAddr/{userId}")
    UserAddr defaultAddr(@PathVariable("userId") String userId);

}
