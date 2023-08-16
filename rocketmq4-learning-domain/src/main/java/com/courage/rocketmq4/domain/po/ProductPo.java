package com.courage.rocketmq4.domain.po;

import java.util.Date;

/**
 * Created by zhangyong on 2023/8/15.
 */
public class ProductPo {

    private Integer id;

    private String name;

    private Double price;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public ProductPo() {

    }

    public ProductPo(Integer id, String name, Double price, Integer status, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
