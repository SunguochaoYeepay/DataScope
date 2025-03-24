package com.datascope.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/** 数据源连接池配置类 */
@Slf4j
@Configuration
public class DataSourceConfig {

  @Value("${datascope.datasource.connection-timeout:30000}")
  private long connectionTimeout;

  @Value("${datascope.datasource.validation-timeout:5000}")
  private long validationTimeout;

  @Value("${datascope.datasource.max-lifetime:1800000}")
  private long maxLifetime;

  /** 创建 PostgreSQL 连接池配置 */
  public HikariConfig createPostgreSQLConfig(
      String host, int port, String database, String username, String password) {
    HikariConfig config = new HikariConfig();

    // 基本连接配置
    config.setJdbcUrl(String.format("jdbc:postgresql://%s:%d/%s", host, port, database));
    config.setUsername(username);
    config.setPassword(password);
    config.setDriverClassName("org.postgresql.Driver");

    // 连接池配置
    config.setMinimumIdle(5);
    config.setMaximumPoolSize(20);
    config.setConnectionTimeout(connectionTimeout);
    config.setValidationTimeout(validationTimeout);
    config.setMaxLifetime(maxLifetime);
    config.setAutoCommit(true);

    // 连接测试配置
    config.setConnectionTestQuery("SELECT 1");
    config.setValidationTimeout(5000);

    // 重试配置
    config.setInitializationFailTimeout(1);
    config.setKeepaliveTime(60000);

    // 性能配置
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");

    // 设置连接参数
    config.addDataSourceProperty("ApplicationName", "DataScope");
    config.addDataSourceProperty("reWriteBatchedInserts", "true");

    return config;
  }

  /** 创建 MySQL 连接池配置 */
  public HikariConfig createMySQLConfig(
      String host, int port, String database, String username, String password) {
    HikariConfig config = new HikariConfig();

    // 基本连接配置
    config.setJdbcUrl(
        String.format(
            "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true",
            host, port, database));
    config.setUsername(username);
    config.setPassword(password);
    config.setDriverClassName("com.mysql.cj.jdbc.Driver");

    // 连接池配置
    config.setMinimumIdle(5);
    config.setMaximumPoolSize(20);
    config.setConnectionTimeout(connectionTimeout);
    config.setValidationTimeout(validationTimeout);
    config.setMaxLifetime(maxLifetime);
    config.setAutoCommit(true);

    // 连接测试配置
    config.setConnectionTestQuery("SELECT 1");
    config.setValidationTimeout(5000);

    // 重试配置
    config.setInitializationFailTimeout(1);
    config.setKeepaliveTime(60000);

    // 性能配置
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");

    // 设置连接参数
    config.addDataSourceProperty("ApplicationName", "DataScope");
    config.addDataSourceProperty("useUnicode", "true");
    config.addDataSourceProperty("characterEncoding", "utf8");
    config.addDataSourceProperty("serverTimezone", "UTC");

    return config;
  }

  /** 创建数据源连接池 */
  public HikariDataSource createDataSource(HikariConfig config) {
    try {
      return new HikariDataSource(config);
    } catch (Exception e) {
      log.error("Failed to create data source: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to create data source", e);
    }
  }
}
