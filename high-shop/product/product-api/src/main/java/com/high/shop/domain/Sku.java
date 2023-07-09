package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
* 单品SKU表
* @TableName sku
*/
@TableName(value ="sku")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Sku implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long skuId;

    /**
     * 商品ID
     */
    @NotNull(message="商品ID不能为空")
    private Long prodId;

    /**
     * 销售属性组合字符串 格式是p1:v1;p2:v2
     */
    @Size(max= 2000, message="长度不能超过2000")
    private String properties;

    /**
     * 原价
     */
    private BigDecimal oriPrice;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 商品在付款减库存的状态下，该sku上未付款的订单数量
     */
    @NotNull(message="商品在付款减库存的状态下，该sku上未付款的订单数量不能为空")
    private Integer stocks;

    /**
     * 实际库存
     */
    private Integer actualStocks;

    /**
     * 修改时间
     */
    @NotNull(message="修改时间不能为空")
    private LocalDateTime updateTime;

    /**
     * 记录时间
     */
    @NotNull(message="记录时间不能为空")
    private LocalDateTime recTime;

    /**
     * 商家编码
     */
    @Size(max= 100, message="长度不能超过100")
    private String partyCode;

    /**
     * 商品条形码
     */
    @Size(max= 100, message="长度不能超过100")
    private String modelId;

    /**
     * sku图片
     */
    @Size(max= 500, message="长度不能超过500")
    private String pic;

    /**
     * sku名称
     */
    @Size(max= 120, message="长度不能超过120")
    private String skuName;

    /**
     * 商品名称
     */
    @Size(max= 255, message="长度不能超过255")
    private String prodName;

    /**
     * 版本号
     */
    @NotNull(message="版本号不能为空")
    private Integer version;

    /**
     * 商品重量
     */
    private Double weight;

    /**
     * 商品体积
     */
    private Double volume;

    /**
     * 0 禁用 1 启用
     */
    @TableLogic
    private Integer status;

    /**
     * 0 正常 1 已被删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
