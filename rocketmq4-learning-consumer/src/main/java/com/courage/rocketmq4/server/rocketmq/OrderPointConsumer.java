package com.courage.rocketmq4.server.rocketmq;

import com.alibaba.fastjson.JSON;
import com.courage.rocketmq4.common.sharding.SnowFlakeIdGenerator;
import com.courage.rocketmq4.domain.mapper.PointsMapper;
import com.courage.rocketmq4.domain.po.OrderPO;
import com.courage.rocketmq4.domain.po.PointsPO;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 订单消费者 用于添加积分
 * Created by zhangyong on 2023/7/8.
 */
@Component
public class OrderPointConsumer implements MessageListenerConcurrently {

    private final Logger logger = LoggerFactory.getLogger(OrderPointConsumer.class);

    @Autowired
    private PointsMapper pointsMapper;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            for (MessageExt messageExt : msgs) {
                String orderJSON = new String(messageExt.getBody(), "UTF-8");
                logger.info("orderJSON:" + orderJSON);
                OrderPO orderPO = JSON.parseObject(orderJSON, OrderPO.class);
                // 首先查询是否处理完成
                PointsPO pointsPO = pointsMapper.getById(orderPO.getId());
                if (pointsPO == null) {
                    Long id = SnowFlakeIdGenerator.getUniqueId(1023, 0);
                    pointsPO = new PointsPO();
                    pointsPO.setId(id);
                    pointsPO.setOrderId(orderPO.getId());
                    pointsPO.setUserId(orderPO.getUserId());
                    // 添加积分数 30
                    pointsPO.setPoints(30);
                    pointsPO.setCreateTime(new Date());
                    pointsPO.setRemarks("添加积分数 30");
                    pointsMapper.insert(pointsPO);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("consumeMessage error: ", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

}
