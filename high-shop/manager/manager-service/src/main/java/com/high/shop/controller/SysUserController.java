package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseManagerController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.SysUser;
import com.high.shop.domain.SysUserRole;
import com.high.shop.enums.State;
import com.high.shop.log.annotation.Log;
import com.high.shop.service.SysUserRoleService;
import com.high.shop.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseManagerController {
    private final SysUserService sysUserService;

    private final SysUserRoleService sysUserRoleService;

    public SysUserController(SysUserService sysUserService, SysUserRoleService sysUserRoleService) {
        this.sysUserService = sysUserService;
        this.sysUserRoleService = sysUserRoleService;
    }

    @GetMapping("/info")
    public ResponseEntity<SysUser> info() {
        // 通过Security上下文对象获取userId
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // 通过userId获取用户信息
        SysUser user = sysUserService.getById(userId);

        // 返回用户信息
        return ok(user);
    }

    @GetMapping("/info/{id}")
    @Log("获取用户详情")
    public ResponseEntity<SysUser> info(@PathVariable Long id) {
        // 通过userId获取用户信息
        SysUser sysUser = sysUserService.getById(id);

        // 根据用户id查询关联角色id列表
        List<Long> roleIdList = sysUserRoleService.list(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id)
        ).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        sysUser.setRoleIdList(roleIdList);

        // 返回用户信息
        return ok(sysUser);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<SysUser>> page(Page<SysUser> page,
                                              @RequestParam(required = false) String username) {
        return ok(
                sysUserService.page(
                        page,
                        new LambdaQueryWrapper<SysUser>()
                                .like(StringUtils.hasText(username), SysUser::getUsername, username)
                )
        );
    }

    @PostMapping
    // 添加事务，注意增删改一定要加事务
    @Transactional
    public ResponseEntity<Boolean> saveUser(@RequestBody SysUser sysUser) {
        // 新增用户操作
        boolean flag = sysUserService.save(
                // 密码加密，添加创建信息
                sysUser.setPassword(
                                new BCryptPasswordEncoder().encode(sysUser.getPassword())
                        )
                        .setCreateTime(LocalDateTime.now())
                        .setShopId(CommonConstant.DEFAULT_SHOP)
                        .setCreateUserId(getAuthenticationUserIdOfLong())
        );
        // 检查操作是否成功
        checked(flag, State.SAVE_ERROR);

        // 新增用户角色关联关系操作
        return ok(saveUserRole(sysUser));
    }

    @PutMapping
    // 添加事务，注意增删改一定要加事务
    @Transactional
    public ResponseEntity<Boolean> updateUser(@RequestBody SysUser sysUser) {
        // 修改操作
        boolean flag = sysUserService.updateById(sysUser);

        checked(flag, State.UPDATE_ERROR);

        // 移除角色关联关系
        flag = sysUserRoleService.remove(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, sysUser.getUserId())
        );

        checked(flag, State.DELETE_ERROR);

        // 新增角色关联关系
        return ok(saveUserRole(sysUser));
    }

    @DeleteMapping("/{userIds}")
    @Transactional
    public ResponseEntity<Boolean> deleteUser(@PathVariable List<Long> userIds) {
        // 删除操作
        boolean flag = sysUserService.removeByIds(userIds);

        checked(flag, State.DELETE_ERROR);

        // 删除角色关联关系
        /* 逻辑删除，不删角色关联关系
        flag = sysUserRoleService.remove(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id)
        );

        checked(flag, State.DELETE_ERROR); */

        return ok(true);
    }

    // 新增角色关联关系
    private boolean saveUserRole(@RequestBody SysUser sysUser) {
        List<SysUserRole> userRoleList = sysUser.getRoleIdList().stream()
                .map(roleId -> SysUserRole.builder()
                        .roleId(roleId)
                        .userId(sysUser.getUserId())
                        .build())
                .collect(Collectors.toList());

        checked(sysUserRoleService.saveBatch(userRoleList), State.SAVE_ERROR);

        return true;
    }
}
