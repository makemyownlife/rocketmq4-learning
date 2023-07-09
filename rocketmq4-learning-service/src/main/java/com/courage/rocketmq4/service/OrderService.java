package com.courage.rocketmq4.service;

import com.courage.rocketmq4.domain.mapper.OrderMapper;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private TransactionMQProducer producer;

    @Autowired
    private OrderMapper orderMapper;

    // 事务消息发送
    public TransactionSendResult send(String data, String topic) throws MQClientException {
        Message message = new Message(topic, data.getBytes());
        return this.producer.sendMessageInTransaction(message, null);
    }


}
