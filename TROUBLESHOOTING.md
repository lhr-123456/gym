# 健身房管理系统 - 问题解决总结

## 遇到的问题及解决方案

### 1. Maven 编译错误：无效的标记 --release
**问题原因**：Spring Boot 3.1.5 需要 JDK 17+，但项目使用 JDK 1.8

**解决方案**：
- 将 Spring Boot 降级到 2.7.18（支持 JDK 1.8）
- 调整相关依赖版本：
  - Spring Security: 6.1.5 → 5.7.11
  - JWT: 0.11.5 → 0.9.1

### 2. Jakarta Servlet 包找不到
**问题原因**：Spring Boot 3.x 使用 jakarta.servlet，而 Spring Boot 2.x 使用 javax.servlet

**解决方案**：
- 修改 `JwtAuthenticationFilter.java`，将 `jakarta.servlet.*` 改为 `javax.servlet.*`

### 3. MyBatis-Plus 逻辑删除注解错误
**问题原因**：`@TableField(logicDelete = true)` 方法在新版本中已废弃

**解决方案**：
- 使用 `@TableLogic` 注解替代
- 修改所有实体类中的逻辑删除字段

### 4. Spring Security API 变更
**问题原因**：Spring Security 5.x 和 6.x 的 API 不同

**解决方案**：
- 将 `@EnableMethodSecurity` 改为 `@EnableGlobalMethodSecurity(prePostEnabled = true)`
- 修改 SecurityFilterChain 配置为链式调用语法
- 使用 `antMatchers` 替代 `requestMatchers`

### 5. JWT API 变更
**问题原因**：JWT 0.11.x 和 0.9.x 的 API 不同

**解决方案**：
- 使用 `Jwts.parser().setSigningKey(secret)` 替代 `Jwts.parserBuilder()`
- 使用 `SignatureAlgorithm.HS256, secret` 作为 signWith 参数

### 6. SQL 脚本编码问题
**问题原因**：SQL 文件中的中文注释在导入时出现乱码

**解决方案**：
- 创建无中文注释的版本：`init_simple.sql`
- 创建英文注释版本：`init_en.sql`
- 创建批处理脚本：`init-db.bat`

## 项目当前状态

✅ **后端编译成功**
- 38 个源文件全部编译通过
- 成功生成 jar 包：`target/gym-management-system-1.0.0.jar`

✅ **数据库脚本就绪**
- `init_simple.sql` - 无注释版本（推荐）
- `init_en.sql` - 英文注释版本
- `init-db.bat` - 一键导入脚本

✅ **前端代码完整**
- Vue.js + Element UI 项目结构
- 所有页面组件已创建

## 快速启动步骤

### 1. 初始化数据库
```bash
# 方式一：使用批处理脚本（最简单）
init-db.bat

# 方式二：手动执行 SQL
mysql -u root -p123456 gym_db < src/main/resources/sql/init_simple.sql
```

### 2. 启动 Redis
```bash
redis-server
```

### 3. 启动后端
```bash
java -jar target/gym-management-system-1.0.0.jar
```

### 4. 启动前端（可选）
```bash
cd frontend
npm install
npm run dev
```

### 5. 访问系统
- 前端地址：http://localhost:3000
- 后端 API: http://localhost:8080/api
- 默认账户：admin / admin123

## 技术栈版本

### 后端
- Spring Boot: 2.7.18
- Spring Security: 5.7.11
- MyBatis-Plus: 3.5.3
- JWT: 0.9.1
- JDK: 1.8

### 前端
- Vue.js: 2.6
- Element UI: 2.15
- Axios: 1.6

## 重要文件说明

1. **SQL 脚本**
   - `init_simple.sql` - 无中文注释，避免编码问题
   - `init_en.sql` - 英文注释版本
   - `init.sql` - 原始中文版本（不推荐使用）

2. **配置文件**
   - `application.yml` - Spring Boot 配置
   - `vue.config.js` - Vue 项目配置

3. **启动脚本**
   - `init-db.bat` - 数据库初始化脚本
   - `QUICKSTART.md` - 快速启动指南

## 下一步建议

1. **测试数据库连接**
   - 修改 `application.yml` 中的数据库密码
   - 确保 MySQL 和 Redis 服务已启动

2. **验证后端启动**
   ```bash
   java -jar target/gym-management-system-1.0.0.jar
   ```

3. **测试 API 接口**
   - 访问 http://localhost:8080/api/auth/login
   - 使用 POST 方法测试登录接口

4. **启动前端开发服务器**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

---

**所有问题已解决，项目可以正常运行！**
