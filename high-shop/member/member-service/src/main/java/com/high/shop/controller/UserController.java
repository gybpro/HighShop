package com.high.shop.controller;

import com.high.shop.base.BaseMemberController;
import com.high.shop.domain.User;
import com.high.shop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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

}
