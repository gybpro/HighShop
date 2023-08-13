package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.high.shop.base.BaseMemberController;
import com.high.shop.domain.UserAddr;
import com.high.shop.service.UserAddrService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/p/address")
public class UserAddrController extends BaseMemberController {

    private final UserAddrService userAddrService;

    public UserAddrController(UserAddrService userAddrService) {
        this.userAddrService = userAddrService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserAddr>> list() {
        return ok(
                userAddrService.list(
                        new LambdaQueryWrapper<UserAddr>()
                                .eq(UserAddr::getUserId, getAuthenticationUserId())
                )
        );
    }

    @PutMapping("/defaultAddr/{addrId}")
    public ResponseEntity<Boolean> defaultAddr(@PathVariable("addrId") Long addrId) {
        // 先根据addrId查询地址信息
        UserAddr userAddr = userAddrService.getById(addrId);

        // 判断该地址是否为默认收获地址，是则直接返回true，否则更新默认地址
        if (userAddr.getCommonAddr() == 1) {
            return ok(true);
        }

        UserAddr oldAddr = userAddrService.getOne(
                new LambdaQueryWrapper<UserAddr>()
                        .eq(UserAddr::getCommonAddr, 1)
                        .eq(UserAddr::getUserId, getAuthenticationUserId())
        );

        boolean flag = userAddrService.updateById(
                oldAddr.setCommonAddr(0).setUpdateTime(LocalDateTime.now())
        );

        if (!flag) {
            throw new RuntimeException("更新失败");
        }

        flag = userAddrService.updateById(
                userAddr.setCommonAddr(1).setUpdateTime(LocalDateTime.now())
        );

        if (!flag) {
            throw new RuntimeException("更新失败");
        }

        return ok(true);
    }

    @PostMapping("/addAddr")
    public ResponseEntity<Boolean> saveUserAddr(@RequestBody UserAddr userAddr) {
        String userId = getAuthenticationUserId();

        // 查询当前地址信息数，没有地址信息则将新增地址设为默认地址
        int count = userAddrService.count(
                new LambdaQueryWrapper<UserAddr>()
                        .eq(UserAddr::getUserId, userId)
        );

        // 新增地址信息
        return ok(
                userAddrService.save(
                        userAddr.setStatus(1)
                                .setUserId(userId)
                                .setCommonAddr(count == 0 ? 1 : 0)
                                .setVersion(0)
                                .setCreateTime(LocalDateTime.now())
                                .setUpdateTime(LocalDateTime.now())
                )
        );
    }

    @GetMapping("/addrInfo/{addrId}")
    public ResponseEntity<UserAddr> addrInfo(@PathVariable("addrId") Long addrId) {
        return ok(
                userAddrService.getById(addrId)
        );
    }

    @PutMapping("/updateAddr")
    public ResponseEntity<Boolean> updateAddr(@RequestBody UserAddr userAddr) {
        return ok(
                userAddrService.updateById(
                        userAddr.setVersion(userAddr.getVersion() + 1)
                                .setUpdateTime(LocalDateTime.now())
                )
        );
    }

    @DeleteMapping("/deleteAddr/{addrId}")
    public ResponseEntity<Boolean> deleteAddr(@PathVariable("addrId") Long addrId) {
        return ok(
                userAddrService.removeById(addrId)
        );
    }

    // ============== 远程调用 ==============
    @GetMapping("/defaultAddr/{userId}")
    public UserAddr defaultAddr(@PathVariable("userId") String userId) {
        return userAddrService.getOne(
                new LambdaQueryWrapper<UserAddr>()
                        .eq(UserAddr::getUserId, userId)
                        .eq(UserAddr::getCommonAddr, 1)
        );
    }

}
