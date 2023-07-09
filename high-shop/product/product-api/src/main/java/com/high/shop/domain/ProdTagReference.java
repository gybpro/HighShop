package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
*
* @TableName prod_tag_reference
*/
@TableName(value ="prod_tag_reference")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdTagReference implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long referenceId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 商品id
     */
    private Long prodId;

    /**
     * 状态(1:正常,0:删除)
     */
    @TableLogic
    private Integer status;

    /**
     * 创建时间
     */
    @NotNull(message="创建时间不能为空")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
