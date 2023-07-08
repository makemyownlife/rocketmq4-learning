package com.courage.rocketmq4.server.rocketmq;

import org.apache.rocketmq.client.producer.TransactionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * 样例中 RokcetMQ 配置
 * Created by zhangyong on 2023/7/5.
 */
@Configuration
public class RocketMQConfig {

    @Autowired
    private TransactionListener transactionListener;


}
