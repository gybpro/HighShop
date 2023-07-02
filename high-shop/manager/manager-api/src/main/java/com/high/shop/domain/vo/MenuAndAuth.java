package com.high.shop.domain.vo;

import com.high.shop.domain.SysMenu;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ApiModel("菜单和权限的对象")
public class MenuAndAuth {

    // 当前用户的权限列表
    private List<String> authorities;

    // 当前用户的菜单列表
    private List<SysMenu> menuList;

}
