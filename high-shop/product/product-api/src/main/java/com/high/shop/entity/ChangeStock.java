package com.high.shop.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@ApiModel("商品prod和sku扣减库存数量对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeStock {

    private List<ProdChange> prodChangeList;

    private List<SkuChange> skuChangeList;

}
