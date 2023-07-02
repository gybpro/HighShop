package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.*;
import java.util.List;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
* 角色
* @TableName sys_role
*/
@TableName(value ="sys_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysRole implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message="主键不能为空")
    private Long roleId;

    /**
     * 角色名称
     */
    @Size(max= 100, message="长度不能超过100")
    private String roleName;

    /**
     * 备注
     */
    @Size(max= 100, message="长度不能超过100")
    private String remark;

    /**
     * 创建者ID
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 菜单列表
     */
    @TableField(exist = false)
    private List<Long> menuIdList;

}
