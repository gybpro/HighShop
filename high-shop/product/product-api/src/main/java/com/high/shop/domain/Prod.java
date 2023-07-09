package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
* 商品
* @TableName prod
*/
@TableName(value ="prod")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Prod implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long prodId;

    /**
     * 商品名称
     */
    @NotBlank(message="商品名称不能为空")
    @Size(max= 300, message="长度不能超过300")
    private String prodName;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 原价
     */
    private BigDecimal oriPrice;

    /**
     * 现价
     */
    private BigDecimal price;

    /**
     * 简要描述,卖点等
     */
    @Size(max= 500, message="长度不能超过500")
    private String brief;

    /**
     * 详细描述
     */
    @Size(max= -1, message="长度不能超过-1")
    private String content;

    /**
     * 商品主图
     */
    @Size(max= 255, message="长度不能超过255")
    private String pic;

    /**
     * 商品图片，以,分割
     */
    @Size(max= 1000, message="长度不能超过1000")
    private String imgs;

    /**
     * 默认是1，表示正常状态, -1表示删除, 0下架
     */
    @TableLogic
    private Integer status;

    /**
     * 商品分类
     */
    private Long categoryId;

    /**
     * 销量
     */
    private Integer soldNum;

    /**
     * 总库存
     */
    private Integer totalStocks;

    /**
     * 配送方式json见TransportModeVO
     */
    private Object deliveryMode;

    /**
     * 运费模板id
     */
    private Long deliveryTemplateId;

    /**
     * 录入时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 上架时间
     */
    private LocalDateTime putawayTime;

    /**
     * 版本 乐观锁
     */
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private List<Long> tagList;

    @TableField(exist = false)
    private List<Sku> skuList;

    @TableField(exist = false)
    private DeliveryModeVo deliveryModeVo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class DeliveryModeVo {
        private Boolean hasShopDelivery;
        private Boolean hasUserPickUp;
    }

}
