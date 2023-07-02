package com.high.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.domain.SysMenu;
import com.high.shop.service.SysMenuService;
import com.high.shop.mapper.SysMenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author high
* @description 针对表【sys_menu(菜单管理)】的数据库操作Service实现
* @createDate 2023-04-27 11:15:32
*/
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
    implements SysMenuService{
    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public List<SysMenu> selectMenuListByUserId(Long userId) {
        return sysMenuMapper.selectMenuListByUserId(userId);
    }

    @Override
    public List<SysMenu> listByType() {
        return sysMenuMapper.listByType();
    }
}




