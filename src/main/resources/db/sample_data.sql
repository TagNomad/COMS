-- ====================================================
-- COMS 系统示例数据插入脚本（带图片）
-- ====================================================
USE product_order_system;

-- ====================================================
-- 1. 清理现有数据（可选，谨慎使用）
-- ====================================================
-- DELETE FROM Order_Items;
-- DELETE FROM Orders;
-- DELETE FROM Products;
-- DELETE FROM Customers;
-- DELETE FROM Categories WHERE category_id > 13;

-- ====================================================
-- 2. 插入更多分类数据
-- ====================================================
INSERT INTO Categories (category_name, parent_id) VALUES 
('运动户外', NULL),
('运动装备', 14),
('户外用品', 14),
('图书音像', NULL),
('小说', 16),
('工具书', 16);

-- ====================================================
-- 3. 更新现有产品，添加图片URL
-- ====================================================
-- 注意：这些图片路径是示例，实际使用时需要：
-- 1. 将生成的图片复制到 webapp/assets/img/products/ 目录
-- 2. 或使用完整的HTTP URL

UPDATE Products SET image_url = '../assets/img/products/iphone15pro.jpg' WHERE product_id = 1;
UPDATE Products SET image_url = 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400' WHERE product_id = 2;
UPDATE Products SET image_url = 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400' WHERE product_id = 3;
UPDATE Products SET image_url = '../assets/img/products/tshirt.jpg' WHERE product_id = 4;
UPDATE Products SET image_url = '../assets/img/products/dress.jpg' WHERE product_id = 5;
UPDATE Products SET image_url = 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400' WHERE product_id = 6;
UPDATE Products SET image_url = 'https://images.unsplash.com/photo-1584100936595-c0654b55a2e2?w=400' WHERE product_id = 7;
UPDATE Products SET image_url = 'https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=400' WHERE product_id = 8;
UPDATE Products SET image_url = 'https://images.unsplash.com/photo-1601024445121-e5b82f020549?w=400' WHERE product_id = 9;

-- ====================================================
-- 4. 插入更多带图片的产品
-- ====================================================
INSERT INTO Products (product_name, category_id, description, unit, price, stock_quantity, status, image_url) VALUES 
-- 电子产品
('Samsung Galaxy S24', 2, '骁龙8 Gen3处理器，AI智能相机', '台', 5999.00, 45, '在售', 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400'),
('小米14 Pro', 2, '徕卡影像，骁龙8 Gen3', '台', 4999.00, 60, '在售', 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=400'),
('ThinkPad X1 Carbon', 3, '商务笔记本，轻薄便携', '台', 12999.00, 25, '在售', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400'),
('Dell XPS 13', 3, '13英寸超窄边框，4K屏幕', '台', 9999.00, 30, '在售', 'https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=400'),
('华为MatePad Pro', 4, '12.6英寸OLED屏，鸿蒙系统', '台', 3999.00, 50, '在售', 'https://images.unsplash.com/photo-1585790050230-5dd28404f905?w=400'),

-- 服装类
('男士商务衬衫', 6, '纯棉免烫，商务正装', '件', 189.00, 150, '在售', 'https://images.unsplash.com/photo-1602810318693-ymg4d86bf3c8e?w=400'),
('男士牛仔裤', 6, '修身直筒，弹力舒适', '件', 299.00, 180, '在售', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=400'),
('女士针织衫', 7, '羊绒混纺，温暖柔软', '件', 399.00, 120, '在售', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=400'),
('女士长裤', 7, '高腰显瘦，多色可选', '件', 259.00, 140, '在售', 'https://images.unsplash.com/photo-1594633313593-bab3825d0caf?w=400'),

-- 家居用品
('陶瓷餐具套装', 9, '28件套，北欧简约风', '套', 299.00, 60, '在售', 'https://images.unsplash.com/photo-1578500494198-246f612d3b3d?w=400'),
('不锈钢刀具组', 9, '7件套，德国工艺', '套', 459.00, 45, '在售', 'https://images.unsplash.com/photo-1593618998160-e34014e67546?w=400'),
('四件套床品', 10, '纯棉贡缎，1.8米床', '套', 599.00, 80, '在售', 'https://images.unsplash.com/photo-1616594266537-a3b8c4e87b2a?w=400'),
('羽绒被', 10, '90%白鹅绒，冬季保暖', '床', 899.00, 50, '在售', 'https://images.unsplash.com/photo-1616046229478-9901c5536a45?w=400'),

-- 食品饮料
('进口咖啡豆', 12, '阿拉比卡精选，中度烘焙', '袋', 128.00, 200, '在售', 'https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=400'),
('蜂蜜礼盒', 12, '野生土蜂蜜，纯天然无添加', '盒', 168.00, 150, '在售', 'https://images.unsplash.com/photo-1587049352846-4a222e784053?w=400'),
('纯净水', 13, '天然矿泉水，550ml', '瓶', 2.00, 1000, '在售', 'https://images.unsplash.com/photo-1559839914-17aae19ea3c0?w=400'),
('功能饮料', 13, '维生素补充，提神醒脑', '瓶', 8.00, 600, '在售', 'https://images.unsplash.com/photo-1600891964599-f61ba0e24092?w=400'),

-- 运动户外
('瑜伽垫', 15, 'TPE材质，防滑加厚10mm', '张', 89.00, 200, '在售', 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400'),
('哑铃套装', 15, '可调节重量，家用健身', '套', 299.00, 100, '在售', 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400'),
('登山包', 16, '60L大容量，防水透气', '个', 399.00, 80, '在售', 'https://images.unsplash.com/photo-1622260614153-03223fb72052?w=400'),
('帐篷', 16, '3-4人双层，防暴雨', '个', 599.00, 50, '在售', 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=400'),

-- 图书音像
('Python编程', 17, '从入门到精通', '本', 89.00, 300, '在售', 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=400'),
('经济学原理', 18, '曼昆著，第7版', '本', 108.00, 150, '在售', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400');

-- ====================================================
-- 5. 插入示例客户数据
-- ====================================================
INSERT INTO Customers (customer_name, contact_name, phone, address, email, customer_level, password, status) VALUES 
('张三', '张先生', '13800138001', '北京市朝阳区xx路xx号', 'zhangsan@example.com', 'VIP', '123456', 'active'),
('李四', '李女士', '13800138002', '上海市浦东新区yy街yy号', 'lisi@example.com', '普通会员', '123456', 'active'),
('王五', '王先生', '13800138003', '广州市天河区zz大道zz号', 'wangwu@example.com', 'VIP', '123456', 'active'),
('赵六', '赵女士', '13800138004', '深圳市南山区aa路aa号', 'zhaoliu@example.com', '普通会员', '123456', 'active'),
('孙七', '孙先生', '13800138005', '杭州市西湖区bb街bb号', 'sunqi@example.com', '黄金会员', '123456', 'active');

-- ====================================================
-- 6. 插入示例订单数据
-- ====================================================
INSERT INTO Orders (customer_id, order_date, total_amount, order_status, shipping_address) VALUES 
(1, '2025-12-20 10:30:00', 23997.00, '已确认', '北京市朝阳区xx路xx号'),
(2, '2025-12-21 14:20:00', 398.00, '已发货', '上海市浦东新区yy街yy号'),
(3, '2025-12-22 09:15:00', 5999.00, '待审核', '广州市天河区zz大道zz号'),
(4, '2025-12-23 16:45:00', 888.00, '已完成', '深圳市南山区aa路aa号'),
(1, '2025-12-24 11:00:00', 12999.00, '已确认', '北京市朝阳区xx路xx号');

-- ====================================================
-- 7. 插入订单明细数据
-- ====================================================
INSERT INTO Order_Items (order_id, product_id, quantity, unit_price, total_price) VALUES 
-- 订单1：张三购买两台电脑和一部手机
(1, 2, 1, 14999.00, 14999.00),  -- MacBook Pro
(1, 1, 1, 8999.00, 8999.00),    -- iPhone 15 Pro

-- 订单2：李四购买服装
(2, 4, 2, 99.00, 198.00),       -- 男士T恤 x2
(2, 5, 1, 199.00, 199.00),      -- 女士连衣裙

-- 订单3：王五购买手机
(3, 10, 1, 5999.00, 5999.00),   -- Samsung Galaxy

-- 订单4：赵六购买家居用品
(4, 6, 2, 159.00, 318.00),      -- 炒锅 x2
(4, 7, 2, 285.00, 570.00),      -- 枕头 x2

-- 订单5：张三再次购买
(5, 12, 1, 12999.00, 12999.00); -- ThinkPad

-- ====================================================
-- 8. 更新统计信息
-- ====================================================
-- 查询统计
SELECT 
    c.category_name as '分类',
    COUNT(p.product_id) as '商品数量',
    AVG(p.price) as '平均价格',
    SUM(p.stock_quantity) as '总库存'
FROM Categories c
LEFT JOIN Products p ON c.category_id = p.category_id
WHERE c.parent_id IS NOT NULL
GROUP BY c.category_id, c.category_name
ORDER BY COUNT(p.product_id) DESC;

-- ====================================================
-- 完成
-- ====================================================
SELECT '数据插入完成！' AS '状态';
SELECT COUNT(*) AS '产品总数' FROM Products;
SELECT COUNT(*) AS '客户总数' FROM Customers;
SELECT COUNT(*) AS '订单总数' FROM Orders;
