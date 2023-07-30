package com.high.shop.controller;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.high.shop.base.BaseSearchController;
import com.high.shop.domain.ProdEs;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
public class SearchController extends BaseSearchController {

    private final ElasticsearchRestTemplate esRestTemplate;

    public SearchController(ElasticsearchRestTemplate esRestTemplate) {
        this.esRestTemplate = esRestTemplate;
    }

    @GetMapping("/prod/prodListByTagId")
    public ResponseEntity<Page<ProdEs>> prodListByTagId(@RequestParam Long tagId,
                                                        @RequestParam(defaultValue = "1") Long current,
                                                        @RequestParam(defaultValue = "6") Long size) {
        // 商品数据从ES中查询
        // 构建查询对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 设置查询条件
        queryBuilder.withQuery(
                QueryBuilders.matchQuery("tagList", tagId)
        );

        // 设置分页条件
        // 注意：MyBatisPlus的分页是从1开始的，而ES的分页是从0开始的，所以这里需要减1
        queryBuilder.withPageable(
                PageRequest.of(current.intValue() - 1, size.intValue())
        );

        // 执行查询
        SearchHits<ProdEs> searchHits = esRestTemplate.search(queryBuilder.build(), ProdEs.class);

        // 将ES查询结果封装到Page对象
        List<ProdEs> prodEsList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());

        Page<ProdEs> page = new Page<>(current, size, searchHits.getTotalHits());

        page.setRecords(prodEsList);

        return ok(page);
    }

    @GetMapping("/search/searchProdPage")
    public ResponseEntity<Page<ProdEs>> searchProdPage(@RequestParam String prodName,
                                                       @RequestParam Integer sort,
                                                       @RequestParam(defaultValue = "1") Long current,
                                                       @RequestParam(defaultValue = "6") Long size) {
        // 构建查询对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 设置查询条件
        queryBuilder.withQuery(
                QueryBuilders.matchQuery("prodName", prodName)
        );

        // 设置排序
        switch (sort) {
            case 0: {
                // 0代表综合，按好评率倒叙排序
                queryBuilder.withSort(
                        SortBuilders.fieldSort("positiveRating").order(SortOrder.DESC)
                );
                break;
            }
            case 1: {
                // 1代表销量，按销量倒叙排序
                queryBuilder.withSort(
                        SortBuilders.fieldSort("soldNum").order(SortOrder.DESC)
                );
                break;
            }
            case 2: {
                // 2代表价格，按价格正叙排序
                queryBuilder.withSort(
                        SortBuilders.fieldSort("price").order(SortOrder.ASC)
                );
                break;
            }
            default: {
            }
        }

        // 设置高亮显示查询
        /* 两种写法都可
        queryBuilder.withHighlightFields(
                // 设置高亮字段查询
                new HighlightBuilder.Field("prodName")
                        // 设置高亮字段前缀标签
                        .preTags("<i style='color:pink'>")
                        // 设置高亮字段后缀标签
                        .postTags("</i>")
        ); */
        queryBuilder.withHighlightBuilder(
                new HighlightBuilder()
                        .field("prodName")
                        // 设置高亮字段前缀标签
                        .preTags("<i style='color:pink'>")
                        // 设置高亮字段后缀标签
                        .postTags("</i>")
        );

        // 设置分页条件
        // 注意：MyBatisPlus的分页是从1开始的，而ES的分页是从0开始的，所以这里需要减1
        queryBuilder.withPageable(
                PageRequest.of(current.intValue() - 1, size.intValue())
        );

        // 执行查询
        SearchHits<ProdEs> searchHits = esRestTemplate.search(queryBuilder.build(), ProdEs.class);

        // 封装高亮结果集
        searchHits.forEach(
                searchHit -> {
                    // 根据商品名称高亮
                    List<String> prodNameList = searchHit.getHighlightField("prodName");

                    if (CollectionUtils.isNotEmpty(prodNameList)) {
                        searchHit.getContent().setProdName(prodNameList.get(0));
                    }
                }
        );

        // 将ES查询结果封装到Page对象
        List<ProdEs> prodEsList = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());

        Page<ProdEs> page = new Page<>(current, size, searchHits.getTotalHits());

        page.setRecords(prodEsList);

        return ok(page);
    }

}
