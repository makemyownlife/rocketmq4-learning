package com.courage.rocketmq4.consumer.controller;

import com.alibaba.fastjson.JSON;
import com.courage.rocketmq4.common.result.ResponseEntity;
import com.courage.rocketmq4.domain.po.OrderPO;
import com.courage.rocketmq4.service.OrderService;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhangyong on 2023/7/5.
 */
@Controller
public class OrderController {

    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @Autowired
    private TransactionMQProducer producer;

    @GetMapping("/order/insertPayOrder")
    @ResponseBody
    public ResponseEntity insert(Long userId) {
        try {
            Long orderId = orderService.insertOrder(userId);
            return ResponseEntity.successResult(orderId);
        } catch (Exception e) {
            logger.error("insertPayOrder error:", e);
            return ResponseEntity.failResult("生成订单失败");
        }
    }

    @GetMapping("/order/updatePayOrderSuccess")
    @ResponseBody
    public ResponseEntity updatePayOrderSuccess(Long orderId) {
        try {
            OrderPO orderPO = orderService.getOrderById(orderId);
            // 发送事务消息
            Message message = new Message("order-topic", JSON.toJSONString(orderPO).getBytes());
            TransactionSendResult sendResult = this.producer.sendMessageInTransaction(message, null);;
            if (sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return ResponseEntity.successResult(orderId);
            }
            return ResponseEntity.failResult("修改订单失败");
        } catch (Exception e) {
            logger.error("updatePayOrderSuccess error:", e);
            return ResponseEntity.failResult("修改订单失败");
        }
    }

}
