package com.high.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseProductController;
import com.high.shop.domain.Prod;
import com.high.shop.domain.ProdComm;
import com.high.shop.feign.MemberServiceFeign;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/prod/prodComm")
public class ProdCommController extends BaseProductController {

    private final MemberServiceFeign memberServiceFeign;

    public ProdCommController(MemberServiceFeign memberServiceFeign) {
        this.memberServiceFeign = memberServiceFeign;
    }

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

    @GetMapping("/prodCommPageByProd")
    public ResponseEntity<Page<ProdComm>> prodCommPageByProd(Page<ProdComm> page,
                                                             @RequestParam Long prodId,
                                                             @RequestParam(defaultValue = "-1") Long evaluate) {
        page = prodCommService.page(
                page,
                new LambdaQueryWrapper<ProdComm>()
                        .eq(ProdComm::getStatus, 1)
                        .eq(!ObjectUtils.isEmpty(prodId), ProdComm::getProdId, prodId)
                        .eq(evaluate != -1, ProdComm::getEvaluate, evaluate)
        );

        // 根据当前用户id查询用户头像及昵称信息
        List<ProdComm> prodCommList = page.getRecords();

        List<String> userIds = prodCommList.stream().map(ProdComm::getUserId).collect(Collectors.toList());

        List<Map<String, Object>> mapList = memberServiceFeign.getListByIds(userIds);

        prodCommList.forEach(
                prodComm -> {
                    List<Map<String, Object>> userList = mapList.stream().filter(
                            map -> map.get("userId").equals(prodComm.getUserId())
                    ).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(userList)) {
                        Map<String, Object> user = userList.get(0);

                        String pic = (String) user.get("pic");

                        StringBuilder nickName = new StringBuilder();
                        String subStr = ((String) user.get("nickName")).substring(0, 1);
                        nickName.append(subStr).append("**");

                        prodComm.setNickName(nickName.toString()).setPic(pic);
                    }
                }
        );

        return ok(page);
    }
}
