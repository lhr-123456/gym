# Redis 安装指南

## 为什么需要 Redis？

Redis 在本项目中用于：
- 缓存热点数据（提高查询性能）
- 会话管理
- 分布式锁（未来扩展）

**注意**：Redis 是可选的，项目核心功能（认证、CRUD）没有 Redis 也能运行。

---

## 安装方式

### 方式一：使用 Docker（推荐 ⭐⭐⭐⭐⭐）

**优点**：简单、快速、干净

```bash
# 拉取 Redis 镜像
docker pull redis:latest

# 启动 Redis 容器
docker run -d -p 6379:6379 --name gym-redis redis:latest

# 验证 Redis 是否运行
docker ps | grep gym-redis

# 测试 Redis 连接
docker exec -it gym-redis redis-cli ping
# 应返回：PONG
```

**停止 Redis**：
```bash
docker stop gym-redis
```

**启动 Redis**：
```bash
docker start gym-redis
```

---

### 方式二：使用 WSL（Windows Subsystem for Linux）

**优点**：原生 Linux 环境，性能较好

1. **安装 WSL**（如果尚未安装）：
   ```powershell
   wsl --install
   ```

2. **在 WSL 中安装 Redis**：
   ```bash
   # 打开 WSL
   wsl ubuntu
   
   # 更新包列表
   sudo apt update
   
   # 安装 Redis
   sudo apt install redis-server
   
   # 启动 Redis
   sudo service redis-server start
   
   # 验证
   redis-cli ping
   # 应返回：PONG
   ```

3. **配置 Redis 允许远程连接**（可选）：
   ```bash
   sudo nano /etc/redis/redis.conf
   # 找到 bind 127.0.0.1，改为 bind 0.0.0.0
   sudo service redis-server restart
   ```

---

### 方式三：使用 Windows 版本 Redis

**优点**：原生 Windows 应用

1. **下载 Redis**：
   - 访问：https://github.com/microsoftarchive/redis/releases
   - 下载：`Redis-x64-3.0.504.msi`

2. **安装**：
   - 双击 MSI 文件
   - 按照向导完成安装
   - 默认安装路径：`C:\Program Files\Redis`

3. **启动 Redis**：
   - Redis 会自动作为 Windows 服务启动
   - 或者手动运行：`redis-server.exe`

4. **验证**：
   ```bash
   redis-cli ping
   # 应返回：PONG
   ```

---

### 方式四：使用 Chocolatey 包管理器

**优点**：命令行安装，方便管理

1. **安装 Chocolatey**（如果尚未安装）：
   ```powershell
   # 以管理员身份运行 PowerShell
   Set-ExecutionPolicy Bypass -Scope Process -Force
   [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
   iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
   ```

2. **安装 Redis**：
   ```powershell
   choco install redis-64 -y
   ```

3. **启动 Redis**：
   ```powershell
   redis-server
   ```

---

## 测试 Redis 连接

安装完成后，测试 Redis 是否正常工作：

```bash
# 方式一：使用 redis-cli
redis-cli ping
# 应返回：PONG

# 方式二：设置和获取键
redis-cli set test "hello"
redis-cli get test
# 应返回："hello"
```

---

## 常见问题

### 1. Redis 服务无法启动

**检查端口是否被占用**：
```bash
netstat -ano | findstr :6379
```

**解决方案**：
- 停止占用端口的程序
- 或者修改 Redis 配置文件中的端口

### 2. 连接被拒绝

**检查防火墙**：
```bash
# Windows 防火墙
netsh advfirewall firewall add rule name="Redis" dir=in action=allow protocol=TCP localport=6379
```

### 3. Redis 密码认证

如果需要设置密码：

1. **编辑 Redis 配置文件**（redis.conf）：
   ```
   requirepass your_password
   ```

2. **修改项目配置**（application.yml）：
   ```yaml
   spring:
     redis:
       password: your_password
   ```

---

## 临时方案：不安装 Redis

如果您暂时不想安装 Redis，可以注释掉配置文件中的 Redis 配置：

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  # redis:
  #   host: localhost
  #   port: 6379
  #   password: 
  #   database: 0
```

**注意**：这样做可能会导致某些缓存功能不可用，但核心的认证和 CRUD 功能仍然可以正常工作。

---

## 推荐方案

对于本项目，**强烈推荐使用 Docker 方式**安装 Redis：

```bash
docker run -d -p 6379:6379 --name gym-redis redis:latest
```

简单、快速、无副作用，不需要时可以随时删除容器。

---

## 验证项目中的 Redis

安装 Redis 后，启动项目并验证：

```bash
# 启动项目
java -jar target/gym-management-system-1.0.0.jar

# 查看日志，应该看到类似信息：
# Connected to Redis at localhost:6379
```

---

**祝您安装顺利！**
