package com.datascope.domain.service.metadata.impl;

import com.datascope.common.exception.DataScopeException;
import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.service.datasource.DataSourceService;
import com.datascope.domain.service.metadata.MetadataFetchService;
import com.datascope.domain.service.metadata.MetadataStorageService;
import com.datascope.domain.service.metadata.extractor.MetadataExtractor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 元数据获取服务实现类
 * 负责从数据库获取元数据并保存到系统中
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetadataFetchServiceImpl implements MetadataFetchService {

  private final DataSourceService dataSourceService;
  private final MetadataStorageService metadataStorageService;
  private final List<MetadataExtractor> metadataExtractors;

  @Override
  @Transactional
  public List<TableMetadata> fetchAndSaveAllMetadata(String dataSourceId) {
    try {
      // 获取数据源
      DataSourceVO dataSourceVO = dataSourceService.get(dataSourceId);
      DataSource dataSource = dataSourceVO.toEntity();

      // 获取适用的元数据抽取器
      MetadataExtractor extractor = getMetadataExtractor(dataSource);
      if (extractor == null) {
        throw new DataScopeException("Unsupported data source type: " + dataSource.getType());
      }

      // 获取数据库连接
      try (Connection connection = dataSourceService.getConnection(dataSource)) {
        // 提取所有表的元数据
        List<TableMetadata> tables = extractor.extractAll(dataSource, connection);
        
        // 保存元数据
        metadataStorageService.saveTableMetadataBatch(tables);
        
        log.info("Successfully fetched and saved metadata for data source: {}", dataSourceId);
        return tables;
      }
    } catch (Exception e) {
      log.error("Failed to fetch metadata for data source: {}", dataSourceId, e);
      throw new DataScopeException("Failed to fetch metadata: " + e.getMessage(), e);
    }
  }

  @Override
  @Transactional
  public TableMetadata fetchAndSaveTableMetadata(String dataSourceId, String schema, String tableName) {
    try {
      // 获取数据源
      DataSourceVO dataSourceVO = dataSourceService.get(dataSourceId);
      DataSource dataSource = dataSourceVO.toEntity();

      // 获取适用的元数据抽取器
      MetadataExtractor extractor = getMetadataExtractor(dataSource);
      if (extractor == null) {
        throw new DataScopeException("Unsupported data source type: " + dataSource.getType());
      }

      // 获取数据库连接
      try (Connection connection = dataSourceService.getConnection(dataSource)) {
        // 提取表的元数据
        TableMetadata table = extractor.extractTable(dataSource, connection, schema, tableName);
        if (table == null) {
          throw new DataScopeException("Table not found: " + schema + "." + tableName);
        }
        
        // 保存元数据
        metadataStorageService.saveTableMetadata(table);
        
        log.info("Successfully fetched and saved metadata for table: {}.{}", schema, tableName);
        return table;
      }
    } catch (Exception e) {
      log.error("Failed to fetch metadata for table: {}.{}", schema, tableName, e);
      throw new DataScopeException("Failed to fetch table metadata: " + e.getMessage(), e);
    }
  }

  /**
   * 获取适用于指定数据源的元数据抽取器
   *
   * @param dataSource 数据源
   * @return 元数据抽取器，如果没有找到适用的抽取器则返回null
   */
  private MetadataExtractor getMetadataExtractor(DataSource dataSource) {
    for (MetadataExtractor extractor : metadataExtractors) {
      if (extractor.supports(dataSource)) {
        return extractor;
      }
    }
    return null;
  }
} 