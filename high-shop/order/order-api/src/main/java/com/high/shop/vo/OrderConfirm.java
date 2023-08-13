package com.high.shop.vo;

import com.high.shop.domain.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
@ApiModel("订单确认的入参对象")
public class OrderConfirm {

    @ApiModelProperty("购物车的ids")
    private List<Long> basketIds;

    @ApiModelProperty("订单条目对象")
    private OrderItem orderItem;

}
