package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseStoreController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.HotSearch;
import com.high.shop.service.HotSearchService;
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
@RequestMapping("/admin/hotSearch")
public class HotSearchController extends BaseStoreController {

    @Resource
    private HotSearchService hotSearchService;

    @GetMapping("/page")
    public ResponseEntity<Page<HotSearch>> page(Page<HotSearch> page, HotSearch hotSearch) {
        return ok(
                hotSearchService.page(
                        page,
                        new LambdaQueryWrapper<HotSearch>()
                                .eq(ObjectUtils.isNotEmpty(hotSearch.getStatus()),  HotSearch::getStatus,
                                        hotSearch.getStatus())
                                .like(StringUtils.isNotBlank(hotSearch.getTitle()), HotSearch::getTitle,
                                        hotSearch.getTitle())
                                .like(StringUtils.isNotBlank(hotSearch.getContent()), HotSearch::getContent,
                                        hotSearch.getContent())
                )
        );
    }

    @PostMapping
    public ResponseEntity<Boolean> saveHotSearch(@RequestBody HotSearch hotSearch) {
        return ok(
                hotSearchService.save(
                        hotSearch.setShopId(CommonConstant.DEFAULT_SHOP)
                                .setRecDate(LocalDateTime.now())
                )
        );
    }

}
