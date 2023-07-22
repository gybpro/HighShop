package com.high.shop.repository;

import com.high.shop.domain.ProdEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface ProdEsRepository extends ElasticsearchRepository<ProdEs, Long> {
}
