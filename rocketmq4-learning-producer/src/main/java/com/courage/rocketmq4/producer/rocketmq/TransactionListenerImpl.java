package com.courage.rocketmq4.producer.rocketmq;

import com.alibaba.fastjson.JSON;
import com.courage.rocketmq4.domain.po.OrderPO;
import com.courage.rocketmq4.domain.po.TransactionLogPO;
import com.courage.rocketmq4.service.OrderService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 事务监听器
 * Created by zhangyong on 2023/7/5.
 */
@Component
public class TransactionListenerImpl implements TransactionListener {

    private static Logger logger = LoggerFactory.getLogger(TransactionListenerImpl.class);

    @Autowired
    private OrderService orderService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String transactionId = msg.getTransactionId();
        logger.info("开始执行本地事务，事务编号：" + transactionId);
        try {
            String orderPOJSON = new String(msg.getBody(), "UTF-8");
            OrderPO orderPO = JSON.parseObject(orderPOJSON, OrderPO.class);
            orderService.updateOrder(orderPO.getId(), transactionId);
            logger.info("结束执行本地事务，事务编号：" + transactionId);
            return LocalTransactionState.COMMIT_MESSAGE;
        } catch (Exception e) {
            logger.info("update order error: ", e);
            logger.info("结束执行本地事务，事务编号：" + transactionId);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String transactionId = msg.getTransactionId();
        LocalTransactionState localTransactionState = LocalTransactionState.UNKNOW;
        try {
            logger.info("检测本地事务，事务编号：" + transactionId);
            TransactionLogPO transactionLogPO = orderService.getLogById(transactionId);
            if (transactionLogPO != null) {
                localTransactionState = LocalTransactionState.COMMIT_MESSAGE;
            } else {
                localTransactionState = LocalTransactionState.UNKNOW;
            }
        } catch (Exception e) {
            logger.error("checkLocalTransaction error:", e);
            localTransactionState = LocalTransactionState.UNKNOW;
        }
        logger.info("检测本地事务，事务编号：" + transactionId + " 事务状态：" + localTransactionState);
        return localTransactionState;
    }

}
