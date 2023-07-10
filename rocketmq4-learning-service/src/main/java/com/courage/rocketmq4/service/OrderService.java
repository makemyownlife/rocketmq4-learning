package com.courage.rocketmq4.service;

import com.courage.rocketmq4.domain.mapper.OrderMapper;
import com.courage.rocketmq4.domain.po.OrderPO;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    public void createOrder(OrderPO orderPO, String transactionId) {
        // 插入到订单表
        orderMapper.insert(orderPO);
        // 插入到事务日志表
        
    }

}
