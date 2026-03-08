# 健身房管理系统 - 快速启动指南

## 环境要求

- **JDK**: 1.8
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Redis**: 5.0+
- **Node.js**: 14+ (用于前端开发)

## 一、数据库配置

### 方式一：使用批处理脚本（推荐）
双击运行项目根目录下的 `init-db.bat` 文件：
```bash
init-db.bat
```

### 方式二：使用 MySQL 命令行
```bash
# 创建数据库
mysql -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS gym_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入数据（使用无中文注释版本）
mysql -u root -p123456 gym_db < src/main/resources/sql/init_simple.sql
```

### 方式三：使用 MySQL Workbench
1. 打开 MySQL Workbench 并连接到数据库
2. 执行 `src/main/resources/sql/init_en.sql` 文件
3. 或者执行 `src/main/resources/sql/init_simple.sql` 文件

## 二、Redis 配置

### Windows
下载并运行 Redis Windows 版本：
```bash
redis-server.exe
```

### Linux
```bash
sudo systemctl start redis
```

## 三、后端配置和启动

### 1. 修改数据库配置
编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gym_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root          # 修改为你的 MySQL 用户名
    password: your_password # 修改为你的 MySQL 密码
  
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码请配置
```

### 2. 编译打包
```bash
mvn clean package -DskipTests
```

### 3. 运行
```bash
java -jar target/gym-management-system-1.0.0.jar
```

或者使用 Maven：
```bash
mvn spring-boot:run
```

后端服务将在 **http://localhost:8080/api** 启动

## 四、前端配置和启动

### 1. 安装依赖
```bash
cd frontend
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

前端服务将在 **http://localhost:3000** 启动

## 五、访问系统

### 默认登录账户
- **用户名**: admin
- **密码**: admin123
- **用户类型**: 管理员 (1)

### 访问地址
- 开发环境：http://localhost:3000
- 后端 API: http://localhost:8080/api

## 六、测试功能

1. **登录系统** - 使用默认账户登录
2. **会员管理** - 新增、编辑、删除会员
3. **教练管理** - 管理教练信息
4. **课程管理** - 创建课程、预约课程
5. **器材管理** - 管理健身器材

## 七、常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 确认数据库用户名密码正确
- 确认数据库 gym_db 已创建

### 2. Redis 连接失败
- 检查 Redis 服务是否启动
- 确认 Redis 端口 6379 可访问

### 3. 端口被占用
修改 `application.yml` 中的端口配置：
```yaml
server:
  port: 8080  # 修改为其他端口
```

### 4. 前端无法访问后端
- 确认后端服务已启动
- 检查 `frontend/vue.config.js` 中的代理配置
- 确认防火墙允许访问

## 八、项目结构

```
gym/
├── src/
│   ├── main/java/com/gym/
│   │   ├── config/          # 配置类
│   │   ├── controller/      # REST API 控制器
│   │   ├── dto/             # 数据传输对象
│   │   ├── entity/          # 实体类
│   │   ├── filter/          # JWT 过滤器
│   │   ├── mapper/          # 数据访问层
│   │   └── service/         # 业务逻辑层
│   └── main/resources/
│       ├── sql/init.sql     # 数据库初始化脚本
│       └── application.yml  # 配置文件
├── frontend/                # Vue.js 前端项目
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── router/          # 路由
│   │   ├── store/           # 状态管理
│   │   └── views/           # 页面组件
│   └── package.json
└── pom.xml                  # Maven 配置
```

## 九、技术栈

### 后端
- Spring Boot 2.7.18
- Spring Security 5.7.11
- MyBatis-Plus 3.5.3
- JWT (io.jsonwebtoken 0.9.1)
- MySQL 8.0
- Redis

### 前端
- Vue.js 2.6
- Element UI 2.15
- Axios 1.6
- Vue Router 3.5
- Vuex 3.6

## 十、API 接口

### 认证接口
- POST /api/auth/login - 用户登录
- GET /api/auth/profile - 获取用户信息

### 会员管理
- GET /api/member/page - 分页查询会员
- POST /api/member - 新增会员
- PUT /api/member - 更新会员
- DELETE /api/member/{id} - 删除会员

### 教练管理
- GET /api/coach/page - 分页查询教练
- POST /api/coach - 新增教练
- PUT /api/coach - 更新教练
- DELETE /api/coach/{id} - 删除教练

### 课程管理
- GET /api/course/page - 分页查询课程
- POST /api/course - 新增课程
- PUT /api/course - 更新课程
- DELETE /api/course/{id} - 删除课程
- POST /api/course/book/{courseId} - 预约课程

### 器材管理
- GET /api/equipment/page - 分页查询器材
- POST /api/equipment - 新增器材
- PUT /api/equipment - 更新器材
- DELETE /api/equipment/{id} - 删除器材

---

**祝您使用愉快！**
