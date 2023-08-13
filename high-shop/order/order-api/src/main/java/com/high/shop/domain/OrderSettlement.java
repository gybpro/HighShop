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
 *
 * @TableName order_settlement
 */
@TableName(value ="order_settlement")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class OrderSettlement implements Serializable {

    /**
     * 主键
     */
    @TableId
    @NotNull(message="主键不能为空")
    private Long settlementId;

    /**
     * 支付单号
     */
    @Size(max= 36, message="长度不能超过36")
    private String payNo;

    /**
     * 外部订单流水号
     */
    @Size(max= 255, message="长度不能超过255")
    private String bizPayNo;

    /**
     * order表中的订单号
     */
    @Size(max= 36, message="长度不能超过36")
    private String orderNumber;

    /**
     * 支付方式 1 微信支付 2 支付宝
     */
    private Integer payType;

    /**
     * 支付方式名称
     */
    @Size(max= 50, message="长度不能超过50")
    private String payTypeName;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 是否清算 0:否 1:是
     */
    private Integer isClearing;

    /**
     * 用户ID
     */
    @Size(max= 36, message="长度不能超过36")
    private String userId;

    /**
     * 创建时间
     */
    @NotNull(message="创建时间不能为空")
    private LocalDateTime createTime;

    /**
     * 清算时间
     */
    private LocalDateTime clearingTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 支付状态
     */
    private Integer payStatus;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
