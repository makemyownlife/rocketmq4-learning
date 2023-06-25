package com.courage.rocketmq4.service;

import com.courage.rocketmq4.domain.mapper.TestMapper;
import com.courage.rocketmq4.domain.po.TestPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Autowired
    private TestMapper TestMapper;

}
