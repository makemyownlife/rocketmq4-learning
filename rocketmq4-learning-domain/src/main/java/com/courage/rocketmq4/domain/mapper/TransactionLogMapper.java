package com.courage.rocketmq4.domain.mapper;

import com.courage.rocketmq4.domain.po.OrderPO;
import com.courage.rocketmq4.domain.po.TransactionLogPO;
import org.springframework.stereotype.Repository;

/**
 * Created by zhangyong on 2023/7/9.
 */
@Repository
public interface TransactionLogMapper {

    void insert(TransactionLogPO transactionLog);

    TransactionLogPO getById(String id);

    void update(TransactionLogPO transactionLog);

    void delete(String id);

}
