package com.high.shop.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
@ApiModel("订单状态数量对象")
public class OrderStatusCount {

    @ApiModelProperty("待支付")
    private Integer unPay = 0;

    @ApiModelProperty("待发货")
    private Integer payed = 0;

    @ApiModelProperty("待收货")
    private Integer consignment = 0;

}
