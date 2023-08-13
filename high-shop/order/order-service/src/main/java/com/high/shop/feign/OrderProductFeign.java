package com.high.shop.feign;

import com.high.shop.domain.Sku;
import com.high.shop.entity.ChangeStock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@FeignClient("product-service")
public interface OrderProductFeign {

    /**
     * 根据skuIds获取sku列表数据
     * @param skuIds
     * @return
     */
    @GetMapping("/prod/prod/getSkuListByIds")
    List<Sku> getSkuListByIds(@RequestParam("skuIds") List<Long> skuIds);

    /**
     * 修改prod和sku库存数量
     * @param changeStock
     */
    @PostMapping("/prod/prod/updateProdAndSkuStock")
    void updateProdAndSkuStock(@RequestBody ChangeStock changeStock);

}
