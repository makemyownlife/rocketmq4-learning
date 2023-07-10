package com.courage.rocketmq4.domain.po;

import java.util.Date;

/**
 * Created by zhangyong on 2023/7/10.
 */
public class TransactionLogPO {

    private String id;

    private String bizId;

    private Integer bizType;

    private Date createTime;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getBizType() {
        return bizType;
    }

    public void setBizType(Integer bizType) {
        this.bizType = bizType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
