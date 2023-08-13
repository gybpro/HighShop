package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseMemberController;
import com.high.shop.domain.Prod;
import com.high.shop.domain.UserCollection;
import com.high.shop.feign.MemberProductFeign;
import com.high.shop.service.UserCollectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    private final MemberProductFeign memberProductFeign;

    public UserCollectionController(UserCollectionService userCollectionService, MemberProductFeign memberProductFeign) {
        this.userCollectionService = userCollectionService;
        this.memberProductFeign = memberProductFeign;
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

        List<Prod> prodList = memberProductFeign.getListByIds(prodIds);

        Page<Prod> prodPage = new Page<>(collectionPage.getCurrent(), collectionPage.getSize(),
                collectionPage.getTotal());

        return ok(prodPage.setRecords(prodList));
    }

    @GetMapping("/isCollection")
    public ResponseEntity<Boolean> isCollection(@RequestParam Long prodId) {
        return ok(
                userCollectionService.count(
                        new LambdaQueryWrapper<UserCollection>()
                                .eq(UserCollection::getUserId, getAuthenticationUserId())
                                .eq(UserCollection::getProdId, prodId)
                ) > 0
        );
    }

    @PostMapping("/addOrCancel")
    public ResponseEntity<Boolean> addOrCancel(@NotNull Long prodId) {
        // 根据用户id和商品id查询用户是否收藏该商品
        UserCollection userCollection = userCollectionService.getOne(
                new LambdaQueryWrapper<UserCollection>()
                        .eq(UserCollection::getUserId, getAuthenticationUserId())
                        .eq(UserCollection::getProdId, prodId)
        );

        boolean flag;

        if (ObjectUtils.isEmpty(userCollection)) {
            // 如果没收藏，则新增收藏记录
            flag = userCollectionService.save(
                    new UserCollection()
                            .setUserId(getAuthenticationUserId())
                            .setCreateTime(LocalDateTime.now())
                            .setProdId(prodId)
            );
        } else {
            // 如果收藏了，则删除收藏记录
            flag = userCollectionService.remove(
                    new LambdaQueryWrapper<UserCollection>()
                            .eq(UserCollection::getId, userCollection.getId())
            );
        }

        return ok(flag);
    }

}
