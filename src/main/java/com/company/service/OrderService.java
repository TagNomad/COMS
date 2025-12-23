package com.company.service;

import com.company.dao.OrderDao;
import com.company.dao.ProductDao;
import com.company.dao.CustomerDao;
import com.company.model.Order;
import com.company.model.OrderItem;
import com.company.model.Customer;
import com.company.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 订单业务逻辑类
 */
public class OrderService {
    private OrderDao orderDao = new OrderDao();
    private ProductDao productDao = new ProductDao();
    private CustomerDao customerDao = new CustomerDao();

    public List<Order> getAllOrders() {
        return orderDao.findAll();
    }

    public Order getOrderById(int id) {
        return orderDao.findById(id);
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        return orderDao.findByCustomerId(customerId);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderDao.findByStatus(status);
    }

    /**
     * 创建订单（含VIP价格计算）
     */
    public boolean createOrder(Order order) {
        Customer customer = customerDao.findById(order.getCustomerId());
        if (customer == null) {
            return false;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {
            Product product = productDao.findById(item.getProductId());
            if (product == null) {
                return false;
            }

            // 应用VIP价格
            BigDecimal unitPrice = customer.calculateVipPrice(product.getPrice());
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(unitPrice.multiply(new BigDecimal(item.getQuantity())));
            totalAmount = totalAmount.add(item.getTotalPrice());

            // 减少库存
            productDao.updateStock(product.getProductId(), -item.getQuantity());
        }

        order.setTotalAmount(totalAmount);
        order.setOrderStatus(Order.STATUS_PENDING);

        return orderDao.insert(order);
    }

    public boolean updateOrderStatus(int orderId, String status) {
        return orderDao.updateStatus(orderId, status);
    }

    public boolean deleteOrder(int id) {
        Order order = orderDao.findById(id);
        if (order != null && order.getOrderStatus().equals(Order.STATUS_PENDING)) {
            // 恢复库存
            for (OrderItem item : order.getOrderItems()) {
                productDao.updateStock(item.getProductId(), item.getQuantity());
            }
            return orderDao.delete(id);
        }
        return false;
    }

    public BigDecimal getTotalSales(String startDate, String endDate) {
        return orderDao.getTotalSales(startDate, endDate);
    }

    public int getOrderCount(String startDate, String endDate) {
        return orderDao.getOrderCount(startDate, endDate);
    }
}
