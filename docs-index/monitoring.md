# 监控设计

## 1. 系统监控指标

### 1.1 基础指标定义

```java
@Configuration
public class MetricsConfig {
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
    
    @Bean
    public List<Tag> commonTags() {
        return Arrays.asList(
            Tag.of("application", "datascope"),
            Tag.of("environment", "${spring.profiles.active}")
        );
    }
}
```

### 1.2 监控指标列表

```yaml
metrics:
  # 数据源连接池监控
  datasource:
    - name: datasource_connection_pool_active
      type: gauge
      description: "活跃连接数"
      labels: [datasource_id]
    - name: datasource_connection_pool_idle
      type: gauge
      description: "空闲连接数"
      labels: [datasource_id]
    - name: datasource_connection_pool_total
      type: gauge
      description: "总连接数"
      labels: [datasource_id]
  
  # 查询执行监控
  query:
    - name: query_execution_time
      type: histogram
      description: "查询执行时间"
      labels: [query_id, user_id]
      buckets: [0.1, 0.5, 1, 2, 5, 10, 30]
    - name: query_execution_count
      type: counter
      description: "查询执行次数"
      labels: [query_id, status]
    - name: query_error_count
      type: counter
      description: "查询错误次数"
      labels: [query_id, error_type]
  
  # 缓存监控
  cache:
    - name: cache_hit_count
      type: counter
      description: "缓存命中次数"
      labels: [cache_name]
    - name: cache_miss_count
      type: counter
      description: "缓存未命中次数"
      labels: [cache_name]
    - name: cache_size
      type: gauge
      description: "缓存大小"
      labels: [cache_name]
  
  # API监控
  api:
    - name: api_request_count
      type: counter
      description: "API请求次数"
      labels: [path, method, status]
    - name: api_response_time
      type: histogram
      description: "API响应时间"
      labels: [path, method]
      buckets: [0.01, 0.05, 0.1, 0.5, 1, 2, 5]
```

## 2. 告警规则

### 2.1 告警配置

```yaml
alerts:
  # 数据源告警
  datasource:
    - name: high_connection_usage
      condition: datasource_connection_pool_active / datasource_connection_pool_total > 0.8
      duration: 5m
      severity: warning
      annotations:
        summary: "数据源连接池使用率过高"
        description: "数据源 {{ $labels.datasource_id }} 连接池使用率超过80%"
    
    - name: connection_error_spike
      condition: rate(datasource_connection_error_count[5m]) > 10
      severity: critical
      annotations:
        summary: "数据源连接错误激增"
        description: "数据源 {{ $labels.datasource_id }} 出现大量连接错误"
  
  # 查询告警
  query:
    - name: long_running_query
      condition: query_execution_time > 30
      severity: warning
      annotations:
        summary: "查询执行时间过长"
        description: "查询 {{ $labels.query_id }} 执行时间超过30秒"
    
    - name: high_error_rate
      condition: rate(query_error_count[5m]) / rate(query_execution_count[5m]) > 0.1
      severity: critical
      annotations:
        summary: "查询错误率过高"
        description: "查询错误率超过10%"
  
  # 系统资源告警
  system:
    - name: high_cpu_usage
      condition: system_cpu_usage > 0.8
      duration: 5m
      severity: warning
      annotations:
        summary: "CPU使用率过高"
        description: "系统CPU使用率超过80%"
    
    - name: high_memory_usage
      condition: system_memory_usage > 0.9
      duration: 5m
      severity: critical
      annotations:
        summary: "内存使用率过高"
        description: "系统内存使用率超过90%"
```

## 3. 日志配置

### 3.1 日志配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_PATH" value="/var/log/datascope"/>
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- 应用日志 -->
    <appender name="APP_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/application.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- 错误日志 -->
    <appender name="ERROR_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/error.log</file>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- SQL日志 -->
    <appender name="SQL_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/sql.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/sql.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <!-- 日志级别 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="APP_LOG"/>
        <appender-ref ref="ERROR_LOG"/>
    </root>
    
    <logger name="com.company.datascope" level="DEBUG"/>
    <logger name="com.company.datascope.sql" level="DEBUG">
        <appender-ref ref="SQL_LOG"/>
    </logger>
</configuration>
```

## 4. 监控实现

### 4.1 性能监控切面

```java
@Aspect
@Component
public class PerformanceMonitorAspect {
    private final MeterRegistry meterRegistry;
    
    @Around("@annotation(MonitorPerformance)")
    public Object monitorPerformance(ProceedingJoinPoint point) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            return point.proceed();
        } finally {
            sample.stop(Timer.builder("method.execution.time")
                .tag("class", point.getSignature().getDeclaringTypeName())
                .tag("method", point.getSignature().getName())
                .register(meterRegistry));
        }
    }
}
```

### 4.2 数据源监控

```java
@Component
public class DataSourceMetrics {
    private final MeterRegistry meterRegistry;
    private final DataSourcePoolManager poolManager;
    
    @Scheduled(fixedRate = 60000)
    public void recordMetrics() {
        poolManager.getDataSources().forEach((id, dataSource) -> {
            HikariDataSource ds = (HikariDataSource) dataSource;
            
            Gauge.builder("datasource.connections.active", ds, HikariDataSource::getActiveConnections)
                .tag("datasource_id", id)
                .register(meterRegistry);
                
            Gauge.builder("datasource.connections.idle", ds, HikariDataSource::getIdleConnections)
                .tag("datasource_id", id)
                .register(meterRegistry);
                
            Gauge.builder("datasource.connections.total", ds, HikariDataSource::getMaximumPoolSize)
                .tag("datasource_id", id)
                .register(meterRegistry);
        });
    }
}
```

### 4.3 查询监控

```java
@Component
public class QueryMetrics {
    private final MeterRegistry meterRegistry;
    
    public void recordQueryExecution(String queryId, String userId, long executionTime) {
        Timer.builder("query.execution.time")
            .tag("query_id", queryId)
            .tag("user_id", userId)
            .register(meterRegistry)
            .record(Duration.ofMillis(executionTime));
    }
    
    public void recordQueryError(String queryId, String errorType) {
        Counter.builder("query.error.count")
            .tag("query_id", queryId)
            .tag("error_type", errorType)
            .register(meterRegistry)
            .increment();
    }
}
```

## 5. 监控面板

### 5.1 Grafana仪表板配置

```json
{
  "dashboard": {
    "title": "DataScope监控面板",
    "panels": [
      {
        "title": "数据源连接池状态",
        "type": "graph",
        "datasource": "Prometheus",
        "targets": [
          {
            "expr": "datasource_connection_pool_active",
            "legendFormat": "{{datasource_id}} - 活跃连接"
          },
          {
            "expr": "datasource_connection_pool_idle",
            "legendFormat": "{{datasource_id}} - 空闲连接"
          }
        ]
      },
      {
        "title": "查询执行时间分布",
        "type": "heatmap",
        "datasource": "Prometheus",
        "targets": [
          {
            "expr": "rate(query_execution_time_bucket[5m])",
            "legendFormat": "{{le}}"
          }
        ]
      },
      {
        "title": "API请求量",
        "type": "graph",
        "datasource": "Prometheus",
        "targets": [
          {
            "expr": "sum(rate(api_request_count[5m])) by (path)",
            "legendFormat": "{{path}}"
          }
        ]
      }
    ]
  }
}
```

### 5.2 告警通知配置

```yaml
alertmanager:
  receivers:
    - name: 'team-datascope'
      email_configs:
        - to: 'team@company.com'
          send_resolved: true
      slack_configs:
        - channel: '#datascope-alerts'
          send_resolved: true
          
  route:
    receiver: 'team-datascope'
    group_by: ['alertname', 'cluster', 'service']
    group_wait: 30s
    group_interval: 5m
    repeat_interval: 4h
    
    routes:
      - match:
          severity: critical
        receiver: 'team-datascope'
        group_wait: 10s
        repeat_interval: 1h
```