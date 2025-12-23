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
    customer_level VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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

