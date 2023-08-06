package com.high.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel("购物车中店铺对象")
public class ShopVO {

    @ApiModelProperty("店铺中商品条目的集合")
    private List<CartItemVO> shopCartItems;

    // 优惠券  折扣
    @ApiModelProperty("店铺的满减优惠")
    private BigDecimal shopReduce;

    @ApiModelProperty("店铺中对应的运费")
    private BigDecimal freight;

}
