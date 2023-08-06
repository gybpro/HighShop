package com.high.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

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
@ApiModel("购物车中的金额对象")
public class CartMoneyVO {

    @ApiModelProperty("最终金额")
    private BigDecimal finalMoney = BigDecimal.ZERO;

    @ApiModelProperty("总金额")
    private BigDecimal totalMoney = BigDecimal.ZERO;

    @ApiModelProperty("优惠金额")
    private BigDecimal subtractMoney = BigDecimal.ZERO;

}
