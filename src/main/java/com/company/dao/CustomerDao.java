package com.company.dao;

import com.company.model.Customer;
import com.company.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户数据访问类
 */
public class CustomerDao {

    /**
     * 获取所有客户
     */
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers ORDER BY customer_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * 根据ID查找客户
     */
    public Customer findById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按名称搜索客户
     */
    public List<Customer> searchByName(String keyword) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers WHERE customer_name LIKE ? ORDER BY customer_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * 按等级查找客户
     */
    public List<Customer> findByLevel(String level) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customers WHERE customer_level = ? ORDER BY customer_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, level);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    /**
     * 新增客户
     */
    public boolean insert(Customer customer) {
        String sql = "INSERT INTO Customers (customer_name, contact_name, phone, address, email, customer_level) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getContactName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6,
                    customer.getCustomerLevel() != null ? customer.getCustomerLevel() : Customer.LEVEL_NORMAL);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新客户
     */
    public boolean update(Customer customer) {
        String sql = "UPDATE Customers SET customer_name = ?, contact_name = ?, phone = ?, " +
                "address = ?, email = ?, customer_level = ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getContactName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getCustomerLevel());
            stmt.setInt(7, customer.getCustomerId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除客户
     */
    public boolean delete(int customerId) {
        String sql = "DELETE FROM Customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查客户是否有订单
     */
    public boolean hasOrders(int customerId) {
        String sql = "SELECT COUNT(*) FROM Orders WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Customer mapResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setCustomerName(rs.getString("customer_name"));
        customer.setContactName(rs.getString("contact_name"));
        customer.setPhone(rs.getString("phone"));
        customer.setAddress(rs.getString("address"));
        customer.setEmail(rs.getString("email"));
        customer.setCustomerLevel(rs.getString("customer_level"));
        return customer;
    }
}
