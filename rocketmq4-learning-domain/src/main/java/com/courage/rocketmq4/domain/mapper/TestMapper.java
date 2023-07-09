package com.courage.rocketmq4.domain.mapper;

import com.courage.rocketmq4.domain.po.TestPO;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMapper {

    TestPO getTestById(Long id);

}
