# 健身房管理系统 - 无 Redis 启动指南

## 说明

Redis 在本项目中是**可选的**，主要用于：
- 缓存热点数据（提升性能）
- 会话管理
- 未来扩展功能

**核心功能（认证、会员管理、课程管理等）不需要 Redis 也能正常运行。**

---

## 快速启动（不使用 Redis）

### 步骤 1：初始化数据库

双击运行：
```bash
init-db.bat
```

或者手动执行：
```bash
mysql -u root -p123456 gym_db < src/main/resources/sql/init_simple.sql
```

### 步骤 2：启动后端

双击运行：
```bash
start.bat
```

或者手动执行：
```bash
java -jar target/gym-management-system-1.0.0.jar
```

### 步骤 3：访问系统

打开浏览器访问：
```
http://localhost:8080/api/auth/login
```

使用以下账户登录：
- **用户名**: admin
- **密码**: admin123
- **用户类型**: 管理员

---

## 配置文件说明

项目包含两个配置文件：

### 1. application.yml（默认）
包含 Redis 配置，适用于已安装 Redis 的环境。

### 2. application-no-redis.yml
不包含 Redis 配置，适用于没有 Redis 的环境。

**使用方法**：
```bash
# 使用无 Redis 配置启动
java -jar -Dspring.profiles.active=no-redis target/gym-management-system-1.0.0.jar
```

或者修改 `application.yml`，注释掉 Redis 配置部分。

---

## 功能测试清单

启动项目后，可以测试以下功能：

### ✅ 认证功能
- [x] 用户登录
- [x] JWT 令牌生成和验证
- [x] 用户信息获取

### ✅ 会员管理
- [x] 会员列表查询
- [x] 新增会员
- [x] 编辑会员
- [x] 删除会员

### ✅ 教练管理
- [x] 教练列表查询
- [x] 新增教练
- [x] 编辑教练
- [x] 删除教练

### ✅ 课程管理
- [x] 课程列表查询
- [x] 新增课程
- [x] 编辑课程
- [x] 删除课程
- [x] 课程预约

### ✅ 器材管理
- [x] 器材列表查询
- [x] 新增器材
- [x] 编辑器材
- [x] 删除器材

---

## 常见问题

### 1. 启动时提示 Redis 连接失败

这是正常的，如果您没有安装 Redis，可以忽略这个警告。项目会继续启动。

如果启动失败，请检查：
- 数据库是否已正确初始化
- MySQL 服务是否运行
- 数据库用户名密码是否正确

### 2. 如何知道 Redis 是否在工作？

查看启动日志：
- 如果看到 `Connected to Redis`，说明 Redis 已连接
- 如果看到 `Redis not configured` 或类似警告，说明 Redis 未启用（这是正常的）

### 3. 没有 Redis 会影响性能吗？

会有一定影响，但对于开发和测试环境来说影响不大。Redis 主要用于：
- 缓存频繁查询的数据
- 减少数据库压力

在生产环境中建议安装 Redis。

---

## 何时需要安装 Redis？

建议在以下情况安装 Redis：

1. **性能测试**：需要测试系统在高并发下的性能
2. **生产部署**：正式上线运行
3. **完整功能测试**：需要测试所有缓存和会话管理功能
4. **学习 Redis 集成**：想了解 Spring Boot 如何集成 Redis

---

## 安装 Redis（可选）

如果您决定安装 Redis，请参考 `REDIS-INSTALL.md` 文档。

**最简单的方式（使用 Docker）**：
```bash
docker run -d -p 6379:6379 --name gym-redis redis:latest
```

然后修改 `application.yml`，取消 Redis 配置的注释即可。

---

## 下一步

1. **测试所有功能**：确保所有 CRUD 操作正常
2. **开发前端**：启动前端开发服务器
3. **学习代码**：研究项目结构和代码实现
4. **扩展功能**：根据需求添加新功能

---

**祝您使用愉快！如有问题，请查看 TROUBLESHOOTING.md 文档。**
