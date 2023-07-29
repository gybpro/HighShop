package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseStoreController;
import com.high.shop.constant.CommonConstant;
import com.high.shop.domain.PickAddr;
import com.high.shop.service.PickAddrService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/shop/pickAddr")
public class PickAddrController extends BaseStoreController {

    private final PickAddrService pickAddrService;

    public PickAddrController(PickAddrService pickAddrService) {
        this.pickAddrService = pickAddrService;
    }

    @GetMapping("/page")
    public ResponseEntity<Page<PickAddr>> page(Page<PickAddr> page, PickAddr pickAddr) {
        return ok(
                pickAddrService.page(
                        page,
                        new LambdaQueryWrapper<PickAddr>()
                                .like(StringUtils.isNotBlank(pickAddr.getAddrName()), PickAddr::getAddrName,
                                        pickAddr.getAddrName())
                )
        );
    }

    @PostMapping
    public ResponseEntity<Boolean> savePickAddr(@RequestBody PickAddr pickAddr) {
        return ok(pickAddrService.save(
                pickAddr.setShopId(CommonConstant.DEFAULT_SHOP)
        ));
    }

}
