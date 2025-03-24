# Changelog

## [Unreleased]

### Added
- 实现数据源管理模块
  - 创建 `DataSource` 实体及相关模型
  - 实现 `DataSourceRepository` 数据访问层
  - 实现 `DataSourceService` 服务层
  - 添加完整单元测试

- 实现元数据管理模块
  - 创建 `MetadataExtractor` 接口定义元数据提取功能
  - 实现 `MySQLMetadataExtractor` 类提供MySQL元数据提取能力
  - 实现 `PostgreSQLMetadataExtractor` 类提供PostgreSQL元数据提取能力
  - 添加元数据相关模型类：`TableMetadata`、`ColumnMetadata`、`IndexMetadata`、`ColumnType`
  - 实现 `MetadataStorageService` 提供元数据存储能力
  - 添加完整单元测试

- 实现数据预览模块
  - 创建数据预览请求/响应模型
  - 实现 `DataPreviewService` 提供数据预览能力
  - 支持数据采样、排序、过滤等功能
  - 添加完整单元测试

- 添加数据源连接池配置类 `DataSourceConfig`，优化连接池参数
- 添加重试工具类 `RetryUtil`，支持可配置的重试机制
- 为 PostgreSQL 连接添加专门的连接池配置

### Changed
- 优化项目结构，采用领域驱动设计
- 统一代码风格和注释规范
- 完善异常处理机制
- 优化配置管理
  - 分离敏感配置到独立文件
  - 调整日志级别为生产环境标准
  - 关闭生产环境SQL日志和格式化

- 重构 `DataSourceServiceImpl`，使用连接池管理数据源连接
- 优化 `PostgreSQLMetadataExtractor` 的错误处理和重试机制
- 改进数据源连接的日志记录

### Deprecated
- 无

### Removed
- 移除默认的不安全配置
  - 移除默认数据库用户名和密码
  - 移除默认JWT密钥

### Fixed
- 修复MySQL元数据提取器中的SQL注入风险
- 优化数据预览性能
- 修复配置安全隐患
  - 修复数据库空密码配置
  - 修复JWT默认密钥问题

- 修复 PostgreSQL 连接失败问题
  - 添加连接重试机制
  - 优化连接池配置
  - 完善错误处理
  - 增加详细日志

- 修复元数据刷新功能
  - 添加 `MetadataFetchService` 接口及实现类
  - 修复 `MetadataController` 中的刷新方法，使其能够正确地重新获取并存储元数据
  - 解决数据预览时表元数据未找到的问题

### Security
- 增强SQL注入防护
- 添加参数验证
- 规范化异常处理
- 加强安全配置
  - 使用安全的JWT密钥
  - 配置数据库安全凭据
  - 添加application-secret.yml到.gitignore
  - 优化数据库连接配置