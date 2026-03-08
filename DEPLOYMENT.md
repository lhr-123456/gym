# 健身房管理系统 - 部署说明

## 一、环境准备

### 1.1 软件环境要求
- **JDK**: 1.8 或以上版本
- **Maven**: 3.6 或以上版本
- **MySQL**: 8.0 或以上版本
- **Redis**: 5.0 或以上版本
- **Node.js**: 14.0 或以上版本
- **npm**: 6.0 或以上版本

### 1.2 检查环境
```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version

# 检查 MySQL 版本
mysql --version

# 检查 Redis 版本
redis-cli --version

# 检查 Node.js 版本
node -v

# 检查 npm 版本
npm -v
```

## 二、数据库配置

### 2.1 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS gym_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2.2 导入数据
方式一：使用命令行
```bash
mysql -u root -p gym_db < src/main/resources/sql/init.sql
```

方式二：使用 MySQL Workbench 或其他工具
1. 打开 SQL 脚本文件：`src/main/resources/sql/init.sql`
2. 执行脚本

### 2.3 创建数据库用户（可选）
```sql
CREATE USER 'gym_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON gym_db.* TO 'gym_user'@'localhost';
FLUSH PRIVILEGES;
```

## 三、Redis 配置

### 3.1 Windows 系统
1. 下载 Redis Windows 版本
2. 解压后运行：
```bash
redis-server.exe
```

### 3.2 Linux 系统
```bash
# 安装 Redis
sudo apt-get install redis-server  # Ubuntu/Debian
sudo yum install redis             # CentOS/RHEL

# 启动 Redis
sudo systemctl start redis

# 设置开机自启
sudo systemctl enable redis
```

### 3.3 验证 Redis
```bash
redis-cli ping
# 应返回：PONG
```

## 四、后端部署

### 4.1 修改配置文件
编辑 `src/main/resources/application.yml` 文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gym_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_mysql_password  # 修改为你的 MySQL 密码
  
  redis:
    host: localhost
    port: 6379
    password:  # 如果有密码请配置
```

### 4.2 编译打包
```bash
cd gym
mvn clean package -DskipTests
```

### 4.3 运行方式

#### 方式一：直接运行
```bash
mvn spring-boot:run
```

#### 方式二：运行 jar 包
```bash
java -jar target/gym-management-system-1.0.0.jar
```

#### 方式三：IDEA 中运行
1. 找到 `GymManagementApplication.java`
2. 右键 -> Run 'GymManagementApplication'

### 4.4 验证后端
访问：http://localhost:8080/api/auth/login
使用 Swagger UI（如配置）：http://localhost:8080/swagger-ui.html

## 五、前端部署

### 5.1 安装依赖
```bash
cd frontend
npm install
```

如果遇到网络问题，可以使用淘宝镜像：
```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### 5.2 修改代理配置
编辑 `vue.config.js` 文件，确保代理配置正确：

```javascript
devServer: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端服务地址
      changeOrigin: true,
      pathRewrite: {
        '^/api': '/api'
      }
    }
  }
}
```

### 5.3 开发环境运行
```bash
npm run dev
```

访问：http://localhost:3000

### 5.4 生产环境打包
```bash
npm run build
```

打包后的文件在 `frontend/dist` 目录，可以部署到 Nginx 或其他 Web 服务器。

## 六、Nginx 配置（生产环境）

### 6.1 安装 Nginx
```bash
# Ubuntu/Debian
sudo apt-get install nginx

# CentOS/RHEL
sudo yum install nginx
```

### 6.2 配置 Nginx
编辑 `/etc/nginx/nginx.conf` 或 `/etc/nginx/conf.d/gym.conf`：

```nginx
server {
    listen 80;
    server_name localhost;
    
    # 前端静态文件
    location / {
        root /path/to/gym/frontend/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端 API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

### 6.3 启动 Nginx
```bash
# 测试配置
sudo nginx -t

# 启动 Nginx
sudo systemctl start nginx

# 设置开机自启
sudo systemctl enable nginx
```

## 七、测试验证

### 7.1 登录测试
- 访问地址：http://localhost:3000（开发环境）或 http://localhost（生产环境）
- 默认账户：
  - 用户名：admin
  - 密码：admin123
  - 用户类型：管理员

### 7.2 功能测试
1. 会员管理：新增、编辑、删除会员
2. 教练管理：新增、编辑、删除教练
3. 课程管理：新增课程、预约课程
4. 器材管理：新增、编辑、删除器材

## 八、常见问题

### 8.1 数据库连接失败
- 检查 MySQL 服务是否启动
- 检查数据库用户名密码是否正确
- 检查数据库是否存在

### 8.2 Redis 连接失败
- 检查 Redis 服务是否启动
- 检查 Redis 端口是否正确（默认 6379）

### 8.3 前端无法访问后端
- 检查后端服务是否启动
- 检查前端代理配置是否正确
- 检查跨域配置

### 8.4 端口被占用
修改配置文件中的端口：
- 后端：修改 `application.yml` 中的 `server.port`
- 前端：修改 `vue.config.js` 中的 `devServer.port`

## 九、性能优化建议

### 9.1 数据库优化
1. 为常用查询字段添加索引
2. 定期清理过期数据
3. 使用连接池配置

### 9.2 Redis 优化
1. 配置持久化策略
2. 设置合理的过期时间
3. 使用 Redis 集群

### 9.3 前端优化
1. 开启 Gzip 压缩
2. 使用 CDN 加速静态资源
3. 开启浏览器缓存

## 十、安全建议

1. 修改默认密码
2. 配置 HTTPS
3. 定期备份数据库
4. 限制 API 访问频率
5. 启用防火墙

## 十一、技术支持

如遇到问题，请检查：
1. 日志文件：后端日志、前端控制台
2. 数据库连接状态
3. Redis 连接状态
4. 网络配置

---

**祝您部署成功！**
