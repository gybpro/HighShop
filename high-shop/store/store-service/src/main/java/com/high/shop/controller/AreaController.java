package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.high.shop.base.BaseStoreController;
import com.high.shop.domain.Area;
import com.high.shop.service.AreaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/admin/area")
public class AreaController extends BaseStoreController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Area>> list() {
        return ok(areaService.list());
    }

    @GetMapping("/listByPid")
    public ResponseEntity<List<Area>> listByPid(@RequestParam Long pid) {
        return ok(areaService.list(
                new LambdaQueryWrapper<Area>()
                        .eq(ObjectUtils.isNotEmpty(pid), Area::getParentId, pid)
        ));
    }

}
