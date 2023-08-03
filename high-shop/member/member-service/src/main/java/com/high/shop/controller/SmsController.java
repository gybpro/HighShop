package com.high.shop.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.high.shop.base.BaseMemberController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.constant.QueueConstant;
import com.high.shop.domain.User;
import com.high.shop.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/p/sms")
public class SmsController extends BaseMemberController {

    private final StringRedisTemplate redisTemplate;

    private final RabbitTemplate rabbitTemplate;

    private final UserService userService;

    public SmsController(StringRedisTemplate redisTemplate, RabbitTemplate rabbitTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.userService = userService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody Map<String, String> params) {
        String phoneNum = params.get("phoneNum");

        checked(phoneNum);

        // 生成短信验证码
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < CommonConstant.SMS_CODE_SIZE; i++) {
            codeBuilder.append(RandomUtil.randomInt(10));
        }
        String code = codeBuilder.toString();

        System.out.println("短信验证码：" + code);

        // 将短信存入Redis中，5分钟有效
        redisTemplate.opsForValue().set("user:bind:" + phoneNum, code, 5, TimeUnit.MINUTES);

        params.put("code", code);

        // 发送消息到MQ
        rabbitTemplate.convertAndSend(
                QueueConstant.PHONE_SMS_QUEUE,
                JSON.toJSONString(params)
        );

        return ok(code);
    }

    @PostMapping("/savePhone")
    public ResponseEntity<String> savePhone(@RequestBody Map<String, String> params) {
        String phoneNum = params.get("phoneNum");
        String code = params.get("code");

        // 校验短信验证码
        String redisCode = redisTemplate.opsForValue().get("user:bind:" + phoneNum);

        if (!code.equals(redisCode)) {
            return ok("短信验证码错误，请重新输入");
        }

        // 获取用户信息，更新用户手机号码
        User user = userService.getById(getAuthenticationUserId());

        if (ObjectUtils.isEmpty(user)) {
            throw new RuntimeException("用户登录异常");
        }

        boolean flag = userService.updateById(
                user.setUserMobile(phoneNum)
        );

        return ok(flag ? "手机号码更新成功" : "手机号码更新失败");
    }

}
