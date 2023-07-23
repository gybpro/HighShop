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
* 主页轮播图
* @TableName index_img
*/
@TableName(value ="index_img")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class IndexImg implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long imgId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 图片
     */
    @NotBlank(message="图片不能为空")
    @Size(max= 200, message="长度不能超过200")
    private String imgUrl;

    /**
     * 说明文字,描述
     */
    @Size(max= 200, message="长度不能超过200")
    private String des;

    /**
     * 标题
     */
    @Size(max= 200, message="长度不能超过200")
    private String title;

    /**
     * 链接
     */
    @Size(max= 200, message="长度不能超过200")
    private String link;

    /**
     * 状态
     */
    @TableLogic
    private Integer status;

    /**
     * 顺序
     */
    private Integer seq;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 关联
     */
    private Long relation;

    /**
     * 类型
     */
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
