package com.courage.rocketmq4.domain.mapper;

import com.courage.rocketmq4.domain.po.OrderPO;
import org.springframework.stereotype.Repository;

/**
 * Created by zhangyong on 2023/7/9.
 */
@Repository
public interface OrderMapper {

    void insert(OrderPO order);

    OrderPO getById(Long id);

    int update(OrderPO order);

    void delete(Long id);

}
