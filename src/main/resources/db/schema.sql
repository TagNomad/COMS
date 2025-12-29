-- ============================================================
-- COMS 产品订单管理系统 - 数据库初始化脚本
-- ============================================================
-- 说明：此脚本用于快速部署COMS系统数据库
-- 使用方法：在MySQL中直接运行此脚本即可完成初始化
-- 版本：1.0
-- 更新日期：2025-12-29
-- ============================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS product_order_system 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE product_order_system;

-- ============================================================
-- 1. 产品分类表
-- ============================================================
DROP TABLE IF EXISTS Categories;
CREATE TABLE Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(255) NOT NULL COMMENT '分类名称',
    parent_id INT DEFAULT NULL COMMENT '父分类ID（NULL表示顶级分类）',
    FOREIGN KEY (parent_id) REFERENCES Categories(category_id) ON DELETE CASCADE,
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品分类表';

-- ============================================================
-- 2. 产品信息表
-- ============================================================
DROP TABLE IF EXISTS Products;
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '产品ID',
    product_name VARCHAR(255) NOT NULL COMMENT '产品名称',
    category_id INT NOT NULL COMMENT '分类ID',
    description TEXT COMMENT '产品描述',
    unit VARCHAR(50) COMMENT '单位（件/台/盒等）',
    price DECIMAL(10, 2) COMMENT '价格',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    status VARCHAR(50) DEFAULT '在售' COMMENT '状态（在售/下架等）',
    image_url VARCHAR(255) COMMENT '产品图片URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE,
    INDEX idx_category (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品信息表';

-- ============================================================
-- 3. 客户表
-- ============================================================
DROP TABLE IF EXISTS Customers;
CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
    customer_name VARCHAR(255) NOT NULL COMMENT '客户姓名',
    contact_name VARCHAR(100) COMMENT '联系人姓名',
    phone VARCHAR(50) COMMENT '电话',
    address VARCHAR(255) COMMENT '地址',
    email VARCHAR(255) COMMENT '邮箱',
    customer_level VARCHAR(50) DEFAULT '普通会员' COMMENT '客户等级',
    password VARCHAR(255) NOT NULL COMMENT '登录密码',
    status VARCHAR(20) DEFAULT 'active' COMMENT '账户状态',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    UNIQUE INDEX idx_phone (phone),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- ============================================================
-- 4. 管理员表
-- ============================================================
DROP TABLE IF EXISTS Admins;
CREATE TABLE Admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '管理员ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    role VARCHAR(20) DEFAULT 'admin' COMMENT '角色（super_admin/admin）',
    last_login DATETIME COMMENT '最后登录时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ============================================================
-- 5. 登录日志表
-- ============================================================
DROP TABLE IF EXISTS Login_Logs;
CREATE TABLE Login_Logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id INT NOT NULL COMMENT '用户ID',
    role VARCHAR(20) NOT NULL COMMENT '角色类型（admin/customer）',
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    INDEX idx_user (user_id, role),
    INDEX idx_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志表';

-- ============================================================
-- 6. 订单表
-- ============================================================
DROP TABLE IF EXISTS Orders;
CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    customer_id INT NOT NULL COMMENT '客户ID',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    total_amount DECIMAL(10, 2) COMMENT '订单总金额',
    order_status VARCHAR(50) DEFAULT '待审核' COMMENT '订单状态',
    shipping_address VARCHAR(255) COMMENT '收货地址',
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE,
    INDEX idx_customer (customer_id),
    INDEX idx_status (order_status),
    INDEX idx_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================================
-- 7. 订单明细表
-- ============================================================
DROP TABLE IF EXISTS Order_Items;
CREATE TABLE Order_Items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单明细ID',
    order_id INT NOT NULL COMMENT '订单ID',
    product_id INT NOT NULL COMMENT '产品ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10, 2) COMMENT '单价',
    total_price DECIMAL(10, 2) COMMENT '小计',
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单明细表';

-- ============================================================
-- 8. 客户价格表（VIP价格等）
-- ============================================================
DROP TABLE IF EXISTS Customer_Prices;
CREATE TABLE Customer_Prices (
    customer_level VARCHAR(50) COMMENT '客户等级',
    product_id INT COMMENT '产品ID',
    price DECIMAL(10, 2) COMMENT 'VIP价格',
    PRIMARY KEY (customer_level, product_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户价格表';

-- ============================================================
-- 9. 销售统计表
-- ============================================================
DROP TABLE IF EXISTS Sales_Statistics;
CREATE TABLE Sales_Statistics (
    stat_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    total_sales DECIMAL(10, 2) COMMENT '总销售额',
    total_quantity INT COMMENT '总销量',
    INDEX idx_date (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='销售统计表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 插入默认管理员账号（用户名: admin, 密码: 123456）
INSERT INTO Admins (username, password, role) 
VALUES ('admin', '123456', 'super_admin')
ON DUPLICATE KEY UPDATE password='123456';

-- 插入基础产品分类
INSERT INTO Categories (category_name, parent_id) VALUES 
('电子产品', NULL),
('手机', 1),
('电脑', 1),
('平板', 1),
('服装', NULL),
('男装', 5),
('女装', 5),
('家居用品', NULL),
('厨房用品', 8),
('卧室用品', 8),
('食品饮料', NULL),
('零食', 11),
('饮料', 11);

-- ============================================================
-- 完成提示
-- ============================================================
SELECT 
    '数据库初始化完成！' AS '状态',
    'product_order_system' AS '数据库名称',
    '默认管理员账号: admin' AS '管理员',
    '默认管理员密码: 123456' AS '密码',
    '建议登录后立即修改密码' AS '提示';

-- 显示创建的表
SHOW TABLES;
