package com.courage.rocketmq4.producer.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 样例中 RokcetMQ 配置
 * Created by zhangyong on 2023/7/5.
 */
@Configuration
public class RocketMQConfig {

    // 生产者组
    private final static String ORDER_PRODUCER_GROUP = "orderProducerGroup";

    private final static Integer CORE_POOL_SIZE = 5;

    private final static Integer MAXIMUM_POOL_SIZE = 10;

    private final static Integer KEEP_ALIVE_TIME = 10;

    // 执行事务任务的线程池
    private static ThreadPoolExecutor TRANSACTION_EXECUTOR =
            new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(100));

    @Autowired
    private TransactionListener transactionListener;

    @Bean(value = "transactionMQProducer")
    public TransactionMQProducer createTransactionProducer() throws MQClientException {
        TransactionMQProducer producer = new TransactionMQProducer(ORDER_PRODUCER_GROUP);
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setSendMsgTimeout(Integer.MAX_VALUE);
        producer.setExecutorService(TRANSACTION_EXECUTOR);
        producer.setTransactionListener(transactionListener);
        producer.start();
        return producer;
    }

}
