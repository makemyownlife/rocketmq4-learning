package com.courage.rocketmq4.server.rocketmq;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by zhangyong on 2023/7/5.
 */
@Component
public class TransactionListenerImpl implements TransactionListener {

    private static Logger logger = LoggerFactory.getLogger(TransactionListenerImpl.class);

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        String transactionId = msg.getTransactionId();
        logger.info("开始执行本地事务，事务编号：" + transactionId);
        return null;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String transactionId = msg.getTransactionId();
        return null;
    }

}
