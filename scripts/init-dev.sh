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

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        print_message $RED "错误: 未找到命令 '$1'. 请先安装."
        exit 1
    fi
}

# 检查必要的命令
print_message $YELLOW "检查必要的命令..."
check_command "java"
check_command "mvn"
check_command "mysql"

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$JAVA_VERSION" != "17" ]; then
    print_message $RED "错误: 需要Java 17, 当前版本: $JAVA_VERSION"
    exit 1
fi

# 创建本地配置文件
print_message $YELLOW "创建本地配置文件..."
if [ ! -f "../src/main/resources/application-local.yml" ]; then
    cp ../src/main/resources/application-local.yml.template ../src/main/resources/application-local.yml
    print_message $GREEN "已创建 application-local.yml"
else
    print_message $YELLOW "application-local.yml 已存在，跳过创建"
fi

# 创建数据库
print_message $YELLOW "创建数据库..."
mysql -u root -e "CREATE DATABASE IF NOT EXISTS datascope CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if [ $? -eq 0 ]; then
    print_message $GREEN "数据库创建成功"
else
    print_message $RED "数据库创建失败"
    exit 1
fi

# 安装依赖
print_message $YELLOW "安装项目依赖..."
cd ..
mvn clean install -DskipTests
if [ $? -eq 0 ]; then
    print_message $GREEN "依赖安装成功"
else
    print_message $RED "依赖安装失败"
    exit 1
fi

print_message $GREEN "开发环境初始化完成!"
print_message $YELLOW "请确保:"
print_message $YELLOW "1. 检查并配置 application-local.yml 中的数据库连接信息"
print_message $YELLOW "2. 检查并配置 Redis 连接信息"
print_message $YELLOW "3. 运行 ./start-dev.sh 启动应用"