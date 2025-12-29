# COMS - 产品订单管理系统

完整的电商管理系统，包含用户商城前台和管理员后台。

## 📋 项目简介

COMS（Commerce Order Management System）是一个功能完善的B2C电商系统，支持：
- ✅ 用户注册、登录
- ✅ 商品浏览、分类筛选
- ✅ 购物车管理（localStorage）
- ✅ 模拟支付流程
- ✅ 订单管理
- ✅ 管理员后台（商品、分类、客户、订单管理）
- ✅ 权限分离（管理员/用户严格隔离）

## 🛠️ 技术栈

### 后端
- **Java Servlet** - 核心框架
- **MySQL 5.7+** - 数据库
- **JDBC** - 数据访问
- **Tomcat 8.5+** - Web服务器

### 前端
- **HTML5 + CSS3** - 页面结构和样式
- **原生JavaScript** - 交互逻辑
- **LocalStorage** - 购物车持久化
- **Fetch API** - 异步请求

## 📦 快速开始

### 1. 环境要求

- ✅ JDK 8 或更高版本
- ✅ MySQL 5.7 或更高版本
- ✅ Apache Tomcat 8.5 或更高版本
- ✅ IDE（推荐 IntelliJ IDEA 或 Eclipse）

### 2. 数据库部署

**步骤 1：创建数据库**

在MySQL中执行以下脚本（二选一）：

**方法A - MySQL Workbench（推荐）：**
```
1. 打开 MySQL Workbench
2. 连接到MySQL服务器
3. File → Open SQL Script
4. 选择：src/main/resources/db/schema.sql
5. 点击 ⚡ Execute
```

**方法B - 命令行：**
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

执行完成后会自动创建：
- ✅ 数据库：`product_order_system`
- ✅ 9张业务表
- ✅ 默认管理员账号：`admin` / `123456`
- ✅ 基础分类数据（13个）

**步骤 2：验证数据库**
```sql
USE product_order_system;
SHOW TABLES;  -- 应该显示9张表
SELECT * FROM Admins;  -- 应该有1条管理员记录
```

### 3. 配置数据库连接

编辑文件：`src/main/java/com/company/utils/DBUtil.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/product_order_system?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
private static final String USER = "root";
private static final String PASSWORD = "你的MySQL密码";
```

### 4. 部署到Tomcat

**使用 IntelliJ IDEA：**
```
1. Run → Edit Configurations
2. 点击 + → Tomcat Server → Local
3. Deployment → + → Artifact → COMS:war exploded
4. Application context: /COMS
5. 点击 Run
```

**使用 Eclipse：**
```
1. 右键项目 → Run As → Run on Server
2. 选择 Tomcat 服务器
3. Finish
```

**手动部署：**
```bash
# 打包项目
mvn clean package

# 复制war文件到Tomcat
cp target/COMS.war $TOMCAT_HOME/webapps/

# 启动Tomcat
$TOMCAT_HOME/bin/startup.sh  # Linux/Mac
$TOMCAT_HOME/bin/startup.bat # Windows
```

### 5. 访问系统

启动成功后，在浏览器访问：

| 页面 | URL | 账号 |
|------|-----|------|
| **管理后台** | http://localhost:8080/COMS/admin/dashboard.html | admin / 123456 |
| **用户商城** | http://localhost:8080/COMS/user/home.html | 需先注册 |
| **登录页面** | http://localhost:8080/COMS/common/login.html | - |

## 👤 默认账号

### 管理员账号
- **用户名**：`admin`
- **密码**：`123456`
- **权限**：管理员后台所有功能

### 测试用户（可选）
执行 `sample_data.sql` 后可获得：
- 张三 / 123456（VIP）
- 李四 / 123456（普通会员）
- 王五 / 123456（VIP）

## 🎯 功能说明

### 用户端功能

#### 1. 商品浏览
- **首页**：展示推荐商品和分类导航
- **商品列表**：按分类筛选、搜索
- **分页加载**：优化大数据量展示

#### 2. 购物车
- **添加商品**：点击"+"快速加入购物车
- **数量调整**：购物车内增减数量
- **实时计算**：自动计算总价
- **本地存储**：使用 localStorage 持久化

#### 3. 下单流程
```
选择商品 → 加入购物车 → 去结算 → 
选择支付方式 → 填写地址 → 扫码支付（模拟） → 
订单创建成功
```

#### 4. 订单管理
- **我的订单**：查看所有订单
- **订单详情**：查看商品明细、地址、状态
- **订单状态**：待审核/已确认/已发货/已完成

### 管理员功能

#### 1. 仪表盘
- 销售统计
- 订单概览
- 热销商品

#### 2. 商品管理
- **增删改查**：完整的CRUD操作
- **图片上传**：支持商品图片URL
- **分类关联**：选择所属分类
- **库存管理**：查看和更新库存

#### 3. 分类管理
- **树形结构**：支持二级分类
- **动态加载**：自动刷新分类树

#### 4. 客户管理
- **客户列表**：查看所有注册用户
- **等级管理**：VIP、普通会员等
- **状态控制**：启用/禁用账户

#### 5. 订单管理
- **订单审核**：待审核订单处理
- **状态更新**：修改订单状态
- **订单详情**：查看完整信息

## 🔐 权限说明

### 严格的角色分离

系统实现了**完全隔离**的权限控制：

| 角色 | 允许访问 | 禁止访问 |
|------|----------|----------|
| **管理员** | `/admin/*` 所有管理页面 | ❌ `/user/*` 用户页面 |
| **用户** | `/user/*` 所有用户页面 | ❌ `/admin/*` 管理页面 |

**跨权限访问**：
- 管理员尝试访问用户页面 → 强制登出
- 用户尝试访问管理后台 → 强制登出
- 均会显示错误提示

### 实现机制
- ✅ 前端：`app.js` 检查路径和角色
- ✅ 后端：`LoginServlet` 验证角色
- ✅ 会话：基于 HttpSession

## 📁 项目结构

```
COMS/
├── src/main/
│   ├── java/com/company/
│   │   ├── controller/      # Servlet控制器
│   │   │   ├── LoginServlet.java
│   │   │   ├── ProductServlet.java
│   │   │   ├── CategoryServlet.java
│   │   │   ├── CustomerServlet.java
│   │   │   └── OrderServlet.java
│   │   ├── dao/             # 数据访问层
│   │   ├── model/           # 实体类
│   │   ├── service/         # 业务逻辑层
│   │   └── utils/           # 工具类
│   ├── resources/
│   │   └── db/
│   │       ├── schema.sql        # 数据库结构（必需）
│   │       └── sample_data.sql   # 示例数据（可选）
│   └── webapp/
│       ├── admin/           # 管理员页面
│       │   ├── dashboard.html
│       │   ├── products.html
│       │   ├── categories.html
│       │   ├── customers.html
│       │   └── orders.html
│       ├── user/            # 用户页面
│       │   ├── home.html
│       │   ├── products.html
│       │   ├── cart.html
│       │   └── my-orders.html
│       ├── common/          # 公共页面
│       │   ├── login.html
│       │   ├── register.html
│       │   ├── nav-admin.html
│       │   └── top-nav.html
│       ├── assets/
│       │   ├── css/style.css
│       │   ├── js/
│       │   │   ├── app.js        # 核心工具函数
│       │   │   └── cart.js       # 购物车逻辑
│       │   └── img/
│       │       ├── logo.svg      # 系统Logo
│       │       └── products/     # 商品图片
│       └── WEB-INF/
│           └── web.xml
└── pom.xml                  # Maven配置
```

## 🔧 API 接口

### 登录认证
- `POST /api/login` - 登录
- `GET /api/login` - 检查登录状态
- `POST /api/login?action=logout` - 登出

### 产品管理
- `GET /api/product` - 获取所有商品
- `GET /api/product/{id}` - 获取单个商品
- `POST /api/product` - 创建商品
- `PUT /api/product` - 更新商品
- `DELETE /api/product/{id}` - 删除商品

### 分类管理
- `GET /api/category` - 获取所有分类
- `POST /api/category` - 创建分类
- `PUT /api/category` - 更新分类
- `DELETE /api/category/{id}` - 删除分类

### 订单管理
- `GET /api/order` - 获取订单列表
- `GET /api/order/{id}` - 获取订单详情
- `POST /api/order` - 创建订单
- `PUT /api/order` - 更新订单状态

### 客户管理
- `GET /api/customer` - 获取客户列表
- `POST /api/customer` - 注册新客户

## 🎨 界面特色

- ✨ **现代化设计**：采用 Glassmorphism 风格
- 🎭 **渐变配色**：精心挑选的色彩方案
- ⚡ **流畅动画**：hover效果和过渡动画
- 📱 **响应式布局**：适配桌面和平板
- 🎯 **用户友好**：直观的操作流程

## ⚠️ 注意事项

### 安全性
- ⚠️ **密码明文存储**：生产环境请使用加密（如BCrypt）
- ⚠️ **SQL注入防护**：已使用PreparedStatement
- ⚠️ **XSS防护**：前端需要添加输入验证

### 数据库
- 📌 首次运行必须执行 `schema.sql`
- 📌 `sample_data.sql` 仅用于测试，可选
- 📌 修改密码后需重启应用

### 开发建议
- 💡 使用 Maven 管理依赖
- 💡 建议配置热部署加快开发
- 💡 Chrome DevTools 便于调试前端

## 🐛 常见问题

### Q1: 登录后显示 "Hi, undefined"
**A:** 检查 `localStorage` 中的 user 对象，可能是登录接口返回数据不完整

### Q2: 购物车数据丢失
**A:** 购物车使用 localStorage，清除浏览器缓存会导致数据丢失

### Q3: 404 错误
**A:** 检查 Tomcat 的 Application Context 是否设置为 `/COMS`

### Q4: 数据库连接失败
**A:** 
1. 确认MySQL服务已启动
2. 检查 `DBUtil.java` 中的用户名密码
3. 确认数据库 `product_order_system` 已创建

### Q5: 图片不显示
**A:** 
1. 本地图片：复制到 `webapp/assets/img/products/`
2. 在线图片：使用完整URL（已内置Unsplash图片）

## 📝 版本历史

### v1.0.0 (2025-12-29)
- ✅ 完整的电商功能实现
- ✅ 管理员和用户权限分离
- ✅ 购物车和支付流程
- ✅ 现代化UI设计
- ✅ 完善的数据库结构

## 📄 许可证

本项目仅供学习和研究使用。

## 📧 联系方式

如有问题，请提Issue或联系开发者。

---

**祝您使用愉快！** 🎉
