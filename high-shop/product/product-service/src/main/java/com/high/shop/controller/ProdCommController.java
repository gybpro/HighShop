package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseProductController;
import com.high.shop.domain.Prod;
import com.high.shop.domain.ProdComm;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
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
@RequestMapping("/prod/prodComm")
public class ProdCommController extends BaseProductController {
    @GetMapping("/page")
    public ResponseEntity<Page<ProdComm>> page(Page<ProdComm> page,
                                               String prodName,
                                               Integer status) {
        List<Long> prodIds = null;

        // 判断是否有传递商品名称
        if (StringUtils.hasText(prodName)) {
            List<Prod> prodList = prodService.list(
                    new LambdaQueryWrapper<Prod>()
                            .like(StringUtils.hasText(prodName), Prod::getProdName, prodName)
            );

            // 获取商品id
            prodIds = prodList.stream().map(Prod::getProdId).collect(Collectors.toList());
        }

        page = prodCommService.page(
                page,
                new LambdaQueryWrapper<ProdComm>()
                        .eq(!ObjectUtils.isEmpty(status), ProdComm::getStatus, status)
                        .in(!CollectionUtils.isEmpty(prodIds), ProdComm::getProdId, prodIds)
        );

        page.getRecords().forEach(
                prodComm -> prodComm.setProdName(prodService.getById(prodComm.getProdId()).getProdName())
        );

        return ok(page);
    }
}
