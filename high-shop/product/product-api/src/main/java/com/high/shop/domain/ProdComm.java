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
import java.time.LocalDateTime;

/**
* 商品评论
* @TableName prod_comm
*/
@TableName(value ="prod_comm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdComm implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long prodCommId;

    /**
     * 商品ID
     */
    @NotNull(message="商品ID不能为空")
    private Long prodId;

    /**
     * 订单项ID
     */
    private Long orderItemId;

    /**
     * 评论用户ID
     */
    @Size(max= 36, message="长度不能超过36")
    private String userId;

    /**
     * 评论内容
     */
    @Size(max= 500, message="长度不能超过500")
    private String content;

    /**
     * 掌柜回复
     */
    @Size(max= 500, message="长度不能超过500")
    private String replyContent;

    /**
     * 记录时间
     */
    private LocalDateTime recTime;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 是否回复 0:未回复  1:已回复
     */
    private Integer replySts;

    /**
     * IP来源
     */
    @Size(max= 16, message="长度不能超过16")
    private String postip;

    /**
     * 得分，0-5分
     */
    private Integer score;

    /**
     * 有用的计数
     */
    private Integer usefulCounts;

    /**
     * 晒图的json字符串
     */
    @Size(max= 1000, message="长度不能超过1000")
    private String pics;

    /**
     * 是否匿名(1:是  0:否)
     */
    private Integer isAnonymous;

    /**
     * 是否显示，1:为显示，0:待审核， -1：不通过审核，不显示。 如果需要审核评论，则是0,，否则1
     */
    @TableLogic
    private Integer status;

    /**
     * 评价(0好评 1中评 2差评)
     */
    private Integer evaluate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableField(exist = false)
    private String prodName;

}
