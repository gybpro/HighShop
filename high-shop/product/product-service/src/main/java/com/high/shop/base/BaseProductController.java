package com.high.shop.base;

import com.high.shop.service.ProdCommService;
import com.high.shop.service.ProdService;
import com.high.shop.service.ProdTagReferenceService;
import com.high.shop.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class BaseProductController extends BaseController {
    @Resource
    protected ProdCommService prodCommService;

    @Resource
    protected ProdService prodService;

    @Resource
    protected ProdTagReferenceService prodTagReferenceService;

    @Resource
    protected SkuService skuService;
}
