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

### Changed
- 优化项目结构，采用领域驱动设计
- 统一代码风格和注释规范
- 完善异常处理机制

### Deprecated
- 无

### Removed
- 无

### Fixed
- 修复MySQL元数据提取器中的SQL注入风险
- 优化数据预览性能

### Security
- 增强SQL注入防护
- 添加参数验证
- 规范化异常处理
- 升级MySQL Connector/J到8.3.0版本以修复安全漏洞
- 升级PostgreSQL JDBC Driver到42.7.5版本以修复安全漏洞