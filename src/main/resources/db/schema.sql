-- 创建数据库
CREATE DATABASE IF NOT EXISTS product_order_system;
USE product_order_system;

-- 创建产品分类表
CREATE TABLE Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    parent_id INT DEFAULT NULL,
    FOREIGN KEY (parent_id) REFERENCES Categories(category_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建产品信息表
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    category_id INT NOT NULL,
    description TEXT,
    unit VARCHAR(50),
    price DECIMAL(10, 2),
    stock_quantity INT,
    status VARCHAR(50),
    image_url VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建客户表
CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255) NOT NULL,
    contact_name VARCHAR(100),
    phone VARCHAR(50),
    address VARCHAR(255),
    email VARCHAR(255),
    customer_level VARCHAR(50),
    password VARCHAR(255),
    status VARCHAR(20) DEFAULT 'active',
    avatar_url VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建管理员表
CREATE TABLE Admins (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'admin',
    last_login DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建登录日志表
CREATE TABLE Login_Logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'admin' or 'customer'
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认管理员账号
INSERT INTO Admins (username, password, role) VALUES ('admin', '123456', 'super_admin');

-- 创建订单表
CREATE TABLE Orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2),
    order_status VARCHAR(50),
    shipping_address VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建订单明细表
CREATE TABLE Order_Items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2),
    total_price DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建客户价格表
CREATE TABLE Customer_Prices (
    customer_level VARCHAR(50),
    product_id INT,
    price DECIMAL(10, 2),
    PRIMARY KEY (customer_level, product_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建销售统计表
CREATE TABLE Sales_Statistics (
    stat_id INT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    total_sales DECIMAL(10, 2),
    total_quantity INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 插入初始数据
-- =============================================

-- 插入默认产品分类
INSERT INTO Categories (category_name, parent_id) VALUES ('电子产品', NULL);
INSERT INTO Categories (category_name, parent_id) VALUES ('手机', 1);
INSERT INTO Categories (category_name, parent_id) VALUES ('电脑', 1);
INSERT INTO Categories (category_name, parent_id) VALUES ('平板', 1);
INSERT INTO Categories (category_name, parent_id) VALUES ('服装', NULL);
INSERT INTO Categories (category_name, parent_id) VALUES ('男装', 5);
INSERT INTO Categories (category_name, parent_id) VALUES ('女装', 5);
INSERT INTO Categories (category_name, parent_id) VALUES ('家居用品', NULL);
INSERT INTO Categories (category_name, parent_id) VALUES ('厨房用品', 8);
INSERT INTO Categories (category_name, parent_id) VALUES ('卧室用品', 8);
INSERT INTO Categories (category_name, parent_id) VALUES ('食品饮料', NULL);
INSERT INTO Categories (category_name, parent_id) VALUES ('零食', 11);
INSERT INTO Categories (category_name, parent_id) VALUES ('饮料', 11);

-- 插入示例产品
INSERT INTO Products (product_name, category_id, description, unit, price, stock_quantity, status) VALUES 
('iPhone 15 Pro', 2, '最新款苹果手机，A17芯片', '台', 8999.00, 50, '在售'),
('MacBook Pro 14', 3, 'M3芯片，14英寸屏幕', '台', 14999.00, 30, '在售'),
('iPad Air', 4, '10.9英寸平板电脑', '台', 4799.00, 40, '在售'),
('男士休闲T恤', 6, '纯棉舒适，多色可选', '件', 99.00, 200, '在售'),
('女士连衣裙', 7, '夏季新款，时尚优雅', '件', 199.00, 150, '在售'),
('不锈钢炒锅', 9, '32cm大容量，不粘涂层', '个', 159.00, 80, '在售'),
('乳胶枕头', 10, '泰国进口天然乳胶', '个', 299.00, 100, '在售'),
('坚果礼盒', 12, '混合坚果，健康美味', '盒', 88.00, 300, '在售'),
('气泡水', 13, '0糖0卡，多种口味', '瓶', 5.00, 500, '在售');

