package com.company.model;

import java.math.BigDecimal;

/**
 * 客户实体类
 * 支持VIP等级价格体系
 */
public class Customer {
    private Integer customerId;
    private String customerName;
    private String contactName;
    private String phone;
    private String address;
    private String email;
    private String customerLevel; // 普通、银牌、金牌、钻石
    private String password;
    private String status;
    private String avatarUrl;

    // VIP折扣率映射
    public static final String LEVEL_NORMAL = "普通";
    public static final String LEVEL_SILVER = "银牌";
    public static final String LEVEL_GOLD = "金牌";
    public static final String LEVEL_DIAMOND = "钻石";

    public Customer() {
    }

    public Customer(Integer customerId, String customerName, String contactName,
            String phone, String address, String email, String customerLevel) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.contactName = contactName;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.customerLevel = customerLevel;
    }

    /**
     * 根据客户等级获取折扣率
     * VIP算法: 普通100%, 银牌95%, 金牌90%, 钻石85%
     */
    public BigDecimal getDiscountRate() {
        if (customerLevel == null) {
            return BigDecimal.ONE;
        }
        switch (customerLevel) {
            case LEVEL_SILVER:
                return new BigDecimal("0.95");
            case LEVEL_GOLD:
                return new BigDecimal("0.90");
            case LEVEL_DIAMOND:
                return new BigDecimal("0.85");
            default:
                return BigDecimal.ONE;
        }
    }

    /**
     * 计算客户专属价格
     */
    public BigDecimal calculateVipPrice(BigDecimal originalPrice) {
        return originalPrice.multiply(getDiscountRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    // Getters and Setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerLevel() {
        return customerLevel;
    }

    public void setCustomerLevel(String customerLevel) {
        this.customerLevel = customerLevel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
