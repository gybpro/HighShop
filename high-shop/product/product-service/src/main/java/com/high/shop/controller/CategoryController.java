package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.high.shop.base.BaseProductController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.Category;
import com.high.shop.service.CategoryService;
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
@RequestMapping("/prod/category")
public class CategoryController extends BaseProductController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/table")
    public ResponseEntity<List<Category>> table() {
        return ok(
                categoryService.list()
        );
    }

    @GetMapping("/listCategory")
    public ResponseEntity<List<Category>> list() {
        return ok(
                categoryService.list(
                        new LambdaQueryWrapper<Category>()
                                .eq(Category::getParentId, 0)
                )
        );
    }

    @PostMapping
    public ResponseEntity<Boolean> saveCategory(@RequestBody Category category) {
        return ok(
                categoryService.save(
                        category.setShopId(CommonConstant.DEFAULT_SHOP)
                                .setGrade(category.getParentId().equals(CommonConstant.DEFAULT_COUNT) ? 1 : 2)
                                .setRecTime(LocalDateTime.now())
                                .setUpdateTime(LocalDateTime.now())
                )
        );
    }
}
