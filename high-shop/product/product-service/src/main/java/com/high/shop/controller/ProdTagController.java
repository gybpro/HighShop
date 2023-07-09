package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseProductController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.ProdTag;
import com.high.shop.service.ProdTagService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/prod/prodTag")
public class ProdTagController extends BaseProductController {
    private final ProdTagService prodTagService;

    public ProdTagController(ProdTagService prodTagService) {
        this.prodTagService = prodTagService;
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ProdTag>> page(Page<ProdTag> page,
                                              @RequestParam(required = false) String title,
                                              @RequestParam(required = false) Integer status) {
        return ok(
                prodTagService.page(
                    page,
                    new LambdaQueryWrapper<ProdTag>()
                            .eq(!ObjectUtils.isEmpty(status), ProdTag::getStatus, status)
                            .like(StringUtils.hasText(title), ProdTag::getTitle, title)
                )
        );
    }

    @PostMapping
    public ResponseEntity<Boolean> saveProdTag(@RequestBody ProdTag prodTag) {
        // 如果商家没有分组，则为系统默认分组
        int count = prodTagService.count(
                new LambdaQueryWrapper<ProdTag>()
                        .eq(ProdTag::getIsDefault, 1)
        );

        // 赋值操作
        prodTag.setShopId(CommonConstant.DEFAULT_SHOP)
                .setIsDefault(count > 0 ? 0 : 1)
                .setProdCount(CommonConstant.DEFAULT_COUNT)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());

        return ok(
                prodTagService.save(prodTag)
        );
    }

    @GetMapping("/listTagList")
    public ResponseEntity<List<ProdTag>> listTagList() {
        return ok(
                prodTagService.list(
                        new LambdaQueryWrapper<ProdTag>()
                                .eq(ProdTag::getStatus, 1)
                                .orderByDesc(ProdTag::getSeq)
                )
        );
    }
}
