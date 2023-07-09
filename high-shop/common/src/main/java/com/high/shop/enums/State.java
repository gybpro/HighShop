package com.high.shop.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum State {
    // 状态枚举
    SUCCESS(20000, "请求成功"),
    FAIL(20001, "请求失败"),
    SAVE_ERROR(20002, "新增失败"),
    DELETE_ERROR(20003, "删除失败"),
    UPDATE_ERROR(20004, "更新失败"),
    QUERY_ERROR(20005, "查询失败");

    /**
     * 公司自定义的状态码
     */
    private Integer code;

    /**
     * 状态信息
     */
    private String msg;
}
