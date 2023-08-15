package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author high
 * 订单表
 * @TableName order_tbl
 */
@TableName(value ="order_tbl")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Order implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long orderId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 产品名称,多个产品将会以逗号隔开
     */
    @NotBlank(message="产品名称,多个产品将会以逗号隔开不能为空")
    @Size(max= 1000, message="长度不能超过1000")
    private String prodName;

    /**
     * 订购用户ID
     */
    @NotBlank(message="订购用户ID不能为空")
    @Size(max= 36, message="长度不能超过36")
    private String userId;

    /**
     * 订购流水号
     */
    @NotBlank(message="订购流水号不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String orderNumber;

    /**
     * 总值
     */
    @NotNull(message="总值不能为空")
    private BigDecimal total;

    /**
     * 实际总值
     */
    private BigDecimal actualTotal;

    /**
     * 支付方式 0 手动代付 1 微信支付 2 支付宝
     */
    private Integer payType;

    /**
     * 订单备注
     */
    @Size(max= 1024, message="长度不能超过1024")
    private String remarks;

    /**
     * 订单状态 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败
     */
    // @NotNull(message="订单状态 1:待付款 2:待发货 3:待收货 4:待评价 5:成功 6:失败不能为空")
    private Integer orderStatus;

    /**
     * 配送类型
     */
    @Size(max= 10, message="长度不能超过10")
    private String dvyType;

    /**
     * 配送方式ID
     */
    private Long dvyId;

    /**
     * 物流单号
     */
    @Size(max= 100, message="长度不能超过100")
    private String dvyFlowId;

    /**
     * 订单运费
     */
    private BigDecimal freightAmount;

    /**
     * 用户订单地址Id
     */
    private Long addrOrderId;

    /**
     * 订单商品总数
     */
    private Integer productNums;

    /**
     * 订购时间
     */
    @NotNull(message="订购时间不能为空")
    private LocalDateTime createTime;

    /**
     * 订单更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 付款时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime dvyTime;

    /**
     * 完成时间
     */
    private LocalDateTime finallyTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 是否已经支付，1：已经支付过，0：，没有支付过
     */
    private Integer isPayed;

    /**
     * 用户订单删除状态，0：回收站， 1：没有删除， 2：永久删除
     */
    private Integer status;

    /**
     * 0:默认,1:在处理,2:处理完成
     */
    private Integer refundSts;

    /**
     * 优惠总额
     */
    private BigDecimal reduceAmount;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 订单关闭原因 1-超时未支付 2-退款关闭 4-买家取消 15-已通过货到付款交易
     */
    private Integer closeType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private List<OrderItem> orderItemList;

}
