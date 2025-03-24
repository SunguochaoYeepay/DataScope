package com.datascope.domain.service.metadata;

import com.datascope.domain.model.metadata.TableMetadata;
import java.util.List;

/** 元数据存储服务接口，用于管理数据源的元数据信息. */
public interface MetadataStorageService {

  /**
   * 保存表元数据.
   *
   * @param tableMetadata 要保存的表元数据
   */
  void saveTableMetadata(TableMetadata tableMetadata);

  /**
   * 批量保存表元数据.
   *
   * @param tableMetadataList 要保存的表元数据列表
   */
  void saveTableMetadataBatch(List<TableMetadata> tableMetadataList);

  /**
   * 获取数据源下的所有表元数据.
   *
   * @param dataSourceId 数据源ID
   * @return 表元数据列表
   */
  List<TableMetadata> getTableMetadataByDataSource(String dataSourceId);

  /**
   * 获取指定schema下的所有表元数据.
   *
   * @param dataSourceId 数据源ID
   * @param schema schema名称
   * @return 表元数据列表
   */
  List<TableMetadata> getTableMetadataBySchema(String dataSourceId, String schema);

  /**
   * 获取指定表的元数据.
   *
   * @param dataSourceId 数据源ID
   * @param schema schema名称
   * @param tableName 表名
   * @return 表元数据
   */
  TableMetadata getTableMetadata(String dataSourceId, String schema, String tableName);

  /**
   * 获取数据源下的所有schema.
   *
   * @param dataSourceId 数据源ID
   * @return schema名称列表
   */
  List<String> getSchemas(String dataSourceId);

  /**
   * 删除数据源的所有元数据.
   *
   * @param dataSourceId 数据源ID
   */
  void deleteByDataSource(String dataSourceId);
}
