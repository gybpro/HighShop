package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseManagerController;
import com.high.shop.domain.SysRole;
import com.high.shop.domain.SysRoleMenu;
import com.high.shop.enums.State;
import com.high.shop.service.SysRoleMenuService;
import com.high.shop.service.SysRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseManagerController {
    private final SysRoleService sysRoleService;

    private final SysRoleMenuService sysRoleMenuService;

    public SysRoleController(SysRoleService sysRoleService, SysRoleMenuService sysRoleMenuService) {
        this.sysRoleService = sysRoleService;
        this.sysRoleMenuService = sysRoleMenuService;
    }

    // 查询角色列表
    @GetMapping("/list")
    public ResponseEntity<List<SysRole>> list() {
        return ok(sysRoleService.list());
    }

    // 分页查询角色信息
    @GetMapping("/page")
    public ResponseEntity<Page<SysRole>> page(Page<SysRole> page,
                                              @RequestParam(required = false) String roleName) {
        return ok(sysRoleService.page(
                page,
                new LambdaQueryWrapper<SysRole>()
                        .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
        ));
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<SysRole> info(@PathVariable Long id) {
        // 通过userId获取用户信息
        SysRole sysRole = sysRoleService.getById(id);

        // 根据用户id查询关联角色id列表
        List<Long> menuIdList = sysRoleMenuService.list(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id)
        ).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

        sysRole.setMenuIdList(menuIdList);

        // 返回用户信息
        return ok(sysRole);
    }

    // 新增角色信息
    @PostMapping
    @Transactional
    public ResponseEntity<Boolean> saveRole(@RequestBody SysRole sysRole) {
        // 添加创建信息
        sysRole.setCreateTime(LocalDateTime.now())
                .setCreateUserId(getAuthenticationUserIdOfLong());

        // 新增角色信息，并检查是否成功
        checked(sysRoleService.save(sysRole), State.SAVE_ERROR);

        // 新增菜单关联关系
        return ok(saveRoleMenu(sysRole));
    }

    // 更新角色信息
    @PutMapping
    @Transactional
    public ResponseEntity<Boolean> updateRole(@RequestBody SysRole sysRole) {
        // 更新角色信息，并检查是否成功
        checked(sysRoleService.updateById(sysRole), State.UPDATE_ERROR);

        // 移除旧的菜单关联关系
        checked(sysRoleMenuService.remove(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, sysRole.getRoleId())
        ), State.DELETE_ERROR);

        // 新增菜单关联关系
        return ok(saveRoleMenu(sysRole));
    }

    // 删除角色信息
    @DeleteMapping("/{ids}")
    @Transactional
    public ResponseEntity<Boolean> deleteRole(@PathVariable List<Long> ids) {
        checked(sysRoleService.removeByIds(ids), State.DELETE_ERROR);

        System.out.println(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, ids).toString());

        // 移除旧的菜单关联关系
        ids.forEach(id -> checked(sysRoleMenuService.remove(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, id)
        ), State.DELETE_ERROR));

        return ok(true);
    }

    // 新增菜单关联关系
    private boolean saveRoleMenu(SysRole sysRole) {
        List<SysRoleMenu> roleMenuList = sysRole.getMenuIdList().stream()
                .map(menuId -> SysRoleMenu.builder()
                        .menuId(menuId)
                        .roleId(sysRole.getRoleId())
                        .build())
                .collect(Collectors.toList());

        checked(sysRoleMenuService.saveBatch(roleMenuList), State.SAVE_ERROR);

        return true;
    }
}
