package com.company.model;

import java.math.BigDecimal;

/**
 * 客户价格实体类
 * 用于管理不同客户等级的产品专属价格
 */
public class CustomerPrice {
    private String customerLevel;
    private Integer productId;
    private String productName; // 用于显示产品名称
    private BigDecimal price;

    public CustomerPrice() {
    }

    public CustomerPrice(String customerLevel, Integer productId, BigDecimal price) {
        this.customerLevel = customerLevel;
        this.productId = productId;
        this.price = price;
    }

    // Getters and Setters
    public String getCustomerLevel() {
        return customerLevel;
    }

    public void setCustomerLevel(String customerLevel) {
        this.customerLevel = customerLevel;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
