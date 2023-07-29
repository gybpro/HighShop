package com.high.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.constant.CommonConstant;
import com.high.shop.constant.QueueConstant;
import com.high.shop.domain.Prod;
import com.high.shop.domain.ProdComm;
import com.high.shop.domain.ProdEs;
import com.high.shop.domain.ProdTagReference;
import com.high.shop.entity.EsChange;
import com.high.shop.mapper.ProdCommMapper;
import com.high.shop.mapper.ProdMapper;
import com.high.shop.mapper.ProdTagReferenceMapper;
import com.high.shop.pool.ProductThreadPool;
import com.high.shop.repository.ProdEsRepository;
import com.high.shop.service.ImportService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
@Service
public class ImportServiceImpl implements ImportService, CommandLineRunner {

    @Resource
    private ProdMapper prodMapper;

    @Resource
    private ProdTagReferenceMapper prodTagReferenceMapper;

    @Resource
    private ProdCommMapper prodCommMapper;

    @Resource
    private ElasticsearchRestTemplate esRestTemplate;

    @Resource
    private ProdEsRepository prodEsRepository;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${es.import.size}")
    private Integer size;

    private LocalDateTime t1;

    @Override
    public void importAll() {
        // 全量导入，一次导入300-500条数据(1000条以内数据)，如果一次性导入数据太多会导致OOM
        // 查询商品总记录数
        Integer totalCount = prodMapper.selectCount(
                new LambdaQueryWrapper<Prod>()
                        .eq(Prod::getStatus, 1)
        );

        System.out.println("商品总记录数：" + totalCount);

        // 计算总页面数
        int totalPages = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;

        System.out.println("总页面数：" + totalPages);

        // 通过程序计数器，来实现计时操作
        CountDownLatch countDownLatch = new CountDownLatch(totalPages);

        // 遍历总页面数，每次进行导入操作
        for (int i = 0; i < totalPages; i++) {
            // 构建ES数据对象集合
            List<ProdEs> prodEsList = buildProdEsList(i, size, null, null);

            // prodEsList.forEach(System.out::println);

            // 通过线程池执行耗时操作
            // 批量导入到ES中
            // esRestTemplate.save(prodEsList);
            ProductThreadPool.poolExecutor.execute(() -> esRestTemplate.save(prodEsList));

            // 计数器减一
            countDownLatch.countDown();
        }

        try {
            // 将线程阻塞，等待计数器计数完成，再进行赋值操作
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 记录批量导入完成时间
        t1 = LocalDateTime.now();
    }

    @Override
    // cron 定时任务表达式
    // initialDelay 首次执行时间
    // fixedDelay 后续执行间隔时间
    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 60)
    public void updateImport() {
        // 增量导入，定时导入更新的数据
        LocalDateTime t2 = LocalDateTime.now();

        // 查询更新商品记录数
        Integer totalCount = prodMapper.selectCount(
                new LambdaQueryWrapper<Prod>()
                        .eq(Prod::getStatus, 1)
                        // 根据更新时间查询新上架的商品数据
                        .between(Prod::getUpdateTime, t1, t2)
        );

        System.out.println("更新商品记录数：" + totalCount);

        // 计算总页面数
        int totalPages = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;

        System.out.println("总页面数：" + totalPages);

        // 通过程序计数器，来实现计时操作
        CountDownLatch countDownLatch = new CountDownLatch(totalPages);

        // 遍历总页面数，每次进行导入操作
        for (int i = 0; i < totalPages; i++) {
            // 构建ES数据对象集合
            List<ProdEs> prodEsList = buildProdEsList(i, size, t1, t2);

            // prodEsList.forEach(System.out::println);

            // 通过线程池执行耗时操作
            // 批量导入到ES中
            // esRestTemplate.save(prodEsList);
            ProductThreadPool.poolExecutor.execute(() -> esRestTemplate.save(prodEsList));

            // 计数器减一
            countDownLatch.countDown();
        }

        try {
            // 将线程阻塞，等待计数器计数完成，再进行赋值操作
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 将t2赋值给t1，以便下次操作
        t1 = t2;
    }

    /**
     * 快速导入(MQ)
     * 当下订单后，需要发送消息到MQ中，当前方法需要监听消息队列，获取消息，进行消费，
     * 同时扣减es中的库存信息
     * @param message 消息
     * @param channel 管道
     */
    @Override
    @RabbitListener(queues = {QueueConstant.ES_CHANGE_QUEUE})
    public void quickImport(Message message, Channel channel) {
        // 将传递的消息转换成实体类对象
        List<EsChange> esChangeList = JSON.parseArray(new String(message.getBody()), EsChange.class);

        if (CollectionUtils.isEmpty(esChangeList)) { return; }

        // 查询es中的商品信息
        // 获取商品id
        List<Long> prodIdList = esChangeList.stream().map(EsChange::getProdId).collect(Collectors.toList());

        // 根据id查询商品信息
        Iterable<ProdEs> prodEsIterable = prodEsRepository.findAllById(prodIdList);

        // 遍历
        prodEsIterable.forEach(
                prodEs -> {
                    List<EsChange> esChangeObjList = esChangeList.stream().filter(
                            esChange -> esChange.getProdId().equals(prodEs.getProdId())
                    ).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(esChangeObjList)) {
                        // 扣减库存
                        prodEs.setTotalStocks(prodEs.getTotalStocks() + esChangeObjList.get(0).getCount());
                    }
                }
        );

        // 更新es数据
        prodEsRepository.saveAll(prodEsIterable);

        // 消费这条MQ消息
        try {
            channel.basicAck(
                    message.getMessageProperties().getDeliveryTag(), false
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建ES数据对象集合
     * @param pageNo
     * @param pageSize
     * @param t1
     * @param t2
     * @return
     */
    private List<ProdEs> buildProdEsList(int pageNo, Integer pageSize, LocalDateTime t1, LocalDateTime t2) {
        Page<Prod> page = prodMapper.selectPage(
                // MybatisPlus的当前页码从1开始计算
                new Page<Prod>(pageNo + 1, pageSize),
                new LambdaQueryWrapper<Prod>()
                        // 更新从上次导入时间到这次导入时间之间添加的数据
                        .between(t1 != null && t2 != null, Prod::getUpdateTime, t1, t2)
        );

        // 获取查询结果集
        List<Prod> prodList = page.getRecords();

        if (CollectionUtils.isEmpty(prodList)) {
            return new ArrayList<>();
        }

        List<ProdEs> prodEsList = new ArrayList<>();

        // 查询出当前分页商品相关联的分组id数据
        List<Long> prodIdList = prodList.stream().map(Prod::getProdId).collect(Collectors.toList());

        List<ProdTagReference> tagReferenceList = prodTagReferenceMapper.selectList(
                new LambdaQueryWrapper<ProdTagReference>()
                        .in(ProdTagReference::getProdId, prodIdList)
        );

        // 遍历prodList集合，转换为prodEsList集合
        prodList.forEach(
                prod -> {
                    // 获取当前商品关联的分组列表数据
                    List<ProdTagReference> referenceList = tagReferenceList.stream().filter(
                            prodTagReference -> prod.getProdId().equals(prodTagReference.getProdId())
                    ).collect(Collectors.toList());

                    List<Long> tagList = new ArrayList<>();

                    if (!CollectionUtils.isEmpty(referenceList)) {
                        tagList = referenceList.stream().map(ProdTagReference::getTagId).collect(Collectors.toList());
                    }

                    // 查询当前商品好评数
                    Integer praiseNumber = prodCommMapper.selectCount(
                            new LambdaQueryWrapper<ProdComm>()
                                    .eq(ProdComm::getProdId, prod.getProdId())
                                    .eq(ProdComm::getStatus, 1)
                                    .eq(ProdComm::getEvaluate, 0)
                    );

                    // 查询当前商品总评数
                    Integer totalNumber = prodCommMapper.selectCount(
                            new LambdaQueryWrapper<ProdComm>()
                                    .eq(ProdComm::getProdId, prod.getProdId())
                                    .eq(ProdComm::getStatus, 1)
                    );

                    if (ObjectUtils.isEmpty(praiseNumber)) {
                        praiseNumber = 0;
                    }
                    if (ObjectUtils.isEmpty(totalNumber)) {
                        totalNumber = 0;
                    }

                    // 计算好评率
                    // BigDecimal提供加(add, plus)减(subtract)乘(multiply)除(divide)方法
                    // 要记得设置结果精度
                    BigDecimal positiveRating = BigDecimal.ZERO;
                    if (praiseNumber > 0) {
                        positiveRating = new BigDecimal(praiseNumber).divide(new BigDecimal(totalNumber), 2,
                                RoundingMode.HALF_UP).multiply(new BigDecimal(100));
                    }

                    prodEsList.add(
                            ProdEs.builder()
                                    .prodId(prod.getProdId())
                                    .prodName(prod.getProdName())
                                    .price(prod.getPrice())
                                    // 销售量
                                    .soldNum(prod.getSoldNum().longValue())
                                    // 简述
                                    .brief(prod.getBrief())
                                    // 状态
                                    .status(prod.getStatus())
                                    // 总库存
                                    .totalStocks(prod.getTotalStocks().longValue())
                                    // 类别id
                                    .categoryId(prod.getCategoryId())
                                    // 当前商品的关联分组id
                                    .tagList(tagList)
                                    // 好评数量
                                    .praiseNumber(praiseNumber.longValue())
                                    // 好评率
                                    .positiveRating(positiveRating)
                                    // 商品图片
                                    .pic(prod.getPic())
                                    .build()
                    );
                }
        );

        return prodEsList;
    }

    @Override
    public void run(String... args) throws Exception {
        t1 = LocalDateTime.now();

        System.out.println("批量导入开始..." + LocalDateTime.now().format(CommonConstant.DATE_TIME_FORMATTER));
        importAll();
        System.out.println("批量导入结束..." + t1.format(CommonConstant.DATE_TIME_FORMATTER));

        // 测试：发送消息扣减es中的库存信息
        /* List<EsChange> esChangeList = Collections.singletonList(
                EsChange.builder()
                        .prodId(96L)
                        .count(-1)
                        .build()
        );

        rabbitTemplate.convertAndSend(
                QueueConstant.ES_CHANGE_EXCHANGE,
                QueueConstant.ES_CHANGE_ROUTING_KEY,
                JSON.toJSONString(esChangeList)
        ); */
    }

}
