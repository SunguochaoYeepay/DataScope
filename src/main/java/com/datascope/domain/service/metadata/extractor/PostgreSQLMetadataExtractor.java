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

@Slf4j
@Component
public class PostgreSQLMetadataExtractor implements MetadataExtractor {

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

            // 获取表信息
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT obj_description(c.oid) as table_comment, " +
                "pg_total_relation_size(c.oid) as total_bytes, " +
                "pg_indexes_size(c.oid) as index_bytes, " +
                "pg_stat_get_live_tuples(c.oid) as n_live_tup " +
                "FROM pg_class c " +
                "JOIN pg_namespace n ON n.oid = c.relnamespace " +
                "WHERE n.nspname = ? AND c.relname = ?")) {
                
                stmt.setString(1, schema);
                stmt.setString(2, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        table.setComment(rs.getString("table_comment"));
                        table.setDataSize(rs.getLong("total_bytes") - rs.getLong("index_bytes"));
                        table.setIndexSize(rs.getLong("index_bytes"));
                        table.setRowCount(rs.getLong("n_live_tup"));
                    }
                }
            }

            // 获取列信息
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT a.attname as column_name, " +
                "pg_catalog.format_type(a.atttypid, a.atttypmod) as data_type, " +
                "a.attnotnull as not_null, " +
                "a.attnum as ordinal_position, " +
                "pg_catalog.col_description(a.attrelid, a.attnum) as column_comment, " +
                "(SELECT pg_catalog.pg_get_expr(d.adbin, d.adrelid) " +
                "FROM pg_catalog.pg_attrdef d " +
                "WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum) as column_default, " +
                "a.attidentity = 'a' as is_identity " +
                "FROM pg_catalog.pg_attribute a " +
                "JOIN pg_catalog.pg_class c ON c.oid = a.attrelid " +
                "JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace " +
                "WHERE n.nspname = ? AND c.relname = ? AND a.attnum > 0 AND NOT a.attisdropped " +
                "ORDER BY a.attnum")) {
                
                stmt.setString(1, schema);
                stmt.setString(2, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ColumnMetadata column = new ColumnMetadata();
                        column.setName(rs.getString("column_name"));
                        column.setType(mapPostgreSQLType(rs.getString("data_type")));
                        column.setNullable(!rs.getBoolean("not_null"));
                        column.setOrdinalPosition(rs.getInt("ordinal_position"));
                        column.setComment(rs.getString("column_comment"));
                        column.setDefaultValue(rs.getString("column_default"));
                        column.setAutoIncrement(rs.getBoolean("is_identity"));
                        table.addColumn(column);
                    }
                }
            }

            // 获取主键信息
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT a.attname as column_name " +
                "FROM pg_index i " +
                "JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey) " +
                "JOIN pg_class c ON c.oid = i.indrelid " +
                "JOIN pg_namespace n ON n.oid = c.relnamespace " +
                "WHERE i.indisprimary AND n.nspname = ? AND c.relname = ?")) {
                
                stmt.setString(1, schema);
                stmt.setString(2, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String columnName = rs.getString("column_name");
                        ColumnMetadata column = table.getColumn(columnName);
                        if (column != null) {
                            column.setPrimaryKey(true);
                        }
                    }
                }
            }

            // 获取索引信息
            Map<String, IndexMetadata> indexMap = new HashMap<>();
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT i.relname as index_name, " +
                "ix.indisunique as is_unique, " +
                "a.attname as column_name, " +
                "pg_relation_size(i.oid) as index_size, " +
                "s.n_distinct as cardinality " +
                "FROM pg_index ix " +
                "JOIN pg_class i ON i.oid = ix.indexrelid " +
                "JOIN pg_class t ON t.oid = ix.indrelid " +
                "JOIN pg_namespace n ON n.oid = t.relnamespace " +
                "JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(ix.indkey) " +
                "LEFT JOIN pg_stats s ON s.schemaname = n.nspname " +
                "AND s.tablename = t.relname AND s.attname = a.attname " +
                "WHERE n.nspname = ? AND t.relname = ? " +
                "ORDER BY i.relname, a.attnum")) {
                
                stmt.setString(1, schema);
                stmt.setString(2, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String indexName = rs.getString("index_name");
                        IndexMetadata index = indexMap.computeIfAbsent(indexName, k -> {
                            IndexMetadata idx = new IndexMetadata();
                            idx.setName(k);
                            idx.setUnique(rs.getBoolean("is_unique"));
                            idx.setTable(table);
                            idx.setIndexSize(rs.getLong("index_size"));
                            return idx;
                        });
                        
                        index.addColumn(rs.getString("column_name"));
                        if (rs.getLong("cardinality") > 0) {
                            index.setCardinality(rs.getLong("cardinality"));
                        }
                    }
                }
            }

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
             ResultSet rs = stmt.executeQuery(
                 "SELECT nspname FROM pg_namespace " +
                 "WHERE nspname NOT LIKE 'pg_%' AND nspname != 'information_schema'")) {
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
        try (PreparedStatement stmt = connection.prepareStatement(
            "SELECT tablename FROM pg_tables WHERE schemaname = ?")) {
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
        return dataSource.getType() == DataSourceType.POSTGRESQL;
    }

    private ColumnType mapPostgreSQLType(String pgType) {
        if (pgType == null) return ColumnType.VARCHAR;
        
        return switch (pgType.toLowerCase()) {
            case "smallint" -> ColumnType.SMALLINT;
            case "integer" -> ColumnType.INTEGER;
            case "bigint" -> ColumnType.BIGINT;
            case "real" -> ColumnType.FLOAT;
            case "double precision" -> ColumnType.DOUBLE;
            case "numeric", "decimal" -> ColumnType.DECIMAL;
            case "character", "char" -> ColumnType.CHAR;
            case "character varying", "varchar" -> ColumnType.VARCHAR;
            case "text" -> ColumnType.TEXT;
            case "date" -> ColumnType.DATE;
            case "time" -> ColumnType.TIME;
            case "timestamp" -> ColumnType.TIMESTAMP;
            case "boolean" -> ColumnType.BOOLEAN;
            case "bytea" -> ColumnType.BINARY;
            case "json", "jsonb" -> ColumnType.JSON;
            default -> ColumnType.VARCHAR;
        };
    }
}