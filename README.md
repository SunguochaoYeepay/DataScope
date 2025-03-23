# DataScope

智能数据管理和查询系统

## 项目说明
DataScope是一个集成了多数据源管理、元数据提取、低代码集成和AI增强功能的数据管理系统。

## 功能特性

### 已实现功能 (60%)

#### 数据源管理
- ✅ 多数据源配置和管理
- ✅ 数据源连接测试
- ✅ 数据源状态监控

#### 元数据管理
- ✅ 自动元数据提取
- ✅ MySQL元数据支持
- ✅ PostgreSQL元数据支持
- ✅ 元数据存储和检索
- ✅ 元数据更新机制

#### 数据预览
- ✅ 表数据采样预览
- ✅ 自定义排序和过滤
- ✅ 系统字段过滤
- ✅ 性能优化的数据加载

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

### 环境变量配置
```bash
# 数据库配置
export MYSQL_USERNAME=your_username
export MYSQL_PASSWORD=your_password

# Redis配置
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
访问 http://localhost:8080/api/actuator/health 检查应用状态

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

## 贡献指南
1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交变更 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 许可证
MIT License - 详见 [LICENSE](LICENSE) 文件