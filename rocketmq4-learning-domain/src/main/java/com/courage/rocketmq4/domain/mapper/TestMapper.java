package com.courage.rocketmq4.domain.mapper;

import com.courage.rocketmq4.domain.po.TestPo;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMapper {

    TestPo getTestById(Long id);

}
