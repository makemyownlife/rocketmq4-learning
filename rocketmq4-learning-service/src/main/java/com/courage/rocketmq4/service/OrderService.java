package com.courage.rocketmq4.service;

import com.courage.rocketmq4.common.sharding.SnowFlakeIdGenerator;
import com.courage.rocketmq4.domain.mapper.OrderMapper;
import com.courage.rocketmq4.domain.mapper.TransactionLogMapper;
import com.courage.rocketmq4.domain.po.OrderPO;
import com.courage.rocketmq4.domain.po.TransactionLogPO;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TransactionLogMapper transactionLogMapper;

    public Long insertOrder(Long userId) {
        Long id = SnowFlakeIdGenerator.getUniqueId(userId.intValue(), 0);
        OrderPO orderPO = new OrderPO();
        orderPO.setId(id);
        orderPO.setUserId(userId);
        orderPO.setOrderStatus(0);
        orderPO.setCreateTime(new Date());
        orderPO.setUpdateTime(new Date());
        orderMapper.insert(orderPO);
        return id;
    }

    public OrderPO getOrderById(Long orderId) {
        return orderMapper.getById(orderId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOrder(Long orderId, String transactionId) {
        OrderPO orderPO = orderMapper.getById(orderId);
        // 修改订单状态为已支付
        orderPO.setOrderStatus(1);
        int affectedCount = orderMapper.update(orderPO);
        if (affectedCount <= 0) {
            throw new RuntimeException("updateOrder error");
        }
        // 插入到事务日志表
        TransactionLogPO transactionLogPO = new TransactionLogPO();
        transactionLogPO.setId(transactionId);
        transactionLogPO.setBizType(0);
        transactionLogPO.setBizId(String.valueOf(orderPO.getId()));
        transactionLogPO.setCreateTime(new Date());
        transactionLogMapper.insert(transactionLogPO);
    }

    public TransactionLogPO getLogById(String transactionLogId) {
        return transactionLogMapper.getById(transactionLogId);
    }

}
