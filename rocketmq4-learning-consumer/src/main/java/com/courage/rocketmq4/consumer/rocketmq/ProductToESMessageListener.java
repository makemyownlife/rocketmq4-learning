package com.courage.rocketmq4.consumer.rocketmq;

import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zhangyong on 2023/8/16.
 */
@Component
public class ProductToESMessageListener implements MessageListenerOrderly {

    private final Logger logger = LoggerFactory.getLogger(OrderPointMessageListener.class);

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        try {
            for (MessageExt messageExt : msgs) {
                String data = new String(messageExt.getBody(), "UTF-8");
                logger.info("data:" + data);
            }
        } catch (Exception e) {
            logger.error("consumeMessage error: ", e);
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }

}
