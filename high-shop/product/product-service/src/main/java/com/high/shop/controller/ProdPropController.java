package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseProductController;
import com.high.shop.domain.ProdProp;
import com.high.shop.domain.ProdPropValue;
import com.high.shop.service.ProdPropService;
import com.high.shop.service.ProdPropValueService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/prod/spec")
/*
spec规格模块：
    prod_prop 属性
    prod_prop_value 属性值
 */
public class ProdPropController extends BaseProductController {
    private final ProdPropService prodPropService;

    private final ProdPropValueService prodPropValueService;

    public ProdPropController(ProdPropService prodPropService, ProdPropValueService prodPropValueService) {
        this.prodPropService = prodPropService;
        this.prodPropValueService = prodPropValueService;
    }

    @GetMapping("/page")
    public ResponseEntity<Page<ProdProp>> page(Page<ProdProp> page,
                                               @RequestParam(required = false) String propName) {
        Page<ProdProp> propPage = prodPropService.page(
                page,
                new LambdaQueryWrapper<ProdProp>()
                        .like(StringUtils.isNotBlank(propName), ProdProp::getPropName, propName)
        );

        List<ProdProp> prodPropList = propPage.getRecords();

        if (!CollectionUtils.isEmpty(prodPropList)) {
            // 根据属性id获取属性值列表
            // 两种方式
            // 普通方式：发送多条SQL语句，效率较低，但不占用内存
            prodPropList.forEach(
                    prodProp -> prodProp.setProdPropValues(
                            prodPropValueService.list(
                                    new LambdaQueryWrapper<ProdPropValue>()
                                            .eq(ProdPropValue::getPropId, prodProp.getPropId())
                            )
                    )
            );

            // 全查过滤方式：发送一条SQL语句，效率高，但是占用内存，如果超过50w条数据直接OOM
            List<ProdPropValue> prodPropValueList = prodPropValueService.list();
            prodPropList.forEach(
                    prodProp -> {
                        List<ProdPropValue> valueList = prodPropValueList.stream().filter(prodPropValue -> prodPropValue.getPropId().equals(prodProp.getPropId())).collect(Collectors.toList());

                        if (!CollectionUtils.isEmpty(valueList))
                            prodProp.setProdPropValues(valueList);
                    }
            );
        }

        return ok(propPage);
    }
}
