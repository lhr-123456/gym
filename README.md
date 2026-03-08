# 健身房管理系统

## 项目简介

这是一个基于 Spring Boot + Vue 的健身房管理系统，采用前后端分离架构，实现了健身房日常运营管理的核心功能。

## 技术栈

### 后端技术栈
- Spring Boot 2.7.18：提供快速应用开发能力和自动配置
- MyBatis-Plus 3.5.3：增强型 ORM 框架，简化数据库操作
- Spring Security 5.7.11：负责系统认证与授权
- JWT：实现无状态认证
- Redis：缓存和会话管理
- MySQL 8.0：关系型数据库
- Lombok：简化 Java Bean 编写
- Hutool：Java 工具库

### 前端技术栈
- Vue.js 2.6：构建响应式用户界面
- Element UI 2.15：UI 组件库
- Axios 1.6：HTTP 请求库
- Vue Router 3.5：路由管理
- Vuex 3.6：状态管理

## 功能模块

### 管理员功能
- 会员管理：会员信息录入、查询、修改和删除，会员等级划分和积分管理
- 教练管理：教练信息录入、查询和修改，教练课程分配和排课管理
- 课程管理：课程信息录入、分类和展示，课程排期和容量设置，课程预约管理
- 器材管理：健身器材分类、登记和状态监控，器材借用、归还和报损保修管理
- 数据分析：会员活跃度分析，课程利用率统计，器材使用情况监控，教练绩效评估

### 会员功能
- 个人中心：个人信息查看和修改，会员卡状态查询和管理，消费记录查询和导出
- 课程预约：课程浏览和筛选，课程预约和取消，预约记录查看和管理
- 器材借用：器材状态查看，器材借用和归还，器材使用记录查询
- 课程评价：课程评分和反馈，教练评价和推荐

### 教练功能
- 课程安排：课程排期查看和调整，课程容量管理
- 会员管理：教练负责会员信息查看，会员训练计划制定和跟踪
- 训练记录：会员训练数据记录，训练效果分析和反馈

## 快速开始

### 环境要求
- JDK 1.8+
- MySQL 8.0+
- Redis
- Node.js 14+
- Maven 3.6+

### 数据库配置

1. 创建数据库并导入初始化脚本：
```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

2. 修改配置文件 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gym_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

3. 修改 Redis 配置：
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### 后端启动

```bash
cd gym
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080/api 启动

### 前端启动

```bash
cd gym/frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 默认账户

- 管理员账户：admin / admin123
- 用户类型：管理员 (1)、教练 (2)、会员 (3)

## 项目结构

```
gym/
├── src/
│   ├── main/
│   │   ├── java/com/gym/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── exception/       # 异常处理
│   │   │   ├── filter/          # 过滤器
│   │   │   ├── mapper/          # 数据访问层
│   │   │   ├── service/         # 服务层
│   │   │   └── GymManagementApplication.java
│   │   └── resources/
│   │       ├── sql/             # SQL 脚本
│   │       └── application.yml  # 配置文件
│   └── test/
├── frontend/
│   ├── src/
│   │   ├── api/                 # API 接口
│   │   ├── router/              # 路由配置
│   │   ├── store/               # 状态管理
│   │   ├── styles/              # 样式文件
│   │   ├── utils/               # 工具函数
│   │   └── views/               # 页面组件
│   ├── public/
│   └── package.json
└── pom.xml
```

## API 接口文档

### 认证接口
- POST /api/auth/login - 用户登录
- POST /api/auth/register - 用户注册
- GET /api/auth/profile - 获取用户信息

### 会员管理
- GET /api/member/page - 分页查询会员
- GET /api/member/{id} - 获取会员详情
- POST /api/member - 新增会员
- PUT /api/member - 更新会员
- DELETE /api/member/{id} - 删除会员

### 教练管理
- GET /api/coach/page - 分页查询教练
- GET /api/coach/{id} - 获取教练详情
- POST /api/coach - 新增教练
- PUT /api/coach - 更新教练
- DELETE /api/coach/{id} - 删除教练

### 课程管理
- GET /api/course/page - 分页查询课程
- GET /api/course/{id} - 获取课程详情
- POST /api/course - 新增课程
- PUT /api/course - 更新课程
- DELETE /api/course/{id} - 删除课程
- POST /api/course/book/{courseId} - 预约课程
- POST /api/course/cancel/{bookingId} - 取消预约

### 器材管理
- GET /api/equipment/page - 分页查询器材
- GET /api/equipment/{id} - 获取器材详情
- POST /api/equipment - 新增器材
- PUT /api/equipment - 更新器材
- DELETE /api/equipment/{id} - 删除器材

## 开发说明

### 后端开发规范
1. 遵循 RESTful API 设计规范
2. 使用统一响应格式 ApiResponse
3. 使用 MyBatis-Plus 进行数据库操作
4. 使用 Spring Security + JWT 进行认证授权
5. 使用 Lombok 简化代码

### 前端开发规范
1. 使用 Vue 2.x 语法
2. 使用 Element UI 组件库
3. 使用 Axios 进行 HTTP 请求
4. 使用 Vuex 进行状态管理
5. 使用 Vue Router 进行路由管理

## 注意事项

1. 首次运行前需要先配置好 MySQL 和 Redis
2. 确保数据库连接信息和 Redis 配置正确
3. 前端开发环境需要配置代理到后端服务
4. 生产环境需要修改相关配置并打包部署

## License

MIT
