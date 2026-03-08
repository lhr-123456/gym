@echo off
echo ========================================
echo 健身房管理系统 - 快速启动
echo ========================================
echo.

echo [1/3] 检查数据库...
mysql -u root -p123456 -e "USE gym_db" >nul 2>&1
if %errorlevel% neq 0 (
    echo 数据库未初始化，正在初始化...
    mysql -u root -p123456 gym_db < src\main\resources\sql\init_simple.sql
    if %errorlevel% equ 0 (
        echo 数据库初始化成功！
    ) else (
        echo 数据库初始化失败！请检查 MySQL 是否运行。
        pause
        exit /b
    )
) else (
    echo 数据库已就绪。
)

echo.
echo [2/3] 启动后端服务...
echo 提示：Redis 是可选的，如需使用 Redis，请先安装并取消 application.yml 中的注释
echo.

cd /d "%~dp0"
java -jar target\gym-management-system-1.0.0.jar

pause
