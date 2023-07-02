package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.*;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* 系统配置信息表
* @TableName sys_config
*/
@TableName(value ="sys_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysConfig implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message="主键不能为空")
    private Long id;

    /**
     * key
     */
    @Size(max= 50, message="长度不能超过50")
    private String paramKey;

    /**
     * value
     */
    @Size(max= 2000, message="长度不能超过2000")
    private String paramValue;

    /**
     * 备注
     */
    @Size(max= 500, message="长度不能超过500")
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
