package com.datascope.domain.service.metadata.extractor;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.metadata.ColumnMetadata;
import com.datascope.domain.model.metadata.ColumnType;
import com.datascope.domain.model.metadata.IndexMetadata;
import com.datascope.domain.model.metadata.TableMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MySQL元数据提取器实现
 * 负责从MySQL数据源中提取表、列、索引等元数据信息
 */
@Slf4j
@Component
public class MySQLMetadataExtractor implements MetadataExtractor {

    private static final String SELECT_TABLE_INFO = 
        "SELECT table_comment, table_rows, data_length, index_length " +
        "FROM information_schema.tables " +
        "WHERE table_schema = ? AND table_name = ?";

    private static final String SELECT_INDEX_STATS = 
        "SELECT index_name, cardinality, index_length " +
        "FROM information_schema.statistics " +
        "WHERE table_schema = ? AND table_name = ?";

    private static final String SHOW_DATABASES = "SHOW DATABASES";
    
    private static final String SHOW_TABLES = "SHOW TABLES FROM ?";

    @Override
    public List<TableMetadata> extractAll(DataSource dataSource, Connection connection) {
        List<TableMetadata> tables = new ArrayList<>();
        try {
            List<String> schemas = getSchemas(connection);
            
            for (String schema : schemas) {
                List<String> tableNames = getTables(connection, schema);
                for (String tableName : tableNames) {
                    try {
                        TableMetadata table = extractTable(dataSource, connection, schema, tableName);
                        if (table != null) {
                            tables.add(table);
                        }
                    } catch (Exception e) {
                        log.error("Failed to extract metadata for table {}.{}", schema, tableName, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract metadata from datasource {}", dataSource.getId(), e);
        }
        return tables;
    }

    @Override
    public TableMetadata extractTable(DataSource dataSource, Connection connection, String schema, String tableName) {
        try {
            TableMetadata table = new TableMetadata();
            table.setDataSourceId(dataSource.getId());
            table.setName(tableName);
            table.setSchema(schema);

            // 提取表信息
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_TABLE_INFO)) {
                stmt.setString(1, schema);
                stmt.setString(2, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        table.setComment(rs.getString("table_comment"));
                        table.setRowCount(rs.getLong("table_rows"));
                        table.setDataSize(rs.getLong("data_length"));
                        table.setIndexSize(rs.getLong("index_length"));
                    }
                }
            }

            // 提取列信息
            DatabaseMetaData metaData = connection.getMetaData();
            try (ResultSet columns = metaData.getColumns(null, schema, tableName, null)) {
                while (columns.next()) {
                    ColumnMetadata column = extractColumn(columns);
                    table.addColumn(column);
                }
            }

            // 提取主键信息
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(null, schema, tableName)) {
                while (primaryKeys.next()) {
                    String columnName = primaryKeys.getString("COLUMN_NAME");
                    ColumnMetadata column = table.getColumn(columnName);
                    if (column != null) {
                        column.setPrimaryKey(true);
                    }
                }
            }

            // 提取索引信息
            Map<String, IndexMetadata> indexMap = new HashMap<>();
            try (ResultSet indices = metaData.getIndexInfo(null, schema, tableName, false, false)) {
                while (indices.next()) {
                    String indexName = indices.getString("INDEX_NAME");
                    if (indexName == null) continue;

                    IndexMetadata index = indexMap.computeIfAbsent(indexName, k -> {
                        IndexMetadata idx = new IndexMetadata();
                        idx.setName(k);
                        idx.setUnique(!indices.getBoolean("NON_UNIQUE"));
                        idx.setTable(table);
                        return idx;
                    });

                    String columnName = indices.getString("COLUMN_NAME");
                    index.addColumn(columnName);
                }
            }

            // 获取索引统计信息
            try (PreparedStatement stmt = connection.prepareStatement(SELECT_INDEX_STATS)) {
                stmt.setString(1, schema);
                stmt.setString(2, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String indexName = rs.getString("index_name");
                        IndexMetadata index = indexMap.get(indexName);
                        if (index != null) {
                            index.setCardinality(rs.getLong("cardinality"));
                            index.setIndexSize(rs.getLong("index_length"));
                        }
                    }
                }
            }

            // 添加索引到表
            indexMap.values().forEach(table::addIndex);

            return table;
        } catch (Exception e) {
            log.error("Failed to extract metadata for table {}.{}", schema, tableName, e);
            return null;
        }
    }

    @Override
    public List<String> getSchemas(Connection connection) {
        List<String> schemas = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SHOW_DATABASES)) {
            while (rs.next()) {
                schemas.add(rs.getString(1));
            }
        } catch (Exception e) {
            log.error("Failed to get schemas", e);
        }
        return schemas;
    }

    @Override
    public List<String> getTables(Connection connection, String schema) {
        List<String> tables = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SHOW_TABLES)) {
            stmt.setString(1, schema);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tables.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            log.error("Failed to get tables for schema {}", schema, e);
        }
        return tables;
    }

    @Override
    public boolean supports(DataSource dataSource) {
        return dataSource.getType() == DataSourceType.MYSQL;
    }

    private ColumnMetadata extractColumn(ResultSet rs) throws SQLException {
        ColumnMetadata column = new ColumnMetadata();
        column.setName(rs.getString("COLUMN_NAME"));
        column.setType(mapJdbcType(rs.getInt("DATA_TYPE"), rs.getString("TYPE_NAME")));
        column.setLength(rs.getInt("COLUMN_SIZE"));
        column.setPrecision(rs.getInt("DECIMAL_DIGITS"));
        column.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
        column.setOrdinalPosition(rs.getInt("ORDINAL_POSITION"));
        column.setDefaultValue(rs.getString("COLUMN_DEF"));
        column.setComment(rs.getString("REMARKS"));
        column.setAutoIncrement(rs.getString("IS_AUTOINCREMENT").equals("YES"));
        return column;
    }

    private ColumnType mapJdbcType(int jdbcType, String typeName) {
        return switch (jdbcType) {
            case Types.TINYINT -> ColumnType.TINYINT;
            case Types.SMALLINT -> ColumnType.SMALLINT;
            case Types.INTEGER -> ColumnType.INTEGER;
            case Types.BIGINT -> ColumnType.BIGINT;
            case Types.FLOAT -> ColumnType.FLOAT;
            case Types.DOUBLE -> ColumnType.DOUBLE;
            case Types.DECIMAL -> ColumnType.DECIMAL;
            case Types.CHAR -> ColumnType.CHAR;
            case Types.VARCHAR -> ColumnType.VARCHAR;
            case Types.LONGVARCHAR -> ColumnType.TEXT;
            case Types.DATE -> ColumnType.DATE;
            case Types.TIME -> ColumnType.TIME;
            case Types.TIMESTAMP -> ColumnType.TIMESTAMP;
            case Types.BOOLEAN -> ColumnType.BOOLEAN;
            case Types.BINARY -> ColumnType.BINARY;
            case Types.VARBINARY -> ColumnType.VARBINARY;
            case Types.BLOB -> ColumnType.BLOB;
            default -> {
                if ("JSON".equalsIgnoreCase(typeName)) {
                    yield ColumnType.JSON;
                }
                yield ColumnType.VARCHAR;
            }
        };
    }
}