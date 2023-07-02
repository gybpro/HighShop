package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;

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
* 系统用户
* @TableName sys_user
*/
@TableName(value ="sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysUser implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message="主键不能为空")
    private Long userId;

    /**
     * 用户名
     */
    @NotBlank(message="用户名不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String username;

    /**
     * 密码
     */
    @Size(max= 100, message="长度不能超过100")
    private String password;

    /**
     * 邮箱
     */
    @Size(max= 100, message="长度不能超过100")
    private String email;

    /**
     * 手机号
     */
    @Size(max= 100, message="长度不能超过100")
    private String mobile;

    /**
     * 状态  0：禁用   1：正常
     */
    @TableLogic
    private Integer status;

    /**
     * 创建者ID
     */
    private Long createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户所在的商城Id
     */
    private Long shopId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 关联角色Id列表
     */
    @TableField(exist = false)
    private List<Long> roleIdList;

}
