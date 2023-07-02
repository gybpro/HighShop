package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseController;
import com.high.shop.domain.SysLog;
import com.high.shop.service.SysLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/sys/log")
public class SysLogController extends BaseController {
    private final SysLogService sysLogService;

    public SysLogController(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @GetMapping("/page")
    public ResponseEntity<Page<SysLog>> page(Page<SysLog> page,
                                             @RequestParam(required = false) String username,
                                             @RequestParam(required = false) String operation) {
        return ok(sysLogService.page(
                page,
                new LambdaQueryWrapper<SysLog>()
                        .like(StringUtils.hasText(username), SysLog::getUsername, username)
                        .like(StringUtils.hasText(operation), SysLog::getOperation, operation))
        );
    }
}
