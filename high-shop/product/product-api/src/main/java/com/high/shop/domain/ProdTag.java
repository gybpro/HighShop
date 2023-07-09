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
* 商品分组表
* @TableName prod_tag
*/
@TableName(value ="prod_tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdTag implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long id;

    /**
     * 分组标题
     */
    @Size(max= 36, message="长度不能超过36")
    private String title;

    /**
     * 店铺Id
     */
    private Long shopId;

    /**
     * 状态(1为正常,0为删除)
     */
    @TableLogic
    private Integer status;

    /**
     * 默认类型(0:商家自定义,1:系统默认)
     */
    private Integer isDefault;

    /**
     * 商品数量
     */
    private Long prodCount;

    /**
     * 列表样式(0:一列一个,1:一列两个,2:一列三个)
     */
    private Integer style;

    /**
     * 排序
     */
    private Integer seq;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
