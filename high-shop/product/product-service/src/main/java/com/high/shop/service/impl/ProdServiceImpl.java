package com.high.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.high.shop.constant.QueueConstant;
import com.high.shop.domain.Prod;
import com.high.shop.domain.Sku;
import com.high.shop.entity.ChangeStock;
import com.high.shop.entity.ProdChange;
import com.high.shop.entity.SkuChange;
import com.high.shop.service.ProdService;
import com.high.shop.mapper.ProdMapper;
import com.high.shop.service.SkuService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author high
* @description 针对表【prod(商品)】的数据库操作Service实现
* @createDate 2023-07-02 16:12:42
*/
@Service
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod>
    implements ProdService{

    private final SkuService skuService;

    private final RabbitTemplate rabbitTemplate;

    public ProdServiceImpl(SkuService skuService, RabbitTemplate rabbitTemplate) {
        this.skuService = skuService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void updateProdAndSkuStock(ChangeStock changeStock) {
        List<ProdChange> prodChangeList = changeStock.getProdChangeList();
        List<SkuChange> skuChangeList = changeStock.getSkuChangeList();

        boolean flag;

        // 一次性获取所有购买信息，在本地修改后一次性发送给后台，减少网络传输次数
        // 因为是单用户的购买信息，内存占用较小，可以一次发送，当信息量较大时不可一次性传输，会导致OOM
        // 获取所有商品id
        List<Long> prodIds = prodChangeList.stream().map(ProdChange::getProdId).collect(Collectors.toList());
        // 查询所有商品信息
        List<Prod> prodList = listByIds(prodIds);
        // 遍历商品信息，修改库存
        prodList.forEach(prod -> {
            // 遍历prodChangeList，获取扣减的商品数量
            Integer count = prodChangeList.stream().filter(
                    prodChange -> prodChange.getProdId().equals(prod.getProdId())
            ).collect(Collectors.toList()).get(0).getCount();

            Integer totalStocks = prod.getTotalStocks();

            if (totalStocks < count) {
                throw new RuntimeException("库存不足");
            }

            prod.setTotalStocks(totalStocks - count)
                    .setSoldNum(prod.getSoldNum() + count)
                    .setUpdateTime(LocalDateTime.now());
        });

        // 更新MySQL商品库存信息
        flag = updateBatchById(prodList);
        if (!flag) {
            throw new RuntimeException("更新商品库存失败");
        }

        // 获取所有skuId
        List<Long> skuIds = skuChangeList.stream().map(SkuChange::getSkuId).collect(Collectors.toList());
        // 获取所有sku信息
        List<Sku> skuList = skuService.listByIds(skuIds);
        // 遍历sku信息，更新库存
        skuList.forEach(sku -> {
            // 遍历skuChangeList，获取扣减的商品数量
            Integer count = skuChangeList.stream().filter(
                    skuChange -> skuChange.getSkuId().equals(sku.getSkuId())
            ).collect(Collectors.toList()).get(0).getCount();

            Integer actualStocks = sku.getActualStocks();

            if (actualStocks < count) {
                throw new RuntimeException("库存不足");
            }

            sku.setActualStocks(actualStocks - count)
                    .setUpdateTime(LocalDateTime.now());
        });

        // 更新sku库存信息
        flag = skuService.updateBatchById(skuList);
        if (!flag) {
            throw new RuntimeException("更新商品库存失败");
        }

        // 发送消息更新ES库存信息
        rabbitTemplate.convertAndSend(
                QueueConstant.ES_CHANGE_EXCHANGE, QueueConstant.ES_CHANGE_ROUTING_KEY, JSON.toJSONString(prodChangeList)
        );
    }

}




