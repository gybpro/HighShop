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
* 角色与菜单对应关系
* @TableName sys_role_menu
*/
@TableName(value ="sys_role_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysRoleMenu implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message="主键不能为空")
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
