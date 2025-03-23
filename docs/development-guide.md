# DataScope 开发指南

## 开发环境设置

### 必需组件
- JDK 17
- Maven 3.8+
- MySQL 8.0
- Redis 6.x
- Git

### IDE 推荐配置
- IntelliJ IDEA
- 推荐插件：
  - Lombok
  - MapStruct Support
  - SonarLint
  - Spring Boot Assistant
  - JPA Buddy

### 本地环境配置

1. 克隆项目
```bash
git clone https://github.com/your-username/datascope.git
cd datascope
```

2. 使用快速启动脚本
```bash
# 进入脚本目录
cd scripts

# 初始化开发环境（创建数据库、安装依赖等）
./init-dev.sh

# 编辑本地配置
vim ../src/main/resources/application-local.yml

# 启动应用
./start-dev.sh
```

3. 手动配置（如果不使用脚本）
```bash
# 复制本地配置模板
cp src/main/resources/application-local.yml.template src/main/resources/application-local.yml

# 编辑本地配置
vim src/main/resources/application-local.yml

# 创建数据库
mysql -u root -p
CREATE DATABASE datascope CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 使用开发配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

### 开发流程

1. 分支管理
- master: 主分支，保护分支
- develop: 开发分支
- feature/*: 功能分支
- bugfix/*: 缺陷修复分支
- release/*: 发布分支

2. 提交规范
```
<type>(<scope>): <subject>

<body>

<footer>
```

提交类型(type):
- feat: 新功能
- fix: 缺陷修复
- docs: 文档更新
- style: 代码格式化
- refactor: 代码重构
- test: 测试相关
- chore: 构建/工具链/辅助工具

3. 代码规范
- 遵循阿里巴巴Java开发手册
- 使用 EditorConfig 保持代码风格一致
- 提交前格式化代码
- 保持测试覆盖率

4. 测试规范
- 单元测试必须
- 集成测试推荐
- 提交前运行测试

### 常用命令

1. 构建项目
```bash
mvn clean package
```

2. 运行测试
```bash
mvn test
```

3. 生成测试报告
```bash
mvn test jacoco:report
```

4. 本地运行
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,local
```

### API文档

- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI文档: http://localhost:8080/api/v3/api-docs

### 常见问题

1. 数据库连接失败
- 检查MySQL服务是否启动
- 验证用户名密码是否正确
- 确认数据库是否创建

2. Redis连接失败
- 检查Redis服务是否启动
- 验证密码是否正确
- 确认端口是否正确

3. 应用启动失败
- 检查配置文件是否正确
- 查看日志文件排查问题
- 确认端口是否被占用

### 联系方式

- 技术支持：support@datascope.com
- 问题反馈：issues@datascope.com