package com.high.shop.domain;

import com.baomidou.mybatisplus.annotation.*;
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
* @author high
* 用户配送地址
* @TableName user_addr
*/
@TableName(value ="user_addr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class UserAddr implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long addrId;

    /**
     * 用户ID
     */
    @NotBlank(message="用户ID不能为空")
    @Size(max= 36, message="长度不能超过36")
    private String userId;

    /**
     * 收货人
     */
    @Size(max= 50, message="长度不能超过50")
    private String receiver;

    /**
     * 省ID
     */
    private Long provinceId;

    /**
     * 省
     */
    @Size(max= 100, message="长度不能超过100")
    private String province;

    /**
     * 城市
     */
    @Size(max= 20, message="长度不能超过20")
    private String city;

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 区
     */
    @Size(max= 20, message="长度不能超过20")
    private String area;

    /**
     * 区ID
     */
    private Long areaId;

    /**
     * 邮编
     */
    @Size(max= 15, message="长度不能超过15")
    private String postCode;

    /**
     * 地址
     */
    @Size(max= 1000, message="长度不能超过1000")
    private String addr;

    /**
     * 手机
     */
    @Size(max= 20, message="长度不能超过20")
    private String mobile;

    /**
     * 状态,1正常，0无效
     */
    @TableLogic
    @NotNull(message="状态,1正常，0无效不能为空")
    private Integer status;

    /**
     * 是否默认地址 1是
     */
    @NotNull(message="是否默认地址 1是不能为空")
    private Integer commonAddr;

    /**
     * 建立时间
     */
    @NotNull(message="建立时间不能为空")
    private LocalDateTime createTime;

    /**
     * 版本号
     */
    @NotNull(message="版本号不能为空")
    // @Version
    private Integer version;

    /**
     * 更新时间
     */
    @NotNull(message="更新时间不能为空")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
