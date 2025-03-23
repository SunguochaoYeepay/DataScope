# DataScope 开发环境搭建指南

## 1. 系统要求

### 1.1 硬件要求
```yaml
hardware_requirements:
  cpu: 
    minimum: "4核"
    recommended: "8核"
  memory:
    minimum: "8GB"
    recommended: "16GB"
  disk:
    minimum: "256GB SSD"
    recommended: "512GB SSD"
```

### 1.2 软件要求
```yaml
software_requirements:
  os:
    - name: "macOS"
      version: ">=10.15"
    - name: "Windows"
      version: ">=10"
    - name: "Ubuntu"
      version: ">=20.04"
      
  development_tools:
    - name: "JDK"
      version: "17"
    - name: "Maven"
      version: ">=3.8"
    - name: "Node.js"
      version: ">=16"
    - name: "npm"
      version: ">=8"
    - name: "Git"
      version: ">=2.30"
      
  ide:
    recommended: "IntelliJ IDEA Ultimate"
    version: ">=2023.1"
    plugins:
      - "Lombok"
      - "SonarLint"
      - "CheckStyle"
      - "Spring Boot Assistant"
```

## 2. 开发工具安装

### 2.1 macOS环境配置
```bash
# 安装Homebrew
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 安装必要工具
brew install openjdk@17
brew install maven
brew install node
brew install git
brew install docker

# 配置Java环境变量
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc

# 验证安装
java -version
mvn -version
node -v
npm -v
git --version
docker --version
```

### 2.2 Windows环境配置
```powershell
# 安装Chocolatey包管理器
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))

# 安装必要工具
choco install openjdk17
choco install maven
choco install nodejs
choco install git
choco install docker-desktop

# 配置Java环境变量
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-17", "Machine")
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";%JAVA_HOME%\bin", "Machine")

# 验证安装
java -version
mvn -version
node -v
npm -v
git --version
docker --version
```

### 2.3 Ubuntu环境配置
```bash
# 更新包管理器
sudo apt update
sudo apt upgrade

# 安装JDK 17
sudo apt install openjdk-17-jdk

# 安装Maven
sudo apt install maven

# 安装Node.js和npm
curl -fsSL https://deb.nodesource.com/setup_16.x | sudo -E bash -
sudo apt install nodejs

# 安装Git
sudo apt install git

# 安装Docker
sudo apt install docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# 验证安装
java -version
mvn -version
node -v
npm -v
git --version
docker --version
```

## 3. 项目配置

### 3.1 克隆项目
```bash
# 克隆主仓库
git clone https://github.com/your-org/datascope.git
cd datascope

# 初始化子模块
git submodule update --init --recursive
```

### 3.2 IDE配置
```yaml
intellij_settings:
  # 代码风格配置
  code_style:
    java:
      tab_size: 4
      indent: 4
      continuation_indent: 8
    typescript:
      tab_size: 2
      indent: 2
      
  # 插件配置
  plugins:
    lombok:
      enabled: true
      annotations_processing: true
    
    checkstyle:
      config_file: "config/checkstyle/checkstyle.xml"
      
    sonarlint:
      enabled: true
      
  # 运行配置
  run_configurations:
    backend:
      main_class: "com.datascope.DataScopeApplication"
      vm_options: "-Xms512m -Xmx2g"
      env_variables:
        SPRING_PROFILES_ACTIVE: "local"
        
    frontend:
      npm_script: "dev"
      env_variables:
        NODE_ENV: "development"
```

### 3.3 数据库配置
```yaml
# Docker Compose配置
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: datascope
      MYSQL_USER: datascope
      MYSQL_PASSWORD: datascope
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

  redis:
    image: redis:6.2
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  mysql_data:
  redis_data:
```

### 3.4 项目依赖安装
```bash
# 后端依赖安装
mvn clean install

# 前端依赖安装
cd frontend
npm install
```

## 4. 本地开发配置

### 4.1 环境变量配置
```bash
# 创建本地环境变量文件
cat > .env.local << EOF
# 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=datascope
DB_USER=datascope
DB_PASSWORD=datascope

# Redis配置
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT配置
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400

# 其他配置
API_BASE_URL=http://localhost:8080
CORS_ALLOWED_ORIGINS=http://localhost:3000
EOF
```

### 4.2 开发服务器配置
```yaml
# application-local.yml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
    
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
      
logging:
  level:
    root: INFO
    com.datascope: DEBUG
```

### 4.3 开发工具配置
```yaml
# .editorconfig
root = true

[*]
end_of_line = lf
insert_final_newline = true
charset = utf-8
trim_trailing_whitespace = true

[*.{java,xml}]
indent_style = space
indent_size = 4

[*.{js,ts,vue,json,yml}]
indent_style = space
indent_size = 2
```

## 5. 启动服务

### 5.1 启动后端服务
```bash
# 启动数据库和Redis
docker-compose up -d

# 启动Spring Boot应用
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 5.2 启动前端服务
```bash
# 进入前端目录
cd frontend

# 启动开发服务器
npm run dev
```

## 6. 开发工具配置

### 6.1 Git配置
```bash
# 配置Git用户信息
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"

# 配置Git提交模板
cat > ~/.gitmessage << EOF
feat/fix/docs/style/refactor/test/chore: 简短的描述

问题描述:
- 

解决方案:
- 

影响范围:
- 

Closes #issue编号
EOF

git config --global commit.template ~/.gitmessage
```

### 6.2 Maven配置
```xml
<!-- settings.xml -->
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <name>Aliyun Maven Mirror</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
  
  <profiles>
    <profile>
      <id>datascope</id>
      <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
      </properties>
    </profile>
  </profiles>
</settings>
```

### 6.3 NPM配置
```bash
# 配置NPM镜像
npm config set registry https://registry.npmmirror.com

# 配置项目级别的NPM脚本
cat > package.json << EOF
{
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview",
    "lint": "eslint . --ext .vue,.js,.jsx,.cjs,.mjs,.ts,.tsx,.cts,.mts --fix",
    "format": "prettier --write src/",
    "test": "vitest",
    "coverage": "vitest run --coverage"
  }
}
EOF
```

## 7. 常见问题解决

### 7.1 环境问题
```yaml
common_issues:
  java_version:
    problem: "Java版本不匹配"
    solution: "确保使用JDK 17，运行 'java -version' 验证"
    
  maven_dependencies:
    problem: "Maven依赖下载失败"
    solution: "检查Maven镜像配置，尝试使用 'mvn clean install -U' 强制更新"
    
  node_modules:
    problem: "前端依赖安装失败"
    solution: "删除 node_modules 目录和 package-lock.json，重新运行 'npm install'"
    
  docker:
    problem: "Docker服务无法启动"
    solution: "检查Docker服务状态，确保用户在docker组中"
```

### 7.2 IDE问题
```yaml
ide_issues:
  lombok:
    problem: "Lombok注解不生效"
    solution: "安装Lombok插件，启用注解处理"
    
  hot_reload:
    problem: "热重载不工作"
    solution: "确保开启自动构建，配置正确的运行配置"
    
  indexing:
    problem: "IDE索引卡住"
    solution: "清除IDE缓存，重新构建项目索引"
```

## 8. 开发环境检查清单

### 8.1 环境检查
```yaml
environment_checklist:
  system:
    - JDK 17已安装并配置
    - Maven已安装并配置
    - Node.js已安装并配置
    - Git已安装并配置
    - Docker已安装并运行
    
  ide:
    - 必要插件已安装
    - 代码风格已配置
    - 运行配置已设置
    
  project:
    - 源码已克隆
    - 依赖已安装
    - 数据库已启动
    - 环境变量已配置
```

### 8.2 功能验证
```yaml
functionality_checklist:
  backend:
    - 应用能够启动
    - 数据库连接正常
    - Redis连接正常
    - API能够访问
    
  frontend:
    - 开发服务器能启动
    - 页面能正常访问
    - API调用正常
    - 热重载正常工作
```