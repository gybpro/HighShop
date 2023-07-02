package com.high.shop.service;

import com.high.shop.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author high
* @description 针对表【sys_menu(菜单管理)】的数据库操作Service
* @createDate 2023-04-27 11:15:32
*/
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> selectMenuListByUserId(Long userId);

    List<SysMenu> listByType();
}
