# DataScope

智能数据管理和查询系统

## 最近更新
- **2025-03-24**: 修复了 Swagger UI 和 OpenAPI 文档访问认证问题，现在可以直接访问文档
- **2025-03-24**: 优化查询执行接口设计，遵循RESTful最佳实践，简化客户端API调用方式
- **2025-03-24**: 修复了Flyway数据库迁移冲突问题，解决了应用启动失败的问题

## 项目说明
DataScope是一个集成了多数据源管理、元数据提取、低代码集成和AI增强功能的数据管理系统。

## 功能特性

### 已实现功能 (60%)

#### 数据源管理
- ✅ 多数据源配置和管理
- ✅ 数据源连接测试
- ✅ 数据源状态监控
- ✅ 安全的数据源凭据管理

#### 元数据管理
- ✅ 自动元数据提取
- ✅ MySQL元数据支持
- ✅ PostgreSQL元数据支持
- ✅ 元数据存储和检索
- ✅ 元数据更新机制
- ✅ 一键刷新数据源元数据
- ✅ 表级元数据刷新

#### 数据预览
- ✅ 表数据采样预览
- ✅ 自定义排序和过滤
- ✅ 系统字段过滤
- ✅ 性能优化的数据加载

#### 数据导出
- ✅ 支持多种格式导出(CSV、Excel、JSON)
- ✅ 自定义列配置（选择、排序、格式化）
- ✅ 数据过滤条件配置
- ✅ 数据脱敏处理
- ✅ 导出历史记录和状态跟踪
- ✅ 大数据量分批处理
- ✅ 异步导出任务管理
- ✅ 导出结果文件下载

#### 系统功能
- ✅ JPA 审计功能 - 自动填充创建和更新时间、操作人
- ✅ 数据操作安全防护机制

### 开发中功能 (40%)

#### 数据质量管理
- ⏳ 质量规则引擎
- ⏳ 数据质量检查
- ⏳ 质量报告生成

#### 权限管理
- ⏳ 用户认证授权
- ⏳ 细粒度权限控制
- ⏳ 操作审计日志

#### 系统管理
- ⏳ 系统配置管理
- ⏳ 任务调度管理
- ⏳ 系统监控告警

## 技术栈
- Java 17
- Spring Boot 2.7.x
- MySQL 8.0
- Redis 6.x
- Maven 3.8+

## 快速开始

### 环境要求
- JDK 17
- Maven 3.8+
- MySQL 8.0
- Redis 6.x

### 配置说明

#### 1. 创建配置文件
在 `src/main/resources` 目录下创建 `application-secret.yml` 文件：

```yaml
spring:
  datasource:
    username: your_username
    password: your_password

datascope:
  security:
    jwt-secret: your_jwt_secret
```

注意：此文件包含敏感信息，已添加到 .gitignore

#### 2. 环境变量配置（可选）
如果不想使用配置文件，也可以使用环境变量：

```bash
# 数据库配置
export MYSQL_USERNAME=your_username
export MYSQL_PASSWORD=your_password

# Redis配置（可选）
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=your_password

# 安全配置
export JWT_SECRET=your_jwt_secret
```

### 构建运行
```bash
# 克隆项目
git clone https://github.com/your-username/datascope.git
cd datascope

# 编译打包
mvn clean package

# 运行应用
java -jar target/datascope-1.0.0-SNAPSHOT.jar
```

### 验证部署
访问 http://localhost:8082/api/actuator/health 检查应用状态

### API 文档
- Swagger UI: http://localhost:8082/api/swagger-ui/index.html
- OpenAPI 文档: http://localhost:8082/api/v3/api-docs

## 项目结构
```
datascope/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── datascope/
│   │   │           ├── app/        # 应用层
│   │   │           ├── domain/     # 领域层
│   │   │           ├── facade/     # 接口层
│   │   │           ├── infrastructure/  # 基础设施层
│   │   │           └── main/       # 启动模块
│   │   └── resources/
│   │       ├── application.yml     # 应用配置
│   │       ├── application-secret.yml  # 敏感配置（不提交到git）
│   │       └── logback-spring.xml  # 日志配置
│   └── test/                       # 测试代码
├── pom.xml                         # 项目依赖
├── README.md                       # 项目说明
└── CHANGELOG.md                    # 变更日志
```

## 开发指南
详见 [开发文档](docs/development.md)

## 测试
```bash
# 运行测试
mvn test

# 运行测试并生成覆盖率报告
mvn test jacoco:report
```

## 安全说明
1. 所有敏感配置必须放在 `application-secret.yml` 中
2. 禁止提交敏感配置到代码仓库
3. 生产环境必须使用强密码和安全的JWT密钥
4. 定期更新依赖包以修复安全漏洞

## 贡献指南
1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交变更 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证
MIT License - 详见 [LICENSE](LICENSE) 文件

## 数据库初始化说明

系统采用多层次数据库初始化策略：

1. **schema.sql**: 创建基础表结构，包括Flyway历史表
2. **Flyway迁移**: 执行版本化的数据库迁移脚本
3. **data.sql**: 初始化基础数据

这种设计解决了以下问题：
- 避免了Flyway和JPA之间的循环依赖问题
- 确保Flyway历史表在迁移前就已存在
- 在应用程序启动后执行Flyway迁移，避免启动失败

## 配置说明

主要配置项在`application.yml`文件中，包括：

- 数据库连接配置
- Flyway迁移配置
- Redis缓存配置
- 安全配置
- 日志配置

## 开发指南

请参考 [开发规范](docs-index/development-standards.md) 进行开发。