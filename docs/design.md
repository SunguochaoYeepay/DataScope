# DataScope 系统实现方案

## 1. 项目架构

采用多模块Maven项目结构：

```
datascope/
├── pom.xml
├── docs/
├── datascope-app/
├── datascope-domain/
├── datascope-facade/
├── datascope-infrastructure/
├── datascope-main/
└── datascope-common/
```

### 核心依赖版本
- Spring Boot: 2.7.x
- MyBatis: 3.5.x
- MySQL Connector: 8.0.x
- DB2 JDBC: 11.5.x
- Redis: 7.x
- OpenAPI(Swagger): 3.0.x
- MapStruct: 1.5.x
- Lombok: 1.18.x

## 2. 核心领域模型

### 2.1 聚合根
1. DataSource（数据源）
   - 管理数据源配置信息
   - 维护数据源连接状态
   - 协调元数据同步

2. MetaData（元数据）
   - 管理数据库结构信息
   - 维护表关系
   - 处理增量更新

3. Query（查询）
   - 管理SQL查询
   - 处理自然语言转换
   - 维护查询历史

4. LowCodeConfig（低代码配置）
   - 管理界面配置
   - 处理显示规则
   - 维护用户偏好

### 2.2 实体
1. Schema（数据库模式）
   - 名称
   - 字符集
   - 关联的表集合

2. Table（表）
   - 名称
   - 注释
   - 列集合
   - 索引集合

3. Column（列）
   - 名称
   - 数据类型
   - 约束
   - 注释

4. Relationship（关系）
   - 关系类型
   - 源表/列
   - 目标表/列
   - 置信度

5. QueryHistory（查询历史）
   - SQL
   - 参数
   - 执行时间
   - 执行状态

6. DisplayConfig（显示配置）
   - 显示类型
   - 格式化规则
   - 掩码规则
   - 操作配置

### 2.3 值对象
1. DataSourceType（数据源类型）
   - MYSQL
   - DB2

2. ColumnType（列类型）
   - 数值类型
   - 字符串类型
   - 日期时间类型
   - 布尔类型

3. RelationType（关系类型）
   - 外键关系
   - 推断关系
   - 手动定义关系

4. SecurityLevel（安全级别）
   - 普通
   - 敏感
   - 高度敏感

5. DisplayType（显示类型）
   - 文本框
   - 下拉框
   - 日期选择器
   - 数字输入框

## 3. 核心功能模块

### 3.1 数据源管理模块
```java
public interface DataSourceService {
    String addDataSource(DataSourceDTO source);
    void updateDataSource(DataSourceDTO source);
    void removeDataSource(String sourceId);
    DataSourceDTO getDataSource(String sourceId);
    List<DataSourceDTO> listDataSources(DataSourceQuery query);
}
```

### 3.2 元数据管理模块
```java
public interface MetaDataService {
    void syncMetaData(String sourceId);
    void incrementalSync(String sourceId);
    List<SchemaDTO> listSchemas(String sourceId);
    List<TableDTO> listTables(String schemaId);
    List<ColumnDTO> listColumns(String tableId);
}
```

### 3.3 查询管理模块
```java
public interface QueryService {
    String createQuery(QueryDTO query);
    void updateQuery(QueryDTO query);
    QueryResult executeQuery(String queryId, Map<String, Object> params);
    String nlToSql(String naturalLanguage, String sourceId);
    void saveQueryHistory(QueryHistoryDTO history);
}
```

### 3.4 低代码配置模块
```java
public interface LowCodeConfigService {
    String createConfig(LowCodeConfigDTO config);
    void updateConfig(LowCodeConfigDTO config);
    LowCodeConfigDTO getConfig(String configId);
    DisplayConfig getUserDisplayPreference(String userId);
}
```

## 4. 数据库设计

### 4.1 主要表结构
```sql
-- 数据源表
CREATE TABLE tbl_data_source (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100),
    type VARCHAR(20),
    host VARCHAR(200),
    port INT,
    username VARCHAR(100),
    password_encrypted VARCHAR(500),
    salt VARCHAR(36),
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);

-- 元数据表
CREATE TABLE tbl_meta_schema (
    id VARCHAR(36) PRIMARY KEY,
    source_id VARCHAR(36),
    name VARCHAR(100),
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);

-- 表元数据
CREATE TABLE tbl_meta_table (
    id VARCHAR(36) PRIMARY KEY,
    schema_id VARCHAR(36),
    name VARCHAR(100),
    comment TEXT,
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);

-- 列元数据
CREATE TABLE tbl_meta_column (
    id VARCHAR(36) PRIMARY KEY,
    table_id VARCHAR(36),
    name VARCHAR(100),
    type VARCHAR(50),
    length INT,
    precision INT,
    scale INT,
    nullable BOOLEAN,
    comment TEXT,
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);

-- 表关系
CREATE TABLE tbl_relationship (
    id VARCHAR(36) PRIMARY KEY,
    source_table_id VARCHAR(36),
    source_column_id VARCHAR(36),
    target_table_id VARCHAR(36),
    target_column_id VARCHAR(36),
    type VARCHAR(20),
    confidence DECIMAL(5,2),
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);

-- 查询配置
CREATE TABLE tbl_query (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100),
    source_id VARCHAR(36),
    sql_text TEXT,
    version INT,
    status VARCHAR(20),
    timeout INT,
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);

-- 查询历史
CREATE TABLE tbl_query_history (
    id VARCHAR(36) PRIMARY KEY,
    query_id VARCHAR(36),
    parameters TEXT,
    execution_time BIGINT,
    status VARCHAR(20),
    error_message TEXT,
    created_at DATETIME,
    created_by VARCHAR(36)
);

-- 低代码配置
CREATE TABLE tbl_lowcode_config (
    id VARCHAR(36) PRIMARY KEY,
    query_id VARCHAR(36),
    config_type VARCHAR(20),
    config_json TEXT,
    version INT,
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);

-- 显示配置
CREATE TABLE tbl_display_config (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    column_id VARCHAR(36),
    display_type VARCHAR(20),
    format_rule TEXT,
    mask_rule TEXT,
    nonce INT,
    created_at DATETIME,
    created_by VARCHAR(36),
    modified_at DATETIME,
    modified_by VARCHAR(36)
);
```

## 5. API设计

### 5.1 RESTful API
```yaml
openapi: 3.0.0
paths:
  /api/v1/datasources:
    post:
      summary: 创建数据源
    get:
      summary: 查询数据源列表
  /api/v1/queries:
    post:
      summary: 创建查询
    get:
      summary: 执行查询
```

## 6. 扩展性设计

### 6.1 数据源适配器
```java
public interface DataSourceAdapter {
    MetaData extractMetaData();
    QueryResult executeQuery(String sql, Map<String, Object> params);
}
```

### 6.2 显示适配器
```java
public interface DisplayAdapter {
    DisplayConfig convert(ColumnType columnType);
    String render(Object value, DisplayConfig config);
}
```

## 7. 安全设计

1. 密码加密：AES + 随机盐
   - 每个数据源使用独立的盐值
   - 密钥定期轮换机制
   - 加密算法可配置

2. API访问限流：Redis + Token Bucket
   - 用户级别限流
   - 接口级别限流
   - 可配置限流规则

3. 查询超时控制：线程池 + Future
   - 默认30秒超时
   - 可配置超时时间
   - 优雅降级机制

4. 敏感数据掩码：注解 + AOP
   - 支持多种掩码规则
   - 基于注解配置
   - 动态掩码策略

## 8. 性能优化

1. 元数据缓存：Redis二级缓存
   - 热点数据缓存
   - 缓存自动更新
   - 缓存预热机制

2. 查询结果分页：默认每页50条
   - 游标分页
   - 支持排序
   - 总数估算

3. 大数据量导出：异步任务 + 进度反馈
   - 后台任务处理
   - 进度实时反馈
   - 结果文件管理

4. 关系推断缓存：Redis持久化
   - 增量更新
   - 定期优化
   - 手动干预

## 9. AI增强功能

1. 自然语言转SQL：OpenRouter API集成
   - 上下文理解
   - 错误提示
   - 结果优化

2. 智能表关系推断：基于查询历史的机器学习
   - 模式识别
   - 置信度计算
   - 持续学习

3. 显示配置推荐：基于用户历史行为
   - 个性化推荐
   - 场景适配
   - 动态调整

4. 查询条件智能排序：使用频率分析
   - 实时统计
   - 个性化排序
   - 场景识别

## 10. 监控和运维

1. 查询性能监控
   - 执行时间统计
   - 资源消耗监控
   - 慢查询分析

2. 数据源健康检查
   - 连接状态监控
   - 性能指标采集
   - 异常告警

3. 系统资源监控
   - CPU/内存监控
   - 磁盘使用监控
   - 网络流量监控

4. 操作审计日志
   - 用户操作记录
   - 系统变更记录
   - 异常事件记录

## 11. 部署要求

1. 环境要求
   - JDK 11+
   - MySQL 8.0+
   - Redis 7.0+
   - 最小2核4G内存

2. 配置要求
   - 支持配置文件外部化
   - 支持环境变量覆盖
   - 支持动态配置刷新

3. 扩展性要求
   - 支持水平扩展
   - 支持多实例部署
   - 支持容器化部署