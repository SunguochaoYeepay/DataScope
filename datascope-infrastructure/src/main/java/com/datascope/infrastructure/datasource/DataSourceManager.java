package com.datascope.infrastructure.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源连接池管理器
 */
@Slf4j
@Component
public class DataSourceManager {

    private final Map<String, DruidDataSource> dataSourcePool = new ConcurrentHashMap<>();

    /**
     * 获取数据源连接
     *
     * @param dataSource 数据源配置
     * @return 数据库连接
     */
    public Connection getConnection(DataSource dataSource) throws SQLException {
        DruidDataSource druidDataSource = dataSourcePool.get(dataSource.getId());
        if (druidDataSource == null) {
            synchronized (this) {
                druidDataSource = dataSourcePool.get(dataSource.getId());
                if (druidDataSource == null) {
                    druidDataSource = createDataSource(dataSource);
                    dataSourcePool.put(dataSource.getId(), druidDataSource);
                }
            }
        }
        return druidDataSource.getConnection();
    }

    /**
     * 测试数据源连接
     *
     * @param dataSource 数据源配置
     * @return 是否连接成功
     */
    public boolean testConnection(DataSource dataSource) {
        DruidDataSource druidDataSource = null;
        try {
            druidDataSource = createDataSource(dataSource);
            try (Connection conn = druidDataSource.getConnection()) {
                return true;
            }
        } catch (SQLException e) {
            log.error("Test connection failed for datasource: {}", dataSource.getName(), e);
            return false;
        } finally {
            if (druidDataSource != null) {
                druidDataSource.close();
            }
        }
    }

    /**
     * 移除数据源连接池
     *
     * @param dataSourceId 数据源ID
     */
    public void removeDataSource(String dataSourceId) {
        DruidDataSource druidDataSource = dataSourcePool.remove(dataSourceId);
        if (druidDataSource != null) {
            druidDataSource.close();
        }
    }

    /**
     * 创建数据源
     */
    private DruidDataSource createDataSource(DataSource dataSource) {
        DruidDataSource druidDataSource = new DruidDataSource();
        
        // 设置基本连接信息
        druidDataSource.setUrl(buildJdbcUrl(dataSource));
        druidDataSource.setUsername(dataSource.getUsername());
        druidDataSource.setPassword(dataSource.getPassword());
        
        // 设置连接池配置
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(20);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery(getValidationQuery(dataSource.getType()));
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        
        return druidDataSource;
    }

    /**
     * 构建JDBC URL
     */
    private String buildJdbcUrl(DataSource dataSource) {
        String baseUrl = switch (dataSource.getType()) {
            case MYSQL -> String.format("jdbc:mysql://%s:%d/%s",
                    dataSource.getHost(),
                    dataSource.getPort(),
                    dataSource.getDatabaseName());
            case DB2 -> String.format("jdbc:db2://%s:%d/%s",
                    dataSource.getHost(),
                    dataSource.getPort(),
                    dataSource.getDatabaseName());
        };

        // 添加额外的连接参数
        if (dataSource.getParameters() != null && !dataSource.getParameters().isEmpty()) {
            return baseUrl + "?" + dataSource.getParameters();
        }
        return baseUrl;
    }

    /**
     * 获取数据库验证查询语句
     */
    private String getValidationQuery(DataSourceType type) {
        return switch (type) {
            case MYSQL -> "SELECT 1";
            case DB2 -> "SELECT 1 FROM SYSIBM.SYSDUMMY1";
        };
    }
}