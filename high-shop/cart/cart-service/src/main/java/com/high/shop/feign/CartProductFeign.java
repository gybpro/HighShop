package com.high.shop.feign;

import com.high.shop.domain.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@FeignClient("product-service")
public interface CartProductFeign {

    /**
     * 根据skuId获取库存单位(具体产品)库存数量
     * @param skuId
     * @return
     */
    @GetMapping("/prod/prod/getSkuStocks")
    Integer getSkuStocks(@RequestParam("skuId") Long skuId);

    /**
     * 根据skuIds获取库存单位(具体产品)库存信息列表
     * @param skuIds
     * @return
     */
    @GetMapping("/prod/prod/getSkuListByIds")
    List<Sku> getSkuListByIds(@RequestParam("skuIds") List<Long> skuIds);

}
