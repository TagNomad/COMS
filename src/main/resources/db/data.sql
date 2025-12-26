-- 插入产品分类初始数据
-- 使用 product_order_system 数据库
USE product_order_system;

-- 插入顶级分类
INSERT INTO Categories (category_name, parent_id) VALUES 
('电子产品', NULL),
('服装鞋包', NULL),
('家居用品', NULL),
('食品饮料', NULL),
('图书文具', NULL),
('运动户外', NULL),
('美妆个护', NULL),
('母婴用品', NULL);

-- 插入二级分类
INSERT INTO Categories (category_name, parent_id) VALUES 
-- 电子产品子分类
('手机通讯', 1),
('电脑办公', 1),
('数码配件', 1),
('智能设备', 1),

-- 服装鞋包子分类
('男装', 2),
('女装', 2),
('鞋靴', 2),
('箱包', 2),

-- 家居用品子分类
('厨房用品', 3),
('家装建材', 3),
('家具', 3),
('家居装饰', 3),

-- 食品饮料子分类
('休闲零食', 4),
('饮料冲调', 4),
('生鲜食品', 4),
('酒类', 4),

-- 图书文具子分类
('图书', 5),
('文具用品', 5),
('教辅材料', 5),

-- 运动户外子分类
('运动服饰', 6),
('健身器材', 6),
('户外用品', 6),

-- 美妆个护子分类
('护肤', 7),
('彩妆', 7),
('个护', 7),

-- 母婴用品子分类
('婴儿用品', 8),
('儿童用品', 8),
('孕妇用品', 8),
('医药保健', NULL);

-- 插入医药保健子分类
INSERT INTO Categories (category_name, parent_id) VALUES 
('中西药品', (SELECT category_id FROM (SELECT category_id FROM Categories WHERE category_name = '医药保健') as t)),
('营养辅食', (SELECT category_id FROM (SELECT category_id FROM Categories WHERE category_name = '医药保健') as t)),
('医疗器械', (SELECT category_id FROM (SELECT category_id FROM Categories WHERE category_name = '医药保健') as t)),
('计生用品', (SELECT category_id FROM (SELECT category_id FROM Categories WHERE category_name = '医药保健') as t));

-- 插入药品类产品数据
SET @med_category_id = (SELECT category_id FROM Categories WHERE category_name = '中西药品' LIMIT 1);

INSERT INTO Products (product_name, category_id, description, unit, price, stock_quantity, status) VALUES 
('阿莫西林胶囊', @med_category_id, '用于敏感菌所致的呼吸道感染、泌尿生殖道感染等', '盒', 15.50, 100, '在售'),
('布洛芬缓释胶囊', @med_category_id, '用于缓解轻至中度疼痛，如头痛、关节痛、牙痛等', '盒', 22.00, 200, '在售'),
('连花清瘟胶囊', @med_category_id, '清瘟解毒，宣肺泄热。用于治疗流行性感冒', '盒', 14.80, 150, '在售'),
('板蓝根颗粒', @med_category_id, '清热解毒，凉血利咽。用于肺胃热盛所致的咽喉肿痛', '包', 12.00, 300, '在售'),
('999感冒灵颗粒', @med_category_id, '解热镇痛。用于感冒引起的头痛，发热，鼻塞，流涕', '盒', 18.50, 250, '在售'),
('蒙脱石散', @med_category_id, '用于成人及儿童急、慢性腹泻', '盒', 25.00, 80, '在售'),
('罗红霉素分散片', @med_category_id, '适用于化脓性链球菌引起的咽炎及扁桃体炎', '盒', 32.00, 60, '在售'),
('头孢克肟分散片', @med_category_id, '适用于对头孢克肟敏感的链球菌属、肺炎球效等', '盒', 45.00, 50, '在售'),
('藿香正气水', @med_category_id, '解表化湿，理气和中。用于外感风寒、内伤湿滞', '盒', 10.50, 500, '在售'),
('健胃消食片', @med_category_id, '健胃消食。用于脾胃虚弱所致的食积', '盒', 16.00, 120, '在售'),
('葡萄糖酸钙口服溶液', @med_category_id, '用于预防和治疗钙缺乏症，如骨质疏松等', '盒', 28.50, 90, '在售'),
('维生素C泡腾片', @med_category_id, '增强机体抵抗力，用于预防和治疗坏血病', '瓶', 35.00, 200, '在售'),
('云南白药气雾剂', @med_category_id, '活血散瘀，消肿止痛。用于跌打损伤，瘀血肿痛', '盒', 48.00, 40, '在售'),
('西瓜霜喷剂', @med_category_id, '清热解毒，消肿止痛。用于口舌生疮，牙龈肿痛', '瓶', 13.50, 180, '在售'),
('扶他林软膏', @med_category_id, '用于缓解肌肉、软组织和关节的轻至中度疼痛', '支', 38.00, 70, '在售'),
('莫匹罗星软膏', @med_category_id, '局部外用抗生素，适用于革兰阳性球菌引起的皮肤感染', '支', 26.00, 110, '在售'),
('左氧氟沙星滴眼液', @med_category_id, '用于治疗细菌性结膜炎、角膜炎等眼部感染', '瓶', 19.80, 130, '在售'),
('糠酸莫米松鼻喷雾剂', @med_category_id, '用于治疗成人、青少年和3至11岁儿童的季节性鼻炎', '瓶', 65.00, 30, '在售'),
('咽立爽口含滴丸', @med_category_id, '疏风散热，消肿止痛，清喉利咽', '瓶', 21.00, 160, '在售'),
('补中益气丸', @med_category_id, '补中益气。用于体倦乏力，内脏下垂', '盒', 30.00, 45, '在售');


-- 插入模拟客户数据
INSERT INTO Customers (customer_name, contact_name, phone, address, email, customer_level, password, status) VALUES 
('张伟', '张经理', '13800138001', '北京市朝阳区', 'zhangwei@example.com', '金牌', '123456', '激活'),
('李娜', '李小姐', '13912345678', '上海市浦东新区', 'lina@example.com', '银牌', '123456', '激活'),
('王芳', '王主任', '13688889999', '广州市天河区', 'wangfang@example.com', '普通', '123456', '激活'),
('刘洋', '刘先生', '13700001111', '深圳市南山区', 'liuyang@example.com', '钻石', '123456', '激活'),
('陈静', '陈老师', '13566667777', '杭州市西湖区', 'chenjing@example.com', '普通', '123456', '激活'),
('赵雷', '赵总', '13300133001', '成都市武侯区', 'zhaolei@example.com', '金牌', '123456', '激活'),
('孙俪', '孙女士', '18611112222', '南京市玄武区', 'sunli@example.com', '银牌', '123456', '激活'),
('周杰', '周先生', '15899998888', '武汉市江汉区', 'zhoujie@example.com', '普通', '123456', '激活');
