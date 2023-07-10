package com.courage.rocketmq4.server.rocketmq;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单消费者 用于添加积分
 * Created by zhangyong on 2023/7/8.
 */
@Component
public class OrderPointConsumer implements MessageListenerConcurrently {

    private Logger logger = LoggerFactory.getLogger(OrderPointConsumer.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        return null;
    }

}
