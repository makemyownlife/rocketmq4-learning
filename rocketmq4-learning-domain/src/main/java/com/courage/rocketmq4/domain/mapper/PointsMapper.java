package com.courage.rocketmq4.domain.mapper;

import com.courage.rocketmq4.domain.po.PointsPO;
import org.springframework.stereotype.Repository;

/**
 * Created by zhangyong on 2023/7/9.
 */
@Repository
public interface PointsMapper {

    void insert(PointsPO pointsPO);

    PointsPO getById(Long id);

    PointsPO getByOrderId(Long id);

    void update(PointsPO order);

    void delete(Long id);

}
