# 扩展性设计

## 1. 数据源适配器

### 1.1 适配器接口

```java
public interface DataSourceAdapter {
    /**
     * 创建数据源连接池
     */
    ConnectionPool createConnectionPool(DataSourceConfig config);
    
    /**
     * 获取元数据提取器
     */
    MetadataExtractor getMetadataExtractor();
    
    /**
     * 获取查询执行器
     */
    QueryExecutor getQueryExecutor();
    
    /**
     * 测试连接
     */
    boolean testConnection(DataSourceConfig config);
}

public interface MetadataExtractor {
    /**
     * 提取表信息
     */
    List<TableMetadata> extractTables(DataSource dataSource);
    
    /**
     * 提取列信息
     */
    List<ColumnMetadata> extractColumns(DataSource dataSource, String tableName);
    
    /**
     * 提取关系信息
     */
    List<RelationMetadata> extractRelations(DataSource dataSource);
}

public interface QueryExecutor {
    /**
     * 执行查询
     */
    QueryResult execute(String sql, Map<String, Object> params);
    
    /**
     * 验证SQL
     */
    boolean validateSql(String sql);
    
    /**
     * 获取执行计划
     */
    String getExecutionPlan(String sql);
}
```

### 1.2 MySQL适配器实现

```java
@Component
public class MySQLAdapter implements DataSourceAdapter {
    @Override
    public ConnectionPool createConnectionPool(DataSourceConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(buildJdbcUrl(config));
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        // 设置连接池参数
        return new HikariDataSource(hikariConfig);
    }
    
    @Override
    public MetadataExtractor getMetadataExtractor() {
        return new MySQLMetadataExtractor();
    }
    
    @Override
    public QueryExecutor getQueryExecutor() {
        return new MySQLQueryExecutor();
    }
}
```

### 1.3 DB2适配器实现

```java
@Component
public class DB2Adapter implements DataSourceAdapter {
    @Override
    public ConnectionPool createConnectionPool(DataSourceConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(buildJdbcUrl(config));
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        // 设置DB2特定的连接池参数
        return new HikariDataSource(hikariConfig);
    }
    
    @Override
    public MetadataExtractor getMetadataExtractor() {
        return new DB2MetadataExtractor();
    }
    
    @Override
    public QueryExecutor getQueryExecutor() {
        return new DB2QueryExecutor();
    }
}
```

## 2. 组件扩展

### 2.1 组件注册接口

```java
public interface ComponentRegistry {
    /**
     * 注册组件
     */
    void registerComponent(String type, ComponentDefinition definition);
    
    /**
     * 获取组件
     */
    ComponentDefinition getComponent(String type);
    
    /**
     * 检查组件是否支持特定数据类型
     */
    boolean supports(String dataType, String componentType);
}

public class ComponentDefinition {
    private String type;
    private String name;
    private Map<String, Object> props;
    private List<ValidationRule> validations;
    private Function<Object, String> formatter;
    private Function<String, Object> parser;
}
```

### 2.2 自定义组件示例

```java
@Component
public class CustomComponentRegistrar {
    private final ComponentRegistry registry;
    
    @PostConstruct
    public void registerComponents() {
        // 注册日期范围选择器
        registry.registerComponent("dateRange", new ComponentDefinition()
            .setType("dateRange")
            .setName("日期范围选择器")
            .setProps(Map.of(
                "format", "YYYY-MM-DD",
                "showTime", false,
                "allowClear", true
            ))
            .setFormatter(value -> {
                DateRange range = (DateRange) value;
                return range.getStart() + "~" + range.getEnd();
            })
            .setParser(text -> {
                String[] parts = text.split("~");
                return new DateRange(parts[0], parts[1]);
            })
        );
        
        // 注册金额输入框
        registry.registerComponent("amount", new ComponentDefinition()
            .setType("amount")
            .setName("金额输入框")
            .setProps(Map.of(
                "precision", 2,
                "min", 0,
                "prefix", "¥"
            ))
            .setValidations(List.of(
                new ValidationRule("min", 0),
                new ValidationRule("max", 999999999)
            ))
        );
    }
}
```

## 3. 查询引擎扩展

### 3.1 查询处理器接口

```java
public interface QueryProcessor {
    /**
     * 处理查询
     */
    QueryResult process(QueryContext context);
    
    /**
     * 获取处理器优先级
     */
    int getOrder();
    
    /**
     * 是否支持该类型查询
     */
    boolean supports(QueryContext context);
}

public class QueryContext {
    private String sql;
    private Map<String, Object> parameters;
    private DataSource dataSource;
    private QueryConfig config;
    private SecurityContext securityContext;
}
```

### 3.2 自定义查询处理器

```java
@Component
public class CacheQueryProcessor implements QueryProcessor {
    private final Cache<String, QueryResult> queryCache;
    
    @Override
    public QueryResult process(QueryContext context) {
        String cacheKey = generateCacheKey(context);
        
        // 检查缓存
        QueryResult cachedResult = queryCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 执行查询
        QueryResult result = executeQuery(context);
        
        // 更新缓存
        queryCache.put(cacheKey, result);
        
        return result;
    }
    
    @Override
    public int getOrder() {
        return 100;
    }
    
    @Override
    public boolean supports(QueryContext context) {
        return context.getConfig().isCacheEnabled();
    }
}
```

## 4. AI能力扩展

### 4.1 AI服务接口

```java
public interface AIService {
    /**
     * 自然语言转SQL
     */
    String generateSQL(String naturalLanguage, Map<String, Object> context);
    
    /**
     * 推断表关系
     */
    List<TableRelation> inferRelations(List<TableMetadata> tables);
    
    /**
     * 推荐显示配置
     */
    DisplayConfig recommendConfig(QueryMetadata queryMetadata);
}
```

### 4.2 OpenRouter实现

```java
@Service
public class OpenRouterAIService implements AIService {
    private final OpenRouterClient client;
    
    @Override
    public String generateSQL(String naturalLanguage, Map<String, Object> context) {
        String prompt = buildPrompt(naturalLanguage, context);
        return client.complete(prompt);
    }
    
    private String buildPrompt(String naturalLanguage, Map<String, Object> context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("将以下自然语言转换为SQL查询：\n");
        prompt.append(naturalLanguage).append("\n\n");
        prompt.append("可用的表结构：\n");
        
        // 添加表结构信息
        context.forEach((key, value) -> {
            if (value instanceof TableMetadata) {
                prompt.append(formatTableMetadata((TableMetadata) value));
            }
        });
        
        return prompt.toString();
    }
}
```

## 5. 显示配置扩展

### 5.1 显示类型接口

```java
public interface DisplayTypeHandler {
    /**
     * 获取显示类型
     */
    String getType();
    
    /**
     * 生成显示配置
     */
    DisplayConfig generateConfig(QueryMetadata metadata);
    
    /**
     * 验证配置
     */
    void validateConfig(DisplayConfig config);
}
```

### 5.2 自定义显示类型

```java
@Component
public class ChartDisplayHandler implements DisplayTypeHandler {
    @Override
    public String getType() {
        return "CHART";
    }
    
    @Override
    public DisplayConfig generateConfig(QueryMetadata metadata) {
        ChartDisplayConfig config = new ChartDisplayConfig();
        
        // 分析查询元数据
        List<ColumnMetadata> columns = metadata.getColumns();
        
        // 推断图表类型
        ChartType chartType = inferChartType(columns);
        config.setChartType(chartType);
        
        // 设置轴配置
        config.setXAxis(selectXAxis(columns));
        config.setYAxis(selectYAxis(columns));
        
        // 设置图表选项
        config.setOptions(generateChartOptions(chartType));
        
        return config;
    }
    
    private ChartType inferChartType(List<ColumnMetadata> columns) {
        // 基于列类型推断适合的图表类型
        if (hasTimeColumn(columns) && hasNumericColumn(columns)) {
            return ChartType.LINE;
        } else if (hasCategoryColumn(columns) && hasNumericColumn(columns)) {
            return ChartType.BAR;
        } else {
            return ChartType.TABLE;
        }
    }
}
```

## 6. 事件系统

### 6.1 事件定义

```java
public interface DataScopeEvent {
    String getEventType();
    LocalDateTime getTimestamp();
    String getSource();
}

public class QueryExecutedEvent implements DataScopeEvent {
    private String queryId;
    private String userId;
    private long executionTime;
    private boolean success;
    private String errorMessage;
}

public class MetadataChangedEvent implements DataScopeEvent {
    private String datasourceId;
    private String objectType;
    private String objectId;
    private ChangeType changeType;
}
```

### 6.2 事件处理器

```java
@Component
public class QueryEventHandler {
    private final MetricsService metricsService;
    private final AIService aiService;
    
    @EventListener
    public void handleQueryExecuted(QueryExecutedEvent event) {
        // 记录指标
        metricsService.recordQueryExecution(
            event.getQueryId(),
            event.getUserId(),
            event.getExecutionTime()
        );
        
        // 更新AI模型
        if (event.isSuccess()) {
            aiService.learnFromQuery(event);
        }
    }
}

@Component
public class MetadataEventHandler {
    private final Cache<String, Object> metadataCache;
    
    @EventListener
    public void handleMetadataChanged(MetadataChangedEvent event) {
        // 清除相关缓存
        String cacheKey = generateCacheKey(event);
        metadataCache.invalidate(cacheKey);
        
        // 触发元数据重新加载
        if (event.getChangeType() == ChangeType.STRUCTURE_CHANGED) {
            triggerMetadataReload(event.getDatasourceId());
        }
    }
}
```