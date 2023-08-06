package com.high.shop.vo;

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
@ApiModel("购物车VO对象")
public class CartVO {

    @ApiModelProperty("购物车中店铺列表")
    private List<ShopVO> shopList;

    @ApiModelProperty("购物车金额对象")
    private CartMoneyVO cartMoneyVO;

}
