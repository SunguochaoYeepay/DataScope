# DataScope 技术方案设计文档

## 1. 系统架构

### 1.1 整体架构
```
DataScope
├── 数据采集层
│   ├── SQL解析器
│   ├── 参数解析器
│   └── 执行计划分析器
├── 存储层
│   ├── 实时存储
│   └── 归档存储
├── 分析层
│   ├── 模式识别
│   ├── 性能分析
│   └── 趋势分析
└── 应用层
    ├── 查询历史
    ├── 性能统计
    └── 告警监控
```

### 1.2 技术栈选型
- 后端：Spring Boot + MyBatis
- 前端：Vue3 + Ant Design Vue
- 数据库：MySQL
- 缓存：Redis
- 消息队列：RocketMQ（可选）

## 2. 核心模块设计

### 2.1 SQL解析器
```java
public interface SQLParser {
    // 解析SQL语句
    SQLStatement parse(String sql);
    
    // 提取表信息
    List<TableInfo> extractTables(SQLStatement stmt);
    
    // 提取字段信息
    List<ColumnInfo> extractColumns(SQLStatement stmt);
    
    // 提取where条件
    List<Condition> extractConditions(SQLStatement stmt);
}
```

### 2.2 查询模式识别
```java
public interface QueryPatternAnalyzer {
    // 生成查询指纹
    String generateFingerprint(String sql);
    
    // 计算查询相似度
    double calculateSimilarity(String sql1, String sql2);
    
    // 识别查询模式
    QueryPattern identifyPattern(String sql);
}
```

### 2.3 性能分析器
```java
public interface PerformanceAnalyzer {
    // 分析执行计划
    ExecutionPlanAnalysis analyzeExecutionPlan(String sql);
    
    // 计算性能基线
    PerformanceBaseline calculateBaseline(String queryPattern);
    
    // 检测性能异常
    List<Anomaly> detectAnomalies(QueryExecution execution);
}
```

## 3. 数据模型设计

### 3.1 查询历史表
```sql
CREATE TABLE tbl_query_history (
    id VARCHAR(36) NOT NULL,
    query_id VARCHAR(36) NOT NULL,
    parameters TEXT,
    execution_time BIGINT,
    affected_rows BIGINT,
    status VARCHAR(20),
    error_message TEXT,
    execution_ip VARCHAR(50),
    created_at DATETIME,
    created_by VARCHAR(36),
    PRIMARY KEY (id),
    INDEX idx_query_id (query_id),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by)
);
```

### 3.2 查询模式表
```sql
CREATE TABLE tbl_query_pattern (
    id VARCHAR(36) NOT NULL,
    pattern_hash VARCHAR(64) NOT NULL,
    pattern_sql TEXT,
    first_seen DATETIME,
    last_seen DATETIME,
    occurrence_count BIGINT,
    avg_execution_time BIGINT,
    PRIMARY KEY (id),
    UNIQUE INDEX idx_pattern_hash (pattern_hash)
);
```

### 3.3 性能基线表
```sql
CREATE TABLE tbl_performance_baseline (
    id VARCHAR(36) NOT NULL,
    pattern_id VARCHAR(36) NOT NULL,
    p50_time BIGINT,
    p90_time BIGINT,
    p95_time BIGINT,
    p99_time BIGINT,
    sample_size BIGINT,
    updated_at DATETIME,
    PRIMARY KEY (id),
    INDEX idx_pattern_id (pattern_id)
);
```

## 4. 接口设计

### 4.1 查询历史接口
```java
@RestController
@RequestMapping("/api/v1/query-histories")
public class QueryHistoryController {
    // 分页查询历史
    @GetMapping("/user/{userId}")
    public Page<QueryHistory> getUserHistory(
        @PathVariable String userId,
        @RequestParam int page,
        @RequestParam int size
    );
    
    // 获取查询详情
    @GetMapping("/{queryId}")
    public QueryHistory getQueryDetail(@PathVariable String queryId);
    
    // 批量添加查询历史
    @PostMapping("/batch")
    public void batchAddHistory(@RequestBody List<QueryHistory> histories);
}
```

### 4.2 性能分析接口
```java
@RestController
@RequestMapping("/api/v1/performance")
public class PerformanceController {
    // 获取性能统计
    @GetMapping("/stats")
    public PerformanceStats getStats(
        @RequestParam LocalDateTime startTime,
        @RequestParam LocalDateTime endTime
    );
    
    // 获取慢查询列表
    @GetMapping("/slow-queries")
    public List<SlowQuery> getSlowQueries(
        @RequestParam LocalDateTime startTime,
        @RequestParam LocalDateTime endTime,
        @RequestParam long threshold
    );
}
```

## 5. 安全设计

### 5.1 访问控制
- 基于Spring Security实现认证授权
- 支持基于角色的访问控制(RBAC)
- JWT token认证

### 5.2 数据脱敏
- 支持配置化的脱敏规则
- 内置常用脱敏算法
- 支持自定义脱敏处理器

## 6. 性能优化

### 6.1 查询优化
- 索引优化
- 分页查询优化
- 大数据量查询优化

### 6.2 缓存策略
- 多级缓存
- 缓存预热
- 缓存失效策略

## 7. 可观测性

### 7.1 监控指标
- 系统级指标
- 应用级指标
- 业务级指标

### 7.2 日志规范
- 统一日志格式
- 分级日志策略
- 关键操作审计

## 8. 部署架构

### 8.1 部署拓扑
```
[负载均衡]
    │
    ├── [应用服务器1]
    ├── [应用服务器2]
    └── [应用服务器n]
        │
        ├── [主数据库]
        └── [从数据库]
```

### 8.2 扩展性设计
- 应用层水平扩展
- 数据库读写分离
- 分库分表预留