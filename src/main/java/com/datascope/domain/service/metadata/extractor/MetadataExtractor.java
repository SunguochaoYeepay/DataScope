package com.datascope.domain.service.metadata.extractor;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.metadata.TableMetadata;

import java.sql.Connection;
import java.util.List;

/**
 * 元数据提取器接口
 */
public interface MetadataExtractor {

    /**
     * 提取所有表的元数据
     *
     * @param dataSource 数据源
     * @param connection 数据库连接
     * @return 表元数据列表
     */
    List<TableMetadata> extractAll(DataSource dataSource, Connection connection);

    /**
     * 提取指定表的元数据
     *
     * @param dataSource 数据源
     * @param connection 数据库连接
     * @param schema     模式名
     * @param tableName  表名
     * @return 表元数据
     */
    TableMetadata extractTable(DataSource dataSource, Connection connection, String schema, String tableName);

    /**
     * 获取所有模式名
     *
     * @param connection 数据库连接
     * @return 模式名列表
     */
    List<String> getSchemas(Connection connection);

    /**
     * 获取指定模式下的所有表名
     *
     * @param connection 数据库连接
     * @param schema    模式名
     * @return 表名列表
     */
    List<String> getTables(Connection connection, String schema);

    /**
     * 检查是否支持该数据源类型
     *
     * @param dataSource 数据源
     * @return 是否支持
     */
    boolean supports(DataSource dataSource);
}