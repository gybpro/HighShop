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
 * @author high
 * 短信记录表
 * @TableName sms_log
 */
@TableName(value ="sms_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class SmsLog implements Serializable {

    /**
     * 主键
     */
    @TableId
    @NotNull(message="主键不能为空")
    private Long id;

    /**
     * 用户id
     */
    @Size(max= 50, message="长度不能超过50")
    private String userId;

    /**
     * 手机号码
     */
    @NotBlank(message="手机号码不能为空")
    @Size(max= 20, message="长度不能超过20")
    private String userPhone;

    /**
     * 短信内容
     */
    @NotBlank(message="短信内容不能为空")
    @Size(max= 100, message="长度不能超过100")
    private String content;

    /**
     * 手机验证码
     */
    @NotBlank(message="手机验证码不能为空")
    @Size(max= 50, message="长度不能超过50")
    private String mobileCode;

    /**
     * 短信类型  1:注册  2:验证
     */
    @NotNull(message="短信类型  1:注册  2:验证不能为空")
    private Integer type;

    /**
     * 发送时间
     */
    @NotNull(message="发送时间不能为空")
    private LocalDateTime recDate;

    /**
     * 发送短信返回码
     */
    @Size(max= 50, message="长度不能超过50")
    private String responseCode;

    /**
     * 状态  1:有效  0：失效
     */
    @TableLogic
    @NotNull(message="状态  1:有效  0：失效不能为空")
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
