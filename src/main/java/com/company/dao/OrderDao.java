package com.company.dao;

import com.company.model.Order;
import com.company.model.OrderItem;
import com.company.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单数据访问类
 */
public class OrderDao {

    /**
     * 获取所有订单
     */
    public List<Order> findAll() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name FROM Orders o " +
                "LEFT JOIN Customers c ON o.customer_id = c.customer_id ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapResultSet(rs);
                order.setCustomerName(rs.getString("customer_name"));
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * 根据ID查找订单（包含订单明细）
     */
    public Order findById(int orderId) {
        String sql = "SELECT o.*, c.customer_name FROM Orders o " +
                "LEFT JOIN Customers c ON o.customer_id = c.customer_id WHERE o.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapResultSet(rs);
                    order.setCustomerName(rs.getString("customer_name"));
                    order.setOrderItems(findOrderItems(orderId));
                    return order;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取订单明细
     */
    public List<OrderItem> findOrderItems(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.product_name FROM Order_Items oi " +
                "LEFT JOIN Products p ON oi.product_id = p.product_id WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setTotalPrice(rs.getBigDecimal("total_price"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * 按客户查找订单
     */
    public List<Order> findByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name FROM Orders o " +
                "LEFT JOIN Customers c ON o.customer_id = c.customer_id " +
                "WHERE o.customer_id = ? ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSet(rs);
                    order.setCustomerName(rs.getString("customer_name"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * 按状态查找订单
     */
    public List<Order> findByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name FROM Orders o " +
                "LEFT JOIN Customers c ON o.customer_id = c.customer_id " +
                "WHERE o.order_status = ? ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSet(rs);
                    order.setCustomerName(rs.getString("customer_name"));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    /**
     * 创建订单（含订单明细）
     */
    public boolean insert(Order order) {
        String orderSql = "INSERT INTO Orders (customer_id, total_amount, order_status, shipping_address) VALUES (?, ?, ?, ?)";
        String itemSql = "INSERT INTO Order_Items (order_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 插入订单
            try (PreparedStatement stmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, order.getCustomerId());
                stmt.setBigDecimal(2, order.getTotalAmount());
                stmt.setString(3, order.getOrderStatus() != null ? order.getOrderStatus() : Order.STATUS_PENDING);
                stmt.setString(4, order.getShippingAddress());
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        order.setOrderId(rs.getInt(1));
                    }
                }
            }

            // 插入订单明细
            if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
                try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {
                    for (OrderItem item : order.getOrderItems()) {
                        stmt.setInt(1, order.getOrderId());
                        stmt.setInt(2, item.getProductId());
                        stmt.setInt(3, item.getQuantity());
                        stmt.setBigDecimal(4, item.getUnitPrice());
                        stmt.setBigDecimal(5, item.getTotalPrice());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 更新订单状态
     */
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE Orders SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除订单
     */
    public boolean delete(int orderId) {
        String sql = "DELETE FROM Orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取销售统计
     */
    public BigDecimal getTotalSales(String startDate, String endDate) {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM Orders " +
                "WHERE order_status = '已完成' AND DATE(order_date) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取订单数量统计
     */
    public int getOrderCount(String startDate, String endDate) {
        String sql = "SELECT COUNT(*) FROM Orders WHERE DATE(order_date) BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Order mapResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setShippingAddress(rs.getString("shipping_address"));
        return order;
    }
}
