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

# 检查配置文件是否存在
if [ ! -f "../src/main/resources/application-local.yml" ]; then
    print_message $RED "错误: application-local.yml 不存在"
    print_message $YELLOW "请先运行 ./init-dev.sh 初始化开发环境"
    exit 1
fi

# 检查MySQL服务是否运行
if ! mysql -u root -e "SELECT 1" &>/dev/null; then
    print_message $RED "错误: MySQL服务未运行"
    exit 1
fi

# 检查Redis服务是否运行
if ! redis-cli ping &>/dev/null; then
    print_message $RED "错误: Redis服务未运行"
    exit 1
fi

# 启动应用
print_message $YELLOW "正在启动应用..."
cd ..
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 检查启动结果
if [ $? -eq 0 ]; then
    print_message $GREEN "应用启动成功!"
    print_message $YELLOW "Swagger UI: http://localhost:8080/api/swagger-ui.html"
    print_message $YELLOW "API文档: http://localhost:8080/api/v3/api-docs"
else
    print_message $RED "应用启动失败"
    exit 1
fi