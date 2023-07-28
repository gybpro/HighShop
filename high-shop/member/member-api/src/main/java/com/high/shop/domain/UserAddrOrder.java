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
* @author high
* 用户订单配送地址
* @TableName user_addr_order
*/
@TableName(value ="user_addr_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class UserAddrOrder implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long addrOrderId;

    /**
     * 地址ID
     */
    @NotNull(message="地址ID不能为空")
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
     * 区域ID
     */
    private Long areaId;

    /**
     * 区
     */
    @Size(max= 20, message="长度不能超过20")
    private String area;

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 城市
     */
    @Size(max= 20, message="长度不能超过20")
    private String city;

    /**
     * 地址
     */
    @Size(max= 1000, message="长度不能超过1000")
    private String addr;

    /**
     * 邮编
     */
    @Size(max= 15, message="长度不能超过15")
    private String postCode;

    /**
     * 手机
     */
    @Size(max= 20, message="长度不能超过20")
    private String mobile;

    /**
     * 建立时间
     */
    @NotNull(message="建立时间不能为空")
    private LocalDateTime createTime;

    /**
     * 版本号
     */
    @NotNull(message="版本号不能为空")
    private Integer version;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
