package com.courage.rocketmq4.domain.po;

/**
 * Created by zhangyong on 2023/8/15.
 */
public class ProductPo {

    private Integer id;

    private String name;

    private int price;

    public ProductPo() {

    }

    public ProductPo(Integer id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", price=" + price + '}';
    }

}
