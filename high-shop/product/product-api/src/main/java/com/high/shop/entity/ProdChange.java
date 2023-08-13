package com.high.shop.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("商品prod扣减库存数量对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdChange {

    @ApiModelProperty("商品prodId")
    private Long prodId;

    @ApiModelProperty("商品数量")
    private Integer count;

}
