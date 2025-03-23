# 开发规范

## 1. 代码结构规范

### 1.1 项目结构

```
datascope/
├── app/                    # 应用层
│   ├── assembler/         # DTO转换
│   │   ├── DataSourceAssembler.java
│   │   └── QueryConfigAssembler.java
│   ├── command/          # 命令处理
│   │   ├── CreateDataSourceCommand.java
│   │   └── ExecuteQueryCommand.java
│   ├── query/           # 查询处理
│   │   ├── DataSourceQuery.java
│   │   └── QueryHistoryQuery.java
│   └── service/         # 应用服务
│       ├── DataSourceService.java
│       └── QueryService.java
├── domain/                # 领域层
│   ├── model/           # 领域模型
│   │   ├── datasource/
│   │   └── query/
│   ├── repository/      # 仓储接口
│   │   ├── DataSourceRepository.java
│   │   └── QueryRepository.java
│   └── service/        # 领域服务
│       ├── MetadataService.java
│       └── QueryExecutionService.java
├── facade/                # 接口层
│   ├── api/             # API接口
│   │   ├── DataSourceController.java
│   │   └── QueryController.java
│   ├── dto/            # 数据传输对象
│   │   ├── request/
│   │   └── response/
│   └── converter/      # 对象转换器
│       ├── DataSourceConverter.java
│       └── QueryConverter.java
├── infrastructure/        # 基础设施层
│   ├── config/         # 配置
│   │   ├── DatabaseConfig.java
│   │   └── RedisConfig.java
│   ├── persistence/    # 持久化实现
│   │   ├── entity/
│   │   └── repository/
│   ├── integration/    # 外部集成
│   │   ├── openrouter/
│   │   └── datasource/
│   └── common/        # 公共组件
│       ├── utils/
│       └── exception/
└── main/                 # 启动模块
    └── Application.java
```

### 1.2 包命名规范

```java
// 基础包名
com.company.datascope

// 模块包名
com.company.datascope.app
com.company.datascope.domain
com.company.datascope.facade
com.company.datascope.infrastructure

// 功能包名
com.company.datascope.app.service
com.company.datascope.domain.model
com.company.datascope.facade.api
com.company.datascope.infrastructure.persistence
```

## 2. 编码规范

### 2.1 Java编码规范

```java
// 1. 类名使用UpperCamelCase
public class DataSourceService {
    // 类实现
}

// 2. 方法名使用lowerCamelCase
public QueryResult executeQuery(String sql) {
    // 方法实现
}

// 3. 常量使用UPPER_SNAKE_CASE
public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

// 4. 变量使用lowerCamelCase
private final DataSourceRepository dataSourceRepository;

// 5. 包名使用小写字母
package com.company.datascope.domain.model;

// 6. 接口名使用UpperCamelCase
public interface QueryExecutor {
    // 接口定义
}
```

### 2.2 注释规范

```java
/**
 * 数据源服务类，负责数据源的管理和操作
 * 
 * @author developer
 * @since 1.0.0
 */
@Service
public class DataSourceService {
    
    /**
     * 创建新的数据源
     *
     * @param config 数据源配置信息
     * @return 创建的数据源ID
     * @throws DataSourceException 当数据源创建失败时
     */
    public String createDataSource(DataSourceConfig config) {
        // 方法实现
    }
    
    // 单行注释示例
    private void validateConfig(DataSourceConfig config) {
        // 配置验证逻辑
    }
}
```

### 2.3 异常处理规范

```java
// 1. 自定义异常类
public class DataScopeException extends RuntimeException {
    private final ErrorCode errorCode;
    
    public DataScopeException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}

// 2. 异常处理示例
public class QueryService {
    public QueryResult executeQuery(String queryId) {
        try {
            // 查询执行逻辑
            return queryExecutor.execute(queryId);
        } catch (SQLException e) {
            throw new DataScopeException(
                ErrorCode.QUERY_EXECUTION_FAILED,
                "Failed to execute query: " + e.getMessage()
            );
        } catch (Exception e) {
            throw new DataScopeException(
                ErrorCode.INTERNAL_ERROR,
                "Unexpected error: " + e.getMessage()
            );
        }
    }
}

// 3. 全局异常处理
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataScopeException.class)
    public ResponseEntity<ErrorResponse> handleDataScopeException(DataScopeException e) {
        ErrorResponse response = new ErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
```

## 3. 数据库规范

### 3.1 表命名规范

```sql
-- 1. 表名使用小写字母，单词间用下划线分隔
CREATE TABLE tbl_data_source (
    -- 表定义
);

-- 2. 字段名使用小写字母，单词间用下划线分隔
CREATE TABLE tbl_query_config (
    id VARCHAR(36) PRIMARY KEY,
    query_name VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL
);

-- 3. 索引命名规范
CREATE TABLE tbl_query_history (
    id VARCHAR(36) PRIMARY KEY,
    query_id VARCHAR(36) NOT NULL,
    -- 唯一索引使用u_idx前缀
    CONSTRAINT u_idx_query_history_id UNIQUE (query_id),
    -- 普通索引使用idx前缀
    INDEX idx_query_history_created_at (created_at)
);
```

### 3.2 SQL编写规范

```sql
-- 1. 关键字使用大写
SELECT 
    id,
    query_name,
    created_at
FROM 
    tbl_query_config
WHERE 
    status = 'ACTIVE'
    AND created_at >= ?
ORDER BY 
    created_at DESC;

-- 2. 使用参数化查询
INSERT INTO tbl_query_history (
    id,
    query_id,
    execution_time,
    status
) VALUES (
    ?,
    ?,
    ?,
    ?
);

-- 3. 表连接使用显式JOIN
SELECT 
    qc.query_name,
    qh.execution_time
FROM 
    tbl_query_config qc
    INNER JOIN tbl_query_history qh ON qc.id = qh.query_id
WHERE 
    qc.status = 'ACTIVE';
```

## 4. API设计规范

### 4.1 RESTful API规范

```java
@RestController
@RequestMapping("/api/v1/datasources")
public class DataSourceController {
    
    // 1. 使用HTTP方法表示操作类型
    @PostMapping
    public ResponseEntity<DataSourceResponse> createDataSource(
            @RequestBody @Valid DataSourceRequest request) {
        // 创建实现
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DataSourceResponse> getDataSource(
            @PathVariable String id) {
        // 查询实现
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DataSourceResponse> updateDataSource(
            @PathVariable String id,
            @RequestBody @Valid DataSourceRequest request) {
        // 更新实现
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataSource(
            @PathVariable String id) {
        // 删除实现
    }
}
```

### 4.2 请求/响应规范

```java
// 1. 请求对象
@Data
public class DataSourceRequest {
    @NotBlank
    private String name;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private DataSourceType type;
    
    @NotBlank
    private String host;
    
    @Min(1)
    @Max(65535)
    private Integer port;
    
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
}

// 2. 响应对象
@Data
public class DataSourceResponse {
    private String id;
    private String name;
    private DataSourceType type;
    private String status;
    private LocalDateTime createdAt;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
}

// 3. 分页响应
@Data
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long total;
}
```

## 5. 测试规范

### 5.1 单元测试规范

```java
@ExtendWith(MockitoExtension.class)
public class DataSourceServiceTest {
    
    @Mock
    private DataSourceRepository repository;
    
    @InjectMocks
    private DataSourceService service;
    
    @Test
    void createDataSource_WithValidConfig_ShouldSucceed() {
        // Arrange
        DataSourceConfig config = new DataSourceConfig();
        config.setName("test");
        when(repository.save(any())).thenReturn("123");
        
        // Act
        String result = service.createDataSource(config);
        
        // Assert
        assertNotNull(result);
        verify(repository).save(any());
    }
    
    @Test
    void createDataSource_WithInvalidConfig_ShouldThrowException() {
        // Arrange
        DataSourceConfig config = new DataSourceConfig();
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            service.createDataSource(config);
        });
    }
}
```

### 5.2 集成测试规范

```java
@SpringBootTest
@AutoConfigureMockMvc
public class DataSourceControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createDataSource_WithValidRequest_ShouldReturn200() throws Exception {
        // Arrange
        DataSourceRequest request = new DataSourceRequest();
        request.setName("test");
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/datasources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
    }
    
    @Test
    void createDataSource_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Arrange
        DataSourceRequest request = new DataSourceRequest();
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/datasources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
```

## 6. 日志规范

### 6.1 日志配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_PATH" value="/var/log/datascope"/>
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### 6.2 日志使用规范

```java
@Slf4j
public class QueryService {
    
    public QueryResult executeQuery(String queryId) {
        // 1. 输入参数日志
        log.info("Executing query: {}", queryId);
        
        try {
            // 2. 关键步骤日志
            log.debug("Validating query parameters");
            validateQuery(queryId);
            
            log.debug("Preparing query execution");
            QueryResult result = queryExecutor.execute(queryId);
            
            // 3. 执行结果日志
            log.info("Query executed successfully: {}, rows: {}", 
                    queryId, result.getRowCount());
            
            return result;
        } catch (Exception e) {
            // 4. 错误日志
            log.error("Failed to execute query: {}, error: {}", 
                    queryId, e.getMessage(), e);
            throw e;
        }
    }
}
```

## 7. 文档规范

### 7.1 API文档

```java
@RestController
@RequestMapping("/api/v1/queries")
@Tag(name = "查询管理", description = "查询配置和执行相关接口")
public class QueryController {
    
    @Operation(summary = "创建查询配置",
            description = "创建新的查询配置，包括SQL模板和显示配置")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public ResponseEntity<QueryResponse> createQuery(
            @RequestBody @Valid QueryRequest request) {
        // 实现
    }
}
```

### 7.2 README规范

```markdown
# DataScope项目

## 项目说明
DataScope是一个数据管理和查询系统，支持多数据源管理、元数据提取、查询配置等功能。

## 技术栈
- Java 17
- Spring Boot 2.7.x
- MySQL 8.0
- Redis 6.x

## 快速开始
1. 克隆项目
```bash
git clone https://github.com/company/datascope.git
```

2. 配置环境
- 复制 `application.example.yml` 为 `application.yml`
- 修改数据库和Redis配置

3. 构建运行
```bash
./mvnw clean package
java -jar target/datascope.jar
```

## 项目结构
- `app/`: 应用层
- `domain/`: 领域层
- `facade/`: 接口层
- `infrastructure/`: 基础设施层

## 开发指南
详见 [开发文档](docs/development.md)

## 接口文档
访问 `http://localhost:8080/swagger-ui.html`

## 贡献指南
1. Fork 项目
2. 创建特性分支
3. 提交变更
4. 发起 Pull Request

## 许可证
MIT License
```