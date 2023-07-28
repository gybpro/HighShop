package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseMemberController;
import com.high.shop.domain.Prod;
import com.high.shop.domain.UserCollection;
import com.high.shop.feign.ProductServiceFeign;
import com.high.shop.service.UserCollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/p/collection")
public class UserCollectionController extends BaseMemberController {

    private final UserCollectionService userCollectionService;

    private final ProductServiceFeign productServiceFeign;

    public UserCollectionController(UserCollectionService userCollectionService, ProductServiceFeign productServiceFeign) {
        this.userCollectionService = userCollectionService;
        this.productServiceFeign = productServiceFeign;
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> count() {
        return ok(
                userCollectionService.count(
                        new LambdaQueryWrapper<UserCollection>()
                                .eq(UserCollection::getUserId, getAuthenticationUserId())
                )
        );
    }

    @GetMapping("/prods")
    public ResponseEntity<Page<Prod>> prods(Page<UserCollection> page) {
        // 根据分页参数，将用户收藏信息列表查询出来
        Page<UserCollection> collectionPage = userCollectionService.page(
                page,
                new LambdaQueryWrapper<UserCollection>()
                        .eq(UserCollection::getUserId, getAuthenticationUserId())
        );

        if (CollectionUtils.isEmpty(collectionPage.getRecords())) {
            return ok(new Page<>());
        }

        // 根据prodIds来远程调用获取商品列表数据
        List<Long> prodIds = collectionPage.getRecords().stream().map(UserCollection::getProdId).collect(Collectors.toList());

        List<Prod> prodList = productServiceFeign.getListByIds(prodIds);

        Page<Prod> prodPage = new Page<>(collectionPage.getCurrent(), collectionPage.getSize(),
                collectionPage.getTotal());

        return ok(prodPage.setRecords(prodList));
    }

}
