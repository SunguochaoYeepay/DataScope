# 核心功能实现

## 1. 数据源管理

### 1.1 数据源连接池管理

```java
@Service
public class DataSourcePoolManager {
    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();
    
    public DataSource getDataSource(String datasourceId) {
        return dataSources.computeIfAbsent(datasourceId, this::createDataSource);
    }
    
    private HikariDataSource createDataSource(String datasourceId) {
        DataSourceConfig config = getConfig(datasourceId);
        HikariConfig hikariConfig = new HikariConfig();
        // 设置连接池配置
        hikariConfig.setJdbcUrl(config.getJdbcUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(decryptPassword(config));
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(5);
        return new HikariDataSource(hikariConfig);
    }
}
```

### 1.2 密码加密处理

```java
@Component
public class PasswordEncryption {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    
    @Value("${datascope.encryption.key}")
    private String encryptionKey;
    
    public String encrypt(String password, String salt) {
        SecretKey key = generateKey(encryptionKey, salt);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    public String decrypt(String encryptedPassword, String salt) {
        SecretKey key = generateKey(encryptionKey, salt);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decrypted);
    }
}
```

## 2. 元数据管理

### 2.1 元数据提取器

```java
public interface MetadataExtractor {
    List<TableMetadata> extractTables(DataSource dataSource);
    List<ColumnMetadata> extractColumns(DataSource dataSource, String tableName);
    List<RelationMetadata> extractRelations(DataSource dataSource);
}

@Service
public class MySQLMetadataExtractor implements MetadataExtractor {
    @Override
    public List<TableMetadata> extractTables(DataSource dataSource) {
        String sql = """
            SELECT 
                TABLE_NAME,
                TABLE_COMMENT
            FROM 
                INFORMATION_SCHEMA.TABLES
            WHERE 
                TABLE_SCHEMA = ?
        """;
        // 实现提取逻辑
    }
    
    @Override
    public List<ColumnMetadata> extractColumns(DataSource dataSource, String tableName) {
        String sql = """
            SELECT 
                COLUMN_NAME,
                DATA_TYPE,
                CHARACTER_MAXIMUM_LENGTH,
                COLUMN_COMMENT,
                IS_NULLABLE,
                COLUMN_KEY
            FROM 
                INFORMATION_SCHEMA.COLUMNS
            WHERE 
                TABLE_SCHEMA = ?
                AND TABLE_NAME = ?
        """;
        // 实现提取逻辑
    }
}
```

### 2.2 关系推断

```java
@Service
public class RelationInferenceService {
    private final AIClient aiClient;
    
    public List<RelationMetadata> inferRelations(List<TableMetadata> tables) {
        List<RelationMetadata> relations = new ArrayList<>();
        
        // 1. 基于命名模式推断
        relations.addAll(inferByNamingPattern(tables));
        
        // 2. 基于数据分布推断
        relations.addAll(inferByDataDistribution(tables));
        
        // 3. 基于查询模式推断
        relations.addAll(inferByQueryPattern(tables));
        
        return relations;
    }
    
    private List<RelationMetadata> inferByNamingPattern(List<TableMetadata> tables) {
        // 实现基于命名模式的推断逻辑
    }
    
    private List<RelationMetadata> inferByDataDistribution(List<TableMetadata> tables) {
        // 实现基于数据分布的推断逻辑
    }
}
```

## 3. 查询管理

### 3.1 SQL生成器

```java
@Service
public class SQLGenerator {
    private final OpenRouterClient openRouterClient;
    
    public String generateSQL(String naturalLanguage, Map<String, Object> context) {
        // 1. 准备提示信息
        String prompt = buildPrompt(naturalLanguage, context);
        
        // 2. 调用LLM生成SQL
        String sql = openRouterClient.complete(prompt);
        
        // 3. SQL验证和优化
        sql = validateAndOptimize(sql);
        
        return sql;
    }
    
    private String validateAndOptimize(String sql) {
        // SQL语法验证和优化逻辑
    }
}
```

### 3.2 查询执行器

```java
@Service
public class QueryExecutor {
    private final DataSourcePoolManager dataSourceManager;
    
    public QueryResult execute(String datasourceId, String sql, Map<String, Object> params) {
        DataSource dataSource = dataSourceManager.getDataSource(datasourceId);
        
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            setParameters(stmt, params);
            
            // 设置查询超时
            stmt.setQueryTimeout(30);
            
            ResultSet rs = stmt.executeQuery();
            return buildQueryResult(rs);
        }
    }
    
    private QueryResult buildQueryResult(ResultSet rs) {
        // 构建查询结果对象
    }
}
```

## 4. 低代码配置

### 4.1 组件映射管理

```java
@Service
public class ComponentMappingService {
    private final Map<String, ComponentDefinition> typeMapping;
    
    public ComponentDefinition getComponent(String dataType) {
        return typeMapping.getOrDefault(dataType, getDefaultComponent());
    }
    
    public List<ValidationRule> getValidationRules(String dataType) {
        ComponentDefinition component = getComponent(dataType);
        return component.getValidationRules();
    }
}
```

### 4.2 显示配置生成

```java
@Service
public class DisplayConfigGenerator {
    private final AIClient aiClient;
    
    public DisplayConfig generateConfig(QueryMetadata queryMetadata) {
        // 1. 分析查询元数据
        List<ColumnInfo> columns = analyzeColumns(queryMetadata);
        
        // 2. 生成显示配置
        DisplayConfig config = new DisplayConfig();
        config.setConditions(generateConditions(columns));
        config.setResults(generateResults(columns));
        
        return config;
    }
    
    private List<ConditionConfig> generateConditions(List<ColumnInfo> columns) {
        // 生成查询条件配置
    }
    
    private List<ResultConfig> generateResults(List<ColumnInfo> columns) {
        // 生成结果显示配置
    }
}
```

## 5. AI辅助功能

### 5.1 智能推荐服务

```java
@Service
public class RecommendationService {
    private final UserPreferenceAnalyzer preferenceAnalyzer;
    private final QueryPatternAnalyzer queryPatternAnalyzer;
    
    public List<ConditionRecommendation> recommendConditions(String userId, String queryId) {
        // 1. 分析用户偏好
        UserPreference preference = preferenceAnalyzer.analyze(userId);
        
        // 2. 分析查询模式
        QueryPattern pattern = queryPatternAnalyzer.analyze(queryId);
        
        // 3. 生成推荐
        return generateRecommendations(preference, pattern);
    }
    
    private List<ConditionRecommendation> generateRecommendations(
            UserPreference preference, QueryPattern pattern) {
        // 实现推荐逻辑
    }
}
```

### 5.2 数据分析服务

```java
@Service
public class DataAnalysisService {
    public DataDistribution analyzeDistribution(DataSource dataSource, String tableName, String columnName) {
        String sql = """
            SELECT 
                %s,
                COUNT(*) as count
            FROM 
                %s
            GROUP BY 
                %s
        """.formatted(columnName, tableName, columnName);
        
        // 执行分析并返回分布信息
    }
    
    public CorrelationMatrix analyzeCorrelation(DataSource dataSource, String tableName, List<String> columns) {
        // 计算列之间的相关性
    }
}
```

## 6. 性能优化

### 6.1 缓存管理

```java
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
            
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .build();
    }
}
```

### 6.2 查询优化

```java
@Service
public class QueryOptimizer {
    public String optimize(String sql) {
        // 1. 解析SQL
        SQLStatement stmt = parseSql(sql);
        
        // 2. 应用优化规则
        applyOptimizationRules(stmt);
        
        // 3. 重新生成SQL
        return stmt.toString();
    }
    
    private void applyOptimizationRules(SQLStatement stmt) {
        // 实现优化规则
    }
}
```