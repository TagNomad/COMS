package com.company.dao;

import com.company.model.Product;
import com.company.utils.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品数据访问类
 */
public class ProductDao {

    /**
     * 获取所有产品
     */
    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.category_id ORDER BY p.product_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Product product = mapResultSet(rs);
                product.setCategoryName(rs.getString("category_name"));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * 根据ID查找产品
     */
    public Product findById(int productId) {
        String sql = "SELECT p.*, c.category_name FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.category_id WHERE p.product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Product product = mapResultSet(rs);
                    product.setCategoryName(rs.getString("category_name"));
                    return product;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据分类查找产品
     */
    public List<Product> findByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.category_id WHERE p.category_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSet(rs);
                    product.setCategoryName(rs.getString("category_name"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * 按名称搜索产品
     */
    public List<Product> searchByName(String keyword) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                "WHERE p.product_name LIKE ? ORDER BY p.product_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSet(rs);
                    product.setCategoryName(rs.getString("category_name"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    /**
     * 新增产品
     */
    public boolean insert(Product product) {
        String sql = "INSERT INTO Products (product_name, category_id, description, unit, price, stock_quantity, status, image_url) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            if (product.getCategoryId() != null) {
                stmt.setInt(2, product.getCategoryId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, product.getDescription());
            stmt.setString(4, product.getUnit());
            stmt.setBigDecimal(5, product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO);
            stmt.setInt(6, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            stmt.setString(7, product.getStatus() != null ? product.getStatus() : "在售");
            stmt.setString(8, product.getImageUrl());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新产品
     */
    public boolean update(Product product) {
        String sql = "UPDATE Products SET product_name = ?, category_id = ?, description = ?, " +
                "unit = ?, price = ?, stock_quantity = ?, status = ?, image_url = ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            if (product.getCategoryId() != null) {
                stmt.setInt(2, product.getCategoryId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, product.getDescription());
            stmt.setString(4, product.getUnit());
            stmt.setBigDecimal(5, product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO);
            stmt.setInt(6, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            stmt.setString(7, product.getStatus() != null ? product.getStatus() : "在售");
            stmt.setString(8, product.getImageUrl());
            stmt.setInt(9, product.getProductId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除产品
     */
    public boolean delete(int productId) {
        String sql = "DELETE FROM Products WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新库存
     */
    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE Products SET stock_quantity = stock_quantity + ? WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取热销产品排行
     */
    public List<Product> getTopSellingProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, COALESCE(SUM(oi.quantity), 0) as total_sold " +
                "FROM Products p " +
                "LEFT JOIN Categories c ON p.category_id = c.category_id " +
                "LEFT JOIN Order_Items oi ON p.product_id = oi.product_id " +
                "GROUP BY p.product_id ORDER BY total_sold DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = mapResultSet(rs);
                    product.setCategoryName(rs.getString("category_name"));
                    products.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    private Product mapResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setDescription(rs.getString("description"));
        product.setUnit(rs.getString("unit"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setStatus(rs.getString("status"));
        try {
            product.setImageUrl(rs.getString("image_url"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist yet to avoid crashing
        }
        return product;
    }
}
