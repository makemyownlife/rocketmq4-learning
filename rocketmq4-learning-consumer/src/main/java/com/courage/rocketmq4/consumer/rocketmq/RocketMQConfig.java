package com.courage.rocketmq4.consumer.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 样例中 RokcetMQ 配置
 * Created by zhangyong on 2023/7/5.
 */
@Configuration
public class RocketMQConfig {

    //消费者组
    private final static String ORDER_POINT_CONSUMER_GROUP = "orderPointConsumerGroup";

    private final static String PRODUCT_CONSUMER_GROUP = "productConsumerGroup";

    // 事务消费者
    @Autowired
    private OrderPointMessageListener orderPointMessageListener;

    // 商品异构到 Elasticsearch
    @Autowired
    private ProductToESMessageListener productToESMessageListener;

    @Bean
    public DefaultMQPushConsumer createTransactionConsumer() throws MQClientException {
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(ORDER_POINT_CONSUMER_GROUP);
        pushConsumer.setNamesrvAddr("127.0.0.1:9876");
        pushConsumer.setConsumeMessageBatchMaxSize(1);
        pushConsumer.subscribe("order-topic", "*");
        pushConsumer.registerMessageListener(orderPointMessageListener);
        pushConsumer.start();
        return pushConsumer;
    }

    @Bean
    public DefaultMQPushConsumer createProductConsumer() throws MQClientException {
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(PRODUCT_CONSUMER_GROUP);
        pushConsumer.setNamesrvAddr("127.0.0.1:9876");
        pushConsumer.setConsumeMessageBatchMaxSize(1);
        pushConsumer.subscribe("product-syn-topic", "*");
        pushConsumer.registerMessageListener(productToESMessageListener);
        pushConsumer.start();
        return pushConsumer;
    }

}
