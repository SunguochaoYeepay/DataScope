package com.datascope.domain.service.metadata;

import com.datascope.domain.model.metadata.TableMetadata;
import java.util.List;

/**
 * 元数据获取服务接口
 * 负责从数据库获取元数据并保存到系统中
 */
public interface MetadataFetchService {

  /**
   * 获取并保存数据源的全部元数据
   *
   * @param dataSourceId 数据源ID
   * @return 获取并保存的表元数据列表
   */
  List<TableMetadata> fetchAndSaveAllMetadata(String dataSourceId);

  /**
   * 获取并保存特定表的元数据
   *
   * @param dataSourceId 数据源ID
   * @param schema 模式名
   * @param tableName 表名
   * @return 获取并保存的表元数据
   */
  TableMetadata fetchAndSaveTableMetadata(String dataSourceId, String schema, String tableName);
} 