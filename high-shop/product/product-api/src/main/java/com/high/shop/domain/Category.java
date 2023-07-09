package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
* 产品类目
* @TableName category
*/
@TableName(value ="category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Category implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long categoryId;

    /**
     * 店铺ID
     */
    @NotNull(message="店铺ID不能为空")
    private Long shopId;

    /**
     * 父节点
     */
    @NotNull(message="父节点不能为空")
    private Long parentId;

    /**
     * 产品类目名称
     */
    @NotBlank(message="产品类目名称不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String categoryName;

    /**
     * 类目图标
     */
    @Size(max= 255, message="长度不能超过255")
    private String icon;

    /**
     * 类目的显示图片
     */
    @Size(max= 300, message="长度不能超过300")
    private String pic;

    /**
     * 排序
     */
    @NotNull(message="排序不能为空")
    private Integer seq;

    /**
     * 默认是1，表示正常状态,0为下线状态
     */
    @TableLogic
    @NotNull(message="默认是1，表示正常状态,0为下线状态不能为空")
    private Integer status;

    /**
     * 记录时间
     */
    @NotNull(message="记录时间不能为空")
    private LocalDateTime recTime;

    /**
     * 分类层级
     */
    @NotNull(message="分类层级不能为空")
    private Integer grade;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
