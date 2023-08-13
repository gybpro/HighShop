package com.high.shop.vo;

import com.high.shop.domain.OrderItem;
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
@ApiModel("店铺订单对象")
public class ShopOrder {

    @ApiModelProperty("商品条目集合")
    private List<OrderItem> shopCartItemDiscounts;

    @ApiModelProperty("店铺满减")
    private BigDecimal shopReduce = BigDecimal.ZERO;

    @ApiModelProperty("店铺运费")
    private BigDecimal transfee = BigDecimal.ZERO;


}
