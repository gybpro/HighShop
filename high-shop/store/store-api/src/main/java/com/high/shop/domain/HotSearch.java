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
* 热搜
* @TableName hot_search
*/
@TableName(value ="hot_search")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class HotSearch implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long hotSearchId;

    /**
     * 店铺ID 0为全局热搜
     */
    private Long shopId;

    /**
     * 内容
     */
    @NotBlank(message="内容不能为空")
    @Size(max= 255, message="长度不能超过255")
    private String content;

    /**
     * 录入时间
     */
    @NotNull(message="录入时间不能为空")
    private LocalDateTime recDate;

    /**
     * 顺序
     */
    private Integer seq;

    /**
     * 状态 0下线 1上线
     */
    @TableLogic
    @NotNull(message="状态 0下线 1上线不能为空")
    private Integer status;

    /**
     * 热搜标题
     */
    @NotBlank(message="热搜标题不能为空")
    @Size(max= 255, message="长度不能超过255")
    private String title;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
