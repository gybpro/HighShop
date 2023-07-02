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
import java.time.LocalDateTime;

/**
* 系统日志
* @TableName sys_log
*/
@TableName(value ="sys_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SysLog implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(message="主键不能为空")
    private Long id;

    /**
     * 用户名
     */
    @Size(max= 50, message="长度不能超过50")
    private String username;

    /**
     * 用户操作
     */
    @Size(max= 50, message="长度不能超过50")
    private String operation;

    /**
     * 请求方法
     */
    @Size(max= 200, message="长度不能超过200")
    private String method;

    /**
     * 请求参数
     */
    @Size(max= 5000, message="长度不能超过5000")
    private String params;

    /**
     * 执行时长(毫秒)
     */
    @NotNull(message="执行时长(毫秒)不能为空")
    private Long time;

    /**
     * IP地址
     */
    @Size(max= 64, message="长度不能超过64")
    private String ip;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 用户id
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
