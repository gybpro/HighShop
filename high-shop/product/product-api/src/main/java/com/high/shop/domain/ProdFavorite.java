package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
* 商品收藏表
* @TableName prod_favorite
*/
@TableName(value ="prod_favorite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdFavorite implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long favoriteId;

    /**
     * 商品ID
     */
    @NotNull(message="商品ID不能为空")
    private Long prodId;

    /**
     * 收藏时间
     */
    @NotNull(message="收藏时间不能为空")
    private LocalDateTime recTime;

    /**
     * 用户ID
     */
    @NotBlank(message="用户ID不能为空")
    @Size(max= 36, message="长度不能超过36")
    private String userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
