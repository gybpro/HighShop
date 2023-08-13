package com.high.shop.feign;

import com.high.shop.domain.Basket;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@FeignClient("cart-service")
public interface OrderCartFeign {

    /**
     * 根据basketIds获取购物车列表信息
     * @param ids
     * @return
     */
    @GetMapping("/p/shopCart/getBasketListByIds")
    List<Basket> getBasketListByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 删除用户购买商品在购物车中的记录
     *
     * @param userId
     * @param skuIds
     * @return
     */
    @PostMapping("/p/shopCart/deleteUserBasket")
    Boolean deleteUserBasket(@RequestParam("userId") String userId, @RequestParam("skuIds") List<Long> skuIds);

}
