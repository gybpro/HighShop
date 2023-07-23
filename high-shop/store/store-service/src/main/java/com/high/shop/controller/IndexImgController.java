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

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/admin/indexImg")
public class IndexImgController extends BaseStoreController {

    @Resource
    private IndexImgService indexImgService;

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

}
