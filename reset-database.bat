@echo off
echo ========================================
echo 健身房管理系统 - 数据库初始化
echo ========================================
echo.
echo 管理员账户信息：
echo 用户名：admin
echo 密码：admin123
echo 用户类型：管理员 (1)
echo.

echo 正在删除旧数据库...
mysql -u root -p123456 -e "DROP DATABASE IF EXISTS gym_db;"

echo 正在创建数据库...
mysql -u root -p123456 -e "CREATE DATABASE gym_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo 正在导入数据...
mysql -u root -p123456 gym_db < src\main\resources\sql\init_simple.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 数据库初始化成功！
    echo ========================================
    echo.
    echo 现在可以启动项目：
    echo java -jar target\gym-management-system-1.0.0.jar
    echo.
) else (
    echo.
    echo 数据库初始化失败！请检查：
    echo 1. MySQL 服务是否运行
    echo 2. 用户名密码是否正确
    echo 3. 是否有足够的权限
    echo.
)

pause
