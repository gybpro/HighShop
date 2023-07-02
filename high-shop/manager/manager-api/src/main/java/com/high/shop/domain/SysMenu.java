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
* 菜单管理
* @TableName sys_menu
*/
@TableName(value ="sys_menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysMenu implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message="主键不能为空")
    private Long menuId;

    /**
     * 父菜单ID，一级菜单为0
     */
    private Long parentId;

    /**
     * 菜单名称
     */
    @Size(max= 50, message="长度不能超过50")
    private String name;

    /**
     * 菜单URL
     */
    @Size(max= 200, message="长度不能超过200")
    private String url;

    /**
     * 授权(多个用逗号分隔，如：user:list,user:create)
     */
    @Size(max= 500, message="长度不能超过500")
    private String perms;

    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    private Integer type;

    /**
     * 菜单图标
     */
    @Size(max= 50, message="长度不能超过50")
    private String icon;

    /**
     * 排序
     */
    private Integer orderNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 子菜单列表数据
     */
    @TableField(exist = false)
    private List<SysMenu> list;

}
