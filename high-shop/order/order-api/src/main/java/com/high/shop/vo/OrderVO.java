package com.high.shop.vo;

import com.high.shop.domain.UserAddr;
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
@ApiModel("订单确认的返回对象")
public class OrderVO {

    @ApiModelProperty("用户的收货地址")
    private UserAddr userAddr;

    @ApiModelProperty("店铺集合")
    private List<ShopOrder> shopCartOrders;

    @ApiModelProperty("订单商品总数量")
    private Integer totalCount;

    @ApiModelProperty("实际金额")
    private BigDecimal actualTotal = BigDecimal.ZERO;

    @ApiModelProperty("总金额")
    private BigDecimal total = BigDecimal.ZERO;

    @ApiModelProperty("满减")
    private BigDecimal shopReduce = BigDecimal.ZERO;

    @ApiModelProperty("运费")
    private BigDecimal transfee = BigDecimal.ZERO;

    @ApiModelProperty("买家留言")
    private String remarks;

    @ApiModelProperty("选择的优惠券id")
    private List<Long> couponIds;

    @ApiModelProperty("订单请求来源，0商品详情页，1购物车页面")
    private Integer source = 0;

}
