#!/bin/bash

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 创建测试数据库
print_message $YELLOW "创建测试数据库..."
mysql -u root << EOF
CREATE DATABASE IF NOT EXISTS datascope_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'test_user'@'localhost' IDENTIFIED BY 'test_pass';
GRANT ALL PRIVILEGES ON datascope_test.* TO 'test_user'@'localhost';
FLUSH PRIVILEGES;
EOF

if [ $? -eq 0 ]; then
    print_message $GREEN "测试数据库创建成功"
else
    print_message $RED "测试数据库创建失败"
    exit 1
fi

# 启动应用
print_message $YELLOW "启动测试环境..."
cd ..
mvn spring-boot:run -Dspring-boot.run.profiles=test

# 检查启动结果
if [ $? -eq 0 ]; then
    print_message $GREEN "测试环境启动成功!"
    print_message $YELLOW "Swagger UI: http://localhost:8081/api/swagger-ui.html"
    print_message $YELLOW "API文档: http://localhost:8081/api/v3/api-docs"
else
    print_message $RED "测试环境启动失败"
    exit 1
fi