package com.datascope.domain.service;

import com.datascope.domain.model.metadata.TableMetadata;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MetadataService {

  /** 提取数据源的元数据 */
  List<TableMetadata> extractMetadata(String dataSourceId);

  /** 刷新指定表的元数据 */
  TableMetadata refreshTableMetadata(String dataSourceId, String schema, String tableName);

  /** 获取表元数据 */
  Optional<TableMetadata> getTableMetadata(String dataSourceId, String schema, String tableName);

  /** 分页查询表元数据 */
  Page<TableMetadata> findTableMetadata(
      String dataSourceId, String schema, String tableName, Pageable pageable);

  /** 获取数据源下所有表 */
  List<TableMetadata> getAllTables(String dataSourceId);

  /** 获取数据源下指定模式的所有表 */
  List<TableMetadata> getSchemasTables(String dataSourceId, String schema);

  /** 获取数据源的所有模式 */
  List<String> getAllSchemas(String dataSourceId);

  /** 获取大表列表 */
  List<TableMetadata> getLargeTables(String dataSourceId, long minRows);

  /** 获取没有主键的表 */
  List<TableMetadata> getTablesWithoutPrimaryKey(String dataSourceId);

  /** 获取元数据统计信息 */
  MetadataStatistics getMetadataStatistics(String dataSourceId);

  /** 元数据统计信息 */
  interface MetadataStatistics {
    long getTotalTables();

    long getTotalColumns();

    long getTotalIndexes();

    long getTablesWithoutPrimaryKey();

    long getTablesWithoutIndexes();

    long getLargeTables();

    double getAverageColumnsPerTable();

    double getAverageIndexesPerTable();

    long getTotalDataSize();

    long getTotalIndexSize();
  }
}
