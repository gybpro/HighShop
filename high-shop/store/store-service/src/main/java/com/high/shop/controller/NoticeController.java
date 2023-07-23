package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseStoreController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.Notice;
import com.high.shop.service.NoticeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/shop/notice")
public class NoticeController extends BaseStoreController {

    @Resource
    private NoticeService noticeService;

    @GetMapping("/page")
    public ResponseEntity<Page<Notice>> page(Page<Notice> page, Notice notice) {
        return ok(
                noticeService.page(
                        page,
                        new LambdaQueryWrapper<Notice>()
                                .eq(ObjectUtils.isNotEmpty(notice.getIsTop()),  Notice::getIsTop, notice.getIsTop())
                                .eq(ObjectUtils.isNotEmpty(notice.getStatus()),  Notice::getStatus, notice.getStatus())
                                .like(StringUtils.isNotBlank(notice.getTitle()), Notice::getTitle, notice.getTitle())
                )
        );
    }

    @PostMapping
    public ResponseEntity<Boolean> saveNotice(@RequestBody Notice notice) {
        return ok(
                noticeService.save(
                        notice.setShopId(CommonConstant.DEFAULT_SHOP)
                                .setPublishTime(LocalDateTime.now())
                                .setUpdateTime(LocalDateTime.now())
                )
        );
    }

}
