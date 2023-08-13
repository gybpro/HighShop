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
 * @TableName order_refund
 */
@TableName(value ="order_refund")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class OrderRefund implements Serializable {

    /**
     * 主键
     */
    @TableId
    @NotNull(message="主键不能为空")
    private Long refundId;

    /**
     * 店铺ID
     */
    @NotNull(message="店铺ID不能为空")
    private Long shopId;

    /**
     * 订单ID
     */
    @NotNull(message="订单ID不能为空")
    private Long orderId;

    /**
     * 订单流水号
     */
    @NotBlank(message="订单流水号不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String orderNumber;

    /**
     * 订单总金额
     */
    @NotNull(message="订单总金额不能为空")
    private Double orderAmount;

    /**
     * 订单项ID 全部退款是0
     */
    @NotNull(message="订单项ID 全部退款是0不能为空")
    private Long orderItemId;

    /**
     * 退款编号
     */
    @NotBlank(message="退款编号不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String refundSn;

    /**
     * 订单支付流水号
     */
    @NotBlank(message="订单支付流水号不能为空")
    @Size(max= 100, message="长度不能超过100")
    private String flowTradeNo;

    /**
     * 第三方退款单号(微信退款单号)
     */
    @Size(max= 200, message="长度不能超过200")
    private String outRefundNo;

    /**
     * 订单支付方式 1 微信支付 2 支付宝
     */
    private Integer payType;

    /**
     * 订单支付名称
     */
    @Size(max= 50, message="长度不能超过50")
    private String payTypeName;

    /**
     * 买家ID
     */
    @NotBlank(message="买家ID不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String userId;

    /**
     * 退货数量
     */
    private Integer goodsNum;

    /**
     * 退款金额
     */
    @NotNull(message="退款金额不能为空")
    private BigDecimal refundAmount;

    /**
     * 申请类型:1,仅退款,2退款退货
     */
    @NotNull(message="申请类型:1,仅退款,2退款退货不能为空")
    private Integer applyType;

    /**
     * 处理状态:1为待审核,2为同意,3为不同意
     */
    @NotNull(message="处理状态:1为待审核,2为同意,3为不同意不能为空")
    private Integer refundSts;

    /**
     * 处理退款状态: 0:退款处理中 1:退款成功 -1:退款失败
     */
    @NotNull(message="处理退款状态: 0:退款处理中 1:退款成功 -1:退款失败不能为空")
    private Integer returnMoneySts;

    /**
     * 申请时间
     */
    @NotNull(message="申请时间不能为空")
    private LocalDateTime applyTime;

    /**
     * 卖家处理时间
     */
    private LocalDateTime handelTime;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 文件凭证json
     */
    @Size(max= 150, message="长度不能超过150")
    private String photoFiles;

    /**
     * 申请原因
     */
    @Size(max= 300, message="长度不能超过300")
    private String buyerMsg;

    /**
     * 卖家备注
     */
    @Size(max= 300, message="长度不能超过300")
    private String sellerMsg;

    /**
     * 物流公司名称
     */
    @Size(max= 50, message="长度不能超过50")
    private String expressName;

    /**
     * 物流单号
     */
    @Size(max= 50, message="长度不能超过50")
    private String expressNo;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 收货备注
     */
    @Size(max= 300, message="长度不能超过300")
    private String receiveMessage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
