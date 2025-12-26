-- 插入模拟客户数据
USE product_order_system;

INSERT INTO Customers (customer_name, contact_name, phone, address, email, customer_level, password, status) VALUES 
('张伟', '张经理', '13800138001', '北京市朝阳区', 'zhangwei@example.com', '金牌', '123456', '激活'),
('李娜', '李小姐', '13912345678', '上海市浦东新区', 'lina@example.com', '银牌', '123456', '激活'),
('王芳', '王主任', '13688889999', '广州市天河区', 'wangfang@example.com', '普通', '123456', '激活'),
('刘洋', '刘先生', '13700001111', '深圳市南山区', 'liuyang@example.com', '钻石', '123456', '激活'),
('陈静', '陈老师', '13566667777', '杭州市西湖区', 'chenjing@example.com', '普通', '123456', '激活'),
('赵雷', '赵总', '13300133001', '成都市武侯区', 'zhaolei@example.com', '金牌', '123456', '激活'),
('孙俪', '孙女士', '18611112222', '南京市玄武区', 'sunli@example.com', '银牌', '123456', '激活'),
('周杰', '周先生', '15899998888', '武汉市江汉区', 'zhoujie@example.com', '普通', '123456', '激活');
