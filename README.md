# DataScope

DataScope是一个智能数据管理和查询系统，提供数据源管理、元数据提取、智能查询等功能。

## 功能特性

- 数据源管理
  - 支持MySQL、DB2数据源
  - 自动元数据提取
  - 增量更新机制

- 智能查询
  - SQL查询
  - 自然语言转SQL
  - 查询历史记录
  - 查询收藏

- 低代码集成
  - 界面配置
  - 显示规则
  - 操作按钮
  - 数据掩码

## 技术栈

- Java 11
- Spring Boot 2.7
- MyBatis 3.5
- MySQL 8.0
- Redis 7.2
- OpenAPI 3.0

## 项目结构

```
datascope/
├── datascope-api        // API接口层
├── datascope-app        // 应用服务层
├── datascope-common     // 公共工具类
├── datascope-domain     // 领域模型层
├── datascope-facade     // 外部接口层
├── datascope-infrastructure  // 基础设施层
└── datascope-main      // 应用程序入口
```

### 模块说明

- datascope-api: 提供RESTful API接口，包含接口定义、参数校验、响应封装等
- datascope-app: 实现核心业务逻辑，包含服务编排、事务处理等
- datascope-common: 提供公共工具类、常量定义、异常处理等
- datascope-domain: 定义领域模型、聚合根、领域服务等
- datascope-facade: 定义外部服务接口、DTO对象等
- datascope-infrastructure: 实现持久化、缓存、消息等基础设施
- datascope-main: 提供应用启动入口、配置加载等

## 快速开始

### 环境要求

- JDK 11+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+

### 构建

```bash
mvn clean package
```

### 运行

```bash
java -jar datascope-main/target/datascope-main-1.0.0-SNAPSHOT.jar
```

### 配置

主要配置项在 `application.yml` 中，包括：

- 数据库连接
- Redis连接
- 安全配置
- 查询限制
- API限流

## API文档

启动应用后访问：http://localhost:8080/swagger-ui.html

### API模块

- 数据源管理API
  - 数据源CRUD
  - 元数据同步
  - 连接测试

- 查询管理API
  - SQL执行
  - 查询历史
  - 收藏管理
  - 执行统计

- 低代码配置API
  - 界面配置
  - 显示规则
  - 操作按钮
  - 数据掩码

## 开发指南

详见 [docs/design.md](docs/design.md) 和 [docs/user-stories.md](docs/user-stories.md)

## 许可证

MIT License