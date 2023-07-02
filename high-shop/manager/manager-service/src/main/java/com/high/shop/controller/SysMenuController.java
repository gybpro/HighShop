package com.high.shop.controller;

import com.alibaba.fastjson.JSON;
import com.high.shop.base.BaseManagerController;
import com.high.shop.constant.ManagerConstant;
import com.high.shop.domain.SysMenu;
import com.high.shop.domain.vo.MenuAndAuth;
import com.high.shop.enums.State;
import com.high.shop.service.SysMenuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseManagerController {
    private final SysMenuService sysMenuService;

    private final StringRedisTemplate redisTemplate;

    public SysMenuController(SysMenuService sysMenuService, StringRedisTemplate redisTemplate) {
        this.sysMenuService = sysMenuService;
        this.redisTemplate = redisTemplate;
    }

    /*
        1.获取用户权限列表数据
            sys_menu表中type=2代表页面按钮，点击发送请求
        2.获取用户菜单列表数据
            sys_menu表中type=0和type=1分别代表目录和菜单
     */
    @GetMapping("/nav")
    public ResponseEntity<MenuAndAuth> nav() {
        // 通过Security提供的上下文对象，来获取用户的名称(userId)和权限列表数据
        // 获取用户id
        Long userId = getAuthenticationUserIdOfLong();

        // 查询Redis缓存
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String menuAndAuthStr = ops.get(ManagerConstant.MENU_AUTH_PREFIX + userId);

        // 判断是否有数据
        if (StringUtils.isEmpty(menuAndAuthStr)) {
            // 没有则查询数据库
            // 获取用户权限列表
            List<String> authorities = getAuthenticationUserPerms();

            // 根据用户id获取用户菜单列表
            List<SysMenu> menuList = sysMenuService.selectMenuListByUserId(userId);
            // 整理菜单
            menuList = loadMenu(menuList);

            MenuAndAuth menuAndAuth = MenuAndAuth.builder()
                    .authorities(authorities)
                    .menuList(menuList)
                    .build();

            ops.set(ManagerConstant.MENU_AUTH_PREFIX + userId,
                    JSON.toJSONString(menuAndAuth), 7, TimeUnit.DAYS);

            // 返回权限列表和菜单列表
            return ok(menuAndAuth);
        }
        // 有缓存数据，直接返回
        return ok(JSON.parseObject(menuAndAuthStr, MenuAndAuth.class));
    }

    // 查询菜单列表数据
    @GetMapping("/table")
    public ResponseEntity<List<SysMenu>> table() {
        return ok(sysMenuService.list());
    }

    // 根据类型查询菜单列表
    @GetMapping("/list")
    public ResponseEntity<List<SysMenu>> list() {
        return ok(sysMenuService.listByType());
    }

    // 新增菜单
    @PostMapping
    @Transactional
    public ResponseEntity<Boolean> saveMenu(@RequestBody SysMenu sysMenu) {
        // 如果是目录，则父菜单为0
        if (sysMenu.getType() == 0) sysMenu.setParentId(0L);

        boolean flag = sysMenuService.save(sysMenu);

        checked(flag, State.SAVE_ERROR);

        // 清除redis的缓存数据
        redisTemplate.delete(ManagerConstant.MENU_AUTH_PREFIX + getAuthenticationUserId());

        return ok(true);
    }

    // 整理菜单
    private List<SysMenu> loadMenu(List<SysMenu> menuList) {
        // 从顶级开始整理
        return loadMenu(menuList, 0L);
    }

    // 整理子菜单
    private List<SysMenu> loadMenu(List<SysMenu> menuList, Long parentId) {
        if (menuList.isEmpty()) {
            return null;
        }

        // 找出子菜单
        List<SysMenu> subMenuList = menuList.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .collect(Collectors.toList());

        // 遍历子菜单，尝试查询更下级菜单
        subMenuList.forEach(menu ->
            menu.setList(loadMenu(menuList, menu.getMenuId()))
        );

        return subMenuList;
    }
}
