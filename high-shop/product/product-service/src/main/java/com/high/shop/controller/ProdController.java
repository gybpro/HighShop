package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseProductController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.Prod;
import com.high.shop.domain.ProdTagReference;
import com.high.shop.domain.Sku;
import com.high.shop.enums.State;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/prod/prod")
public class ProdController extends BaseProductController {
    @GetMapping("/page")
    public ResponseEntity<Page<Prod>> page(Page<Prod> page, String prodName, Integer status) {
        return ok(
                prodService.page(
                        page,
                        new LambdaQueryWrapper<Prod>()
                                .eq(!ObjectUtils.isEmpty(status), Prod::getStatus, status)
                                .like(StringUtils.hasText(prodName), Prod::getProdName, prodName)
                )
        );
    }

    @PostMapping
    @Transactional(rollbackFor = RuntimeException.class)
    public ResponseEntity<Boolean> saveProd(@RequestBody Prod prod) {
        // 获取分组列表数据
        List<Long> tagList = prod.getTagList();

        // 获取sku列表数据
        List<Sku> skuList = prod.getSkuList();

        // 新增商品
        boolean flag = prodService.save(
                prod.setShopId(CommonConstant.DEFAULT_SHOP)
                        .setPrice(prod.getPrice())
                        .setOriPrice(prod.getOriPrice())
                        .setSoldNum(0)
                        .setCreateTime(LocalDateTime.now())
                        .setUpdateTime(LocalDateTime.now())
                        .setPutawayTime(prod.getStatus() == 1 ? LocalDateTime.now() : null)
                        .setVersion(1)
        );

        if (!flag) {
            throw new RuntimeException(State.SAVE_ERROR.getMsg());
        }

        // 新增商品分组数据
        List<ProdTagReference> referenceList = tagList.stream().map(
                tagId -> ProdTagReference.builder()
                        .shopId(CommonConstant.DEFAULT_SHOP)
                        .tagId(tagId)
                        .prodId(prod.getProdId())
                        .status(1)
                        .createTime(LocalDateTime.now())
                        .build()
        ).collect(Collectors.toList());

        prodTagReferenceService.saveBatch(referenceList);

        // 新增商品sku数据
        skuList.forEach(
                sku -> sku.setProdId(prod.getProdId())
                        .setActualStocks(prod.getTotalStocks())
                        .setUpdateTime(LocalDateTime.now())
                        .setRecTime(LocalDateTime.now())
                        .setVersion(1)
                        .setIsDelete(0)
        );

        skuService.saveBatch(skuList);

        return ok(true);
    }

    @GetMapping("/prodInfo")
    public ResponseEntity<Prod> prodInfo(@RequestParam Long prodId) {
        Prod prod = prodService.getById(prodId);

        List<Sku> skuList = skuService.list(
                new LambdaQueryWrapper<Sku>()
                        .eq(Sku::getProdId, prodId)
        );

        prod.setSkuList(skuList);

        return ok(prod);
    }

    // ============== 远程调用 ==============
    @GetMapping("/getListByIds")
    public List<Prod> getListByIds(@RequestParam("ids") List<Long> ids) {
        return prodService.listByIds(ids);
    }

}
