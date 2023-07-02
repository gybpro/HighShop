package com.high.shop.mapper;

import com.high.shop.domain.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author high
* @description 针对表【sys_menu(菜单管理)】的数据库操作Mapper
* @createDate 2023-04-27 11:15:32
* @Entity com.high.shop.domain.SysMenu
*/
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenuListByUserId(Long userId);

    List<SysMenu> listByType();
}




