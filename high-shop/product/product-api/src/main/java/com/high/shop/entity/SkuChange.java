package com.high.shop.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("商品sku扣减库存数量对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuChange {

    @ApiModelProperty("商品skuId")
    private Long skuId;

    @ApiModelProperty("商品数量")
    private Integer count;

}
