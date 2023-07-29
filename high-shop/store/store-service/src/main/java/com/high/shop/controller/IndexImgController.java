package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseStoreController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.IndexImg;
import com.high.shop.service.IndexImgService;
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
@RequestMapping("/admin/indexImg")
public class IndexImgController extends BaseStoreController {

    private final IndexImgService indexImgService;

    public IndexImgController(IndexImgService indexImgService) {
        this.indexImgService = indexImgService;
    }

    @GetMapping("/page")
    public ResponseEntity<Page<IndexImg>> page(Page<IndexImg> page, IndexImg indexImg) {
        return ok(
                indexImgService.page(
                        page,
                        new LambdaQueryWrapper<IndexImg>()
                                .eq(ObjectUtils.isNotEmpty(indexImg.getStatus()), IndexImg::getStatus,
                                        indexImg.getStatus())
                )
        );
    }

    @PostMapping
    public ResponseEntity<Boolean> saveIndexImg(@RequestBody IndexImg indexImg) {
        return ok(
                indexImgService.save(
                        indexImg.setShopId(CommonConstant.DEFAULT_SHOP)
                                .setUploadTime(LocalDateTime.now())
                )
        );
    }

    @GetMapping("/indexImgs")
    public ResponseEntity<List<IndexImg>> indexImgs() {
        return ok(
                indexImgService.list(
                        new LambdaQueryWrapper<IndexImg>()
                                .select(IndexImg::getImgUrl, IndexImg::getRelation)
                                .eq(IndexImg::getStatus, 1)
                )
        );
    }

}
