package com.datascope.domain.service.metadata.extractor;

import com.datascope.common.exception.DataScopeException;
import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.metadata.ColumnMetadata;
import com.datascope.domain.model.metadata.ColumnType;
import com.datascope.domain.model.metadata.IndexMetadata;
import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.infrastructure.util.RetryUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** PostgreSQL元数据提取器实现类 */
@Slf4j
@Component
public class PostgreSQLMetadataExtractor implements MetadataExtractor {

  private static final String TABLE_METADATA_QUERY =
      "SELECT obj_description(c.oid) as table_comment, "
          + "pg_total_relation_size(c.oid) as total_bytes, "
          + "pg_indexes_size(c.oid) as index_bytes, "
          + "pg_stat_get_live_tuples(c.oid) as n_live_tup "
          + "FROM pg_class c "
          + "JOIN pg_namespace n ON n.oid = c.relnamespace "
          + "WHERE n.nspname = ? AND c.relname = ?";

  private static final String COLUMN_METADATA_QUERY =
      "SELECT a.attname as column_name, "
          + "pg_catalog.format_type(a.atttypid, a.atttypmod) as data_type, "
          + "a.attnotnull as not_null, "
          + "a.attnum as ordinal_position, "
          + "pg_catalog.col_description(a.attrelid, a.attnum) as column_comment, "
          + "(SELECT pg_catalog.pg_get_expr(d.adbin, d.adrelid) "
          + "FROM pg_catalog.pg_attrdef d "
          + "WHERE d.adrelid = a.attrelid AND d.adnum = a.attnum) as column_default, "
          + "a.attidentity = 'a' as is_identity "
          + "FROM pg_catalog.pg_attribute a "
          + "JOIN pg_catalog.pg_class c ON c.oid = a.attrelid "
          + "JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace "
          + "WHERE n.nspname = ? AND c.relname = ? AND a.attnum > 0 AND NOT a.attisdropped "
          + "ORDER BY a.attnum";

  private static final String PRIMARY_KEY_QUERY =
      "SELECT a.attname as column_name "
          + "FROM pg_index i "
          + "JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY(i.indkey) "
          + "JOIN pg_class c ON c.oid = i.indrelid "
          + "JOIN pg_namespace n ON n.oid = c.relnamespace "
          + "WHERE i.indisprimary AND n.nspname = ? AND c.relname = ?";

  private static final String INDEX_METADATA_QUERY =
      "SELECT i.relname as index_name, "
          + "ix.indisunique as is_unique, "
          + "a.attname as column_name, "
          + "pg_relation_size(i.oid) as index_size, "
          + "s.n_distinct as cardinality "
          + "FROM pg_index ix "
          + "JOIN pg_class i ON i.oid = ix.indexrelid "
          + "JOIN pg_class t ON t.oid = ix.indrelid "
          + "JOIN pg_namespace n ON n.oid = t.relnamespace "
          + "JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = ANY(ix.indkey) "
          + "LEFT JOIN pg_stats s ON s.schemaname = n.nspname "
          + "AND s.tablename = t.relname AND s.attname = a.attname "
          + "WHERE n.nspname = ? AND t.relname = ? "
          + "ORDER BY i.relname, a.attnum";

  @Override
  public List<TableMetadata> extractAll(DataSource dataSource, Connection connection) {
    List<TableMetadata> tables = new ArrayList<>();
    try {
      List<String> schemas = getSchemas(connection);
      for (String schema : schemas) {
        List<String> tableNames = getTables(connection, schema);
        for (String tableName : tableNames) {
          try {
            TableMetadata table = extractTableWithRetry(dataSource, connection, schema, tableName);
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
      throw new DataScopeException("Failed to extract metadata", e);
    }
    return tables;
  }

  private TableMetadata extractTableWithRetry(
      DataSource dataSource, Connection connection, String schema, String tableName)
      throws Exception {
    return RetryUtil.retry(
        () -> extractTable(dataSource, connection, schema, tableName),
        3, // 最大重试次数
        e -> e instanceof SQLException, // 只对SQL异常进行重试
        1000 // 重试间隔1秒
        );
  }

  @Override
  public TableMetadata extractTable(
      DataSource dataSource, Connection connection, String schema, String tableName) {
    try {
      TableMetadata table = new TableMetadata();
      table.setDataSourceId(dataSource.getId());
      table.setName(tableName);
      table.setSchema(schema);

      // 获取表信息
      extractTableInfo(connection, schema, tableName, table);

      // 获取列信息
      extractColumnInfo(connection, schema, tableName, table);

      // 获取主键信息
      extractPrimaryKeyInfo(connection, schema, tableName, table);

      // 获取索引信息
      extractIndexInfo(connection, schema, tableName, table);

      return table;
    } catch (Exception e) {
      log.error("Failed to extract metadata for table {}.{}", schema, tableName, e);
      throw new DataScopeException("Failed to extract table metadata", e);
    }
  }

  private void extractTableInfo(
      Connection connection, String schema, String tableName, TableMetadata table)
      throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement(TABLE_METADATA_QUERY)) {
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
    } catch (SQLException e) {
      log.error("Failed to extract table info for {}.{}", schema, tableName, e);
      throw e;
    }
  }

  private void extractColumnInfo(
      Connection connection, String schema, String tableName, TableMetadata table)
      throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement(COLUMN_METADATA_QUERY)) {
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
    } catch (SQLException e) {
      log.error("Failed to extract column info for {}.{}", schema, tableName, e);
      throw e;
    }
  }

  private void extractPrimaryKeyInfo(
      Connection connection, String schema, String tableName, TableMetadata table)
      throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement(PRIMARY_KEY_QUERY)) {
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
    } catch (SQLException e) {
      log.error("Failed to extract primary key info for {}.{}", schema, tableName, e);
      throw e;
    }
  }

  private void extractIndexInfo(
      Connection connection, String schema, String tableName, TableMetadata table)
      throws SQLException {
    Map<String, IndexMetadata> indexMap = new HashMap<>();
    try (PreparedStatement stmt = connection.prepareStatement(INDEX_METADATA_QUERY)) {
      stmt.setString(1, schema);
      stmt.setString(2, tableName);
      try (ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
          String indexName = rs.getString("index_name");
          IndexMetadata index =
              indexMap.computeIfAbsent(
                  indexName,
                  k -> {
                    IndexMetadata idx = new IndexMetadata();
                    try {
                      idx.setName(k);
                      idx.setUnique(rs.getBoolean("is_unique"));
                      idx.setTable(table);
                      idx.setIndexSize(rs.getLong("index_size"));
                    } catch (SQLException e) {
                      log.error("Error getting index metadata: {}", e.getMessage());
                      throw new DataScopeException("Failed to get index metadata", e);
                    }
                    return idx;
                  });

          try {
            index.addColumn(rs.getString("column_name"));
            if (rs.getLong("cardinality") > 0) {
              index.setCardinality(rs.getLong("cardinality"));
            }
          } catch (SQLException e) {
            log.error("Error getting column metadata: {}", e.getMessage());
            throw new DataScopeException("Failed to get column metadata", e);
          }
        }
      }
    } catch (SQLException e) {
      log.error("Failed to extract index info for {}.{}", schema, tableName, e);
      throw e;
    }
    indexMap.values().forEach(table::addIndex);
  }

  @Override
  public List<String> getSchemas(Connection connection) {
    List<String> schemas = new ArrayList<>();
    try {
      Callable<List<String>> operation =
          () -> {
            List<String> result = new ArrayList<>();
            try (Statement stmt = connection.createStatement();
                ResultSet rs =
                    stmt.executeQuery(
                        "SELECT nspname FROM pg_namespace "
                            + "WHERE nspname NOT LIKE 'pg_%' AND nspname != 'information_schema'")) {
              while (rs.next()) {
                result.add(rs.getString("nspname"));
              }
            }
            return result;
          };
      schemas = RetryUtil.retry(operation, 3, e -> e instanceof SQLException, 1000);
    } catch (Exception e) {
      log.error("Failed to get schemas", e);
      throw new DataScopeException("Failed to get schemas", e);
    }
    return schemas;
  }

  @Override
  public List<String> getTables(Connection connection, String schema) {
    List<String> tables = new ArrayList<>();
    try {
      Callable<List<String>> operation =
          () -> {
            List<String> result = new ArrayList<>();
            try (PreparedStatement stmt =
                connection.prepareStatement(
                    "SELECT tablename FROM pg_tables WHERE schemaname = ?")) {
              stmt.setString(1, schema);
              try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                  result.add(rs.getString("tablename"));
                }
              }
            }
            return result;
          };
      tables = RetryUtil.retry(operation, 3, e -> e instanceof SQLException, 1000);
    } catch (Exception e) {
      log.error("Failed to get tables for schema {}", schema, e);
      throw new DataScopeException("Failed to get tables", e);
    }
    return tables;
  }

  @Override
  public boolean supports(DataSource dataSource) {
    return dataSource.getType() == DataSourceType.POSTGRESQL;
  }

  private ColumnType mapPostgreSQLType(String pgType) {
    if (pgType == null) {
      return ColumnType.VARCHAR;
    }

    pgType = pgType.toLowerCase();

    if (pgType.contains("char") || pgType.contains("text")) {
      return ColumnType.VARCHAR;
    } else if (pgType.contains("int") || pgType.equals("serial")) {
      return ColumnType.INTEGER;
    } else if (pgType.contains("float")
        || pgType.contains("numeric")
        || pgType.contains("decimal")
        || pgType.contains("double")
        || pgType.contains("real")) {
      return ColumnType.DECIMAL;
    } else if (pgType.contains("bool")) {
      return ColumnType.BOOLEAN;
    } else if (pgType.contains("date")) {
      return ColumnType.DATE;
    } else if (pgType.contains("time")) {
      return ColumnType.TIMESTAMP;
    } else if (pgType.contains("bytea")) {
      return ColumnType.BINARY;
    } else if (pgType.contains("json")) {
      return ColumnType.JSON;
    } else {
      return ColumnType.VARCHAR;
    }
  }
}
