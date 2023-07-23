package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.*;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
*
* @TableName notice
*/
@TableName(value ="notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class Notice implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 公告标题
     */
    @Size(max= 36, message="长度不能超过36")
    private String title;

    /**
     * 公告内容
     */
    @Size(max= -1, message="长度不能超过-1")
    private String content;

    /**
     * 状态(1:公布 0:撤回)
     */
    @TableLogic
    private Integer status;

    /**
     * 是否置顶
     */
    private Integer isTop;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
