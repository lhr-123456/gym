@echo off
echo Importing database schema...
mysql -u root -p123456 gym_db < src\main\resources\sql\init_en.sql
if %errorlevel% equ 0 (
    echo Database initialization completed successfully!
) else (
    echo Database initialization failed!
    pause
)
