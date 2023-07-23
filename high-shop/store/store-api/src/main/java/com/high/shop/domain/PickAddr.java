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
* 用户配送地址
* @TableName pick_addr
*/
@TableName(value ="pick_addr")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class PickAddr implements Serializable {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message="主键不能为空")
    private Long addrId;

    /**
     * 自提点名称
     */
    @Size(max= 36, message="长度不能超过36")
    private String addrName;

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
     * 省份ID
     */
    private Long provinceId;

    /**
     * 省份
     */
    @Size(max= 32, message="长度不能超过32")
    private String province;

    /**
     * 城市ID
     */
    private Long cityId;

    /**
     * 城市
     */
    @Size(max= 32, message="长度不能超过32")
    private String city;

    /**
     * 区/县ID
     */
    private Long areaId;

    /**
     * 区/县
     */
    @Size(max= 32, message="长度不能超过32")
    private String area;

    /**
     * 店铺id
     */
    private Long shopId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
