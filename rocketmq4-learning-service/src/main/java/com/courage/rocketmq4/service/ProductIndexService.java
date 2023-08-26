package com.courage.rocketmq4.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.courage.rocketmq4.domain.po.ProductPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * 商品索引管理服务
 * Created by zhangyong on 2023/8/26.
 */
@Service
public class ProductIndexService {

    private final static Logger logger = LoggerFactory.getLogger(ProductIndexService.class);

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public void saveProduct(ProductPo product) throws Exception {
        IndexRequest<Object> indexRequest = new IndexRequest.Builder<>()
                .index("t_product")
                .id(String.valueOf(product.getId()))
                .document(product).build();

        IndexResponse response = elasticsearchClient.index(indexRequest);
        logger.info("response:" + response);
    }

    public void deleteProduct(Integer id) throws Exception {
        elasticsearchClient.delete(d -> d.index("t_product").id(String.valueOf(id)));
    }

}
