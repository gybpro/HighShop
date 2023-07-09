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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
*
* @TableName prod_prop_value
*/
@TableName(value ="prod_prop_value")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ProdPropValue implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long valueId;

    /**
     * 属性值名称
     */
    @Size(max= 20, message="长度不能超过20")
    private String propValue;

    /**
     * 属性ID
     */
    private Long propId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
