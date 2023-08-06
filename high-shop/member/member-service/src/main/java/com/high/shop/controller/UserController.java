package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.high.shop.base.BaseMemberController;
import com.high.shop.domain.User;
import com.high.shop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/p/user")
public class UserController extends BaseMemberController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/setUserInfo")
    public ResponseEntity<Boolean> setUserInfo(@RequestBody User user) {
        return ok(
                userService.updateById(
                        user.setUserId(getAuthenticationUserId())
                                .setSex("0".equals(user.getSex()) ? "M" : "F")
                                .setModifyTime(LocalDateTime.now())
                                .setUserLasttime(LocalDateTime.now())
                                .setUserLastip(getRequestIp())
                )
        );
    }

    // ============== 远程调用 ==============
    @GetMapping("/getListByIds")
    public List<User> getListByIds(@RequestParam("ids") List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        return userService.list(
                new LambdaQueryWrapper<User>()
                        .select(User::getUserId, User::getNickName, User::getPic)
                        .in(User::getUserId, ids)
        );
    }

}
