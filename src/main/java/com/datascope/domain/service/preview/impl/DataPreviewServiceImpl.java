package com.datascope.domain.service.preview.impl;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.model.metadata.ColumnMetadata;
import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.model.preview.DataPreviewRequest;
import com.datascope.domain.model.preview.DataPreviewResponse;
import com.datascope.domain.service.datasource.DataSourceService;
import com.datascope.domain.service.metadata.MetadataStorageService;
import com.datascope.domain.service.preview.DataPreviewService;
import java.sql.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataPreviewServiceImpl implements DataPreviewService {

  private final DataSourceService dataSourceService;
  private final MetadataStorageService metadataStorageService;

  @Override
  public DataPreviewResponse previewData(DataPreviewRequest request) {
    long startTime = System.currentTimeMillis();
    DataPreviewResponse response = new DataPreviewResponse();

    // 参数验证
    if (request == null) {
      throw new IllegalArgumentException("请求不能为空");
    }
    if (request.getDataSourceId() == null || request.getDataSourceId().trim().isEmpty()) {
      throw new IllegalArgumentException("数据源ID不能为空");
    }
    if (request.getSchema() == null || request.getSchema().trim().isEmpty()) {
      throw new IllegalArgumentException("Schema不能为空");
    }
    if (request.getTableName() == null || request.getTableName().trim().isEmpty()) {
      throw new IllegalArgumentException("表名不能为空");
    }

    try {
      // 获取数据源
      DataSourceVO dataSourceVO = dataSourceService.get(request.getDataSourceId());
      DataSource dataSource = dataSourceVO.toEntity();

      // 获取表元数据
      TableMetadata tableMetadata =
          metadataStorageService.getTableMetadata(
              request.getDataSourceId(), request.getSchema(), request.getTableName());
      if (tableMetadata == null) {
        throw new IllegalArgumentException(
            "Table not found: " + request.getSchema() + "." + request.getTableName());
      }

      // 设置列信息
      response.setColumns(tableMetadata.getColumns());

      // 构建SQL
      String sql = buildPreviewSql(request, tableMetadata);

      // 执行查询
      try (Connection conn = dataSourceService.getConnection(dataSource);
          PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setMaxRows(request.getSampleSize());
        try (ResultSet rs = stmt.executeQuery()) {
          // 获取总行数
          response.setTotalRows(getTotalRows(conn, request, tableMetadata));

          // 获取采样数据
          List<Map<String, Object>> rows = new ArrayList<>();
          while (rs.next() && rows.size() < request.getSampleSize()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (ColumnMetadata column : tableMetadata.getColumns()) {
              if (!request.isIncludeSystemColumns() && isSystemColumn(column)) {
                continue;
              }
              Object value = getColumnValue(rs, column);
              row.put(column.getName(), value);
            }
            rows.add(row);
          }
          response.setRows(rows);
        }
      }

      response.setSampleSize(request.getSampleSize());
      response.setExecutionTime(System.currentTimeMillis() - startTime);
      return response;

    } catch (Exception e) {
      log.error("Failed to preview data", e);
      throw new RuntimeException("Failed to preview data: " + e.getMessage(), e);
    }
  }

  private String buildPreviewSql(DataPreviewRequest request, TableMetadata tableMetadata) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT ");

    // 添加列
    List<String> columnNames = new ArrayList<>();
    for (ColumnMetadata column : tableMetadata.getColumns()) {
      if (!request.isIncludeSystemColumns() && isSystemColumn(column)) {
        continue;
      }
      columnNames.add(quoteIdentifier(column.getName()));
    }
    sql.append(String.join(", ", columnNames));

    // 添加表
    sql.append(" FROM ")
        .append(quoteIdentifier(request.getSchema()))
        .append(".")
        .append(quoteIdentifier(request.getTableName()));

    // 添加where条件
    if (StringUtils.hasText(request.getWhereClause())) {
      sql.append(" WHERE ").append(request.getWhereClause());
    }

    // 添加排序
    if (StringUtils.hasText(request.getOrderBy())) {
      sql.append(" ORDER BY ")
          .append(quoteIdentifier(request.getOrderBy()))
          .append(request.isDesc() ? " DESC" : " ASC");
    }

    return sql.toString();
  }

  private Long getTotalRows(
      Connection conn, DataPreviewRequest request, TableMetadata tableMetadata) {
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT COUNT(*) FROM ")
        .append(quoteIdentifier(request.getSchema()))
        .append(".")
        .append(quoteIdentifier(request.getTableName()));

    if (StringUtils.hasText(request.getWhereClause())) {
      sql.append(" WHERE ").append(request.getWhereClause());
    }

    try (PreparedStatement stmt = conn.prepareStatement(sql.toString());
        ResultSet rs = stmt.executeQuery()) {
      if (rs.next()) {
        return rs.getLong(1);
      }
      return 0L;
    } catch (SQLException e) {
      log.error("Failed to get total rows", e);
      return 0L;
    }
  }

  private boolean isSystemColumn(ColumnMetadata column) {
    // 根据实际需求定义系统列的判断逻辑
    String name = column.getName().toLowerCase();
    return name.startsWith("sys_")
        || name.equals("created_at")
        || name.equals("updated_at")
        || name.equals("created_by")
        || name.equals("updated_by");
  }

  private String quoteIdentifier(String identifier) {
    // 根据不同的数据库类型使用不同的标识符引用方式
    // MySQL使用反引号，PostgreSQL使用双引号
    return "`" + identifier.replace("`", "``") + "`";
  }

  private Object getColumnValue(ResultSet rs, ColumnMetadata column) throws SQLException {
    switch (column.getType()) {
      case TINYINT:
      case SMALLINT:
      case INTEGER:
        return rs.getInt(column.getName());
      case BIGINT:
        return rs.getLong(column.getName());
      case FLOAT:
        return rs.getFloat(column.getName());
      case DOUBLE:
        return rs.getDouble(column.getName());
      case DECIMAL:
        return rs.getBigDecimal(column.getName());
      case CHAR:
      case VARCHAR:
      case TEXT:
        return rs.getString(column.getName());
      case DATE:
        return rs.getDate(column.getName());
      case TIME:
        return rs.getTime(column.getName());
      case TIMESTAMP:
        return rs.getTimestamp(column.getName());
      case BOOLEAN:
        return rs.getBoolean(column.getName());
      case BINARY:
      case VARBINARY:
      case BLOB:
        return rs.getBytes(column.getName());
      case JSON:
        String jsonStr = rs.getString(column.getName());
        return jsonStr != null ? jsonStr : null;
      default:
        return rs.getString(column.getName());
    }
  }
}
