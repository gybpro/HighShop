package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author high
 * 订单项
 * @TableName order_item
 */
@TableName(value ="order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class OrderItem implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long orderItemId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 订单order_number
     */
    @NotBlank(message="订单order_number不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String orderNumber;

    /**
     * 产品ID
     */
    @NotNull(message="产品ID不能为空")
    private Long prodId;

    /**
     * 产品SkuID
     */
    @NotNull(message="产品SkuID不能为空")
    private Long skuId;

    /**
     * 购物车产品个数
     */
    @NotNull(message="购物车产品个数不能为空")
    private Integer prodCount;

    /**
     * 产品名称
     */
    @NotBlank(message="产品名称不能为空")
    @Size(max= 120, message="长度不能超过120")
    private String prodName;

    /**
     * sku名称
     */
    @Size(max= 120, message="长度不能超过120")
    private String skuName;

    /**
     * 产品主图片路径
     */
    @NotBlank(message="产品主图片路径不能为空")
    @Size(max= 255, message="长度不能超过255")
    private String pic;

    /**
     * 产品价格
     */
    @NotNull(message="产品价格不能为空")
    private BigDecimal price;

    /**
     * 用户Id
     */
    @NotBlank(message="用户Id不能为空")
    @Size(max= 36, message="长度不能超过36")
    private String userId;

    /**
     * 商品总金额
     */
    @NotNull(message="商品总金额不能为空")
    private BigDecimal productTotalAmount;

    /**
     * 购物时间
     */
    @NotNull(message="购物时间不能为空")
    private LocalDateTime recTime;

    /**
     * 评论状态： 0 未评价  1 已评价
     */
    @NotNull(message="评论状态： 0 未评价  1 已评价不能为空")
    private Integer commSts;

    /**
     * 推广员使用的推销卡号
     */
    @Size(max= 36, message="长度不能超过36")
    private String distributionCardNo;

    /**
     * 加入购物车时间
     */
    private LocalDateTime basketDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
