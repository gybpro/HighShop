package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.*;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author high
 * 购物车
 * @TableName basket
 */
@TableName(value ="basket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Basket implements Serializable {

    /**
     * 主键
     */
    @TableId()
    @NotNull(message="主键不能为空")
    private Long basketId;

    /**
     * 店铺ID
     */
    @NotNull(message="店铺ID不能为空")
    private Long shopId;

    /**
     * 产品ID
     */
    @NotNull(message="产品ID不能为空")
    private Long prodId;

    /**
     * SkuID
     */
    @NotNull(message="SkuID不能为空")
    private Long skuId;

    /**
     * 用户ID
     */
    @NotBlank(message="用户ID不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String userId;

    /**
     * 购物车产品个数
     */
    @NotNull(message="购物车产品个数不能为空")
    private Integer basketCount;

    /**
     * 购物时间
     */
    @NotNull(message="购物时间不能为空")
    private LocalDateTime basketDate;

    /**
     * 满减活动ID
     */
    private Long discountId;

    /**
     * 分销推广人卡号
     */
    @Size(max= 36, message="长度不能超过36")
    private String distributionCardNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
