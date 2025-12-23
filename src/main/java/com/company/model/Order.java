package com.company.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 */
public class Order {
    private Integer orderId;
    private Integer customerId;
    private String customerName; // 用于显示客户名称
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private String orderStatus; // 待审核、已确认、发货中、已完成、已取消
    private String shippingAddress;
    private List<OrderItem> orderItems;

    // 订单状态常量
    public static final String STATUS_PENDING = "待审核";
    public static final String STATUS_CONFIRMED = "已确认";
    public static final String STATUS_SHIPPING = "发货中";
    public static final String STATUS_COMPLETED = "已完成";
    public static final String STATUS_CANCELLED = "已取消";

    public Order() {
        this.orderItems = new ArrayList<>();
    }

    public Order(Integer orderId, Integer customerId, Timestamp orderDate,
            BigDecimal totalAmount, String orderStatus, String shippingAddress) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.shippingAddress = shippingAddress;
        this.orderItems = new ArrayList<>();
    }

    /**
     * 添加订单明细
     */
    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
    }

    /**
     * 计算订单总金额
     */
    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

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

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
