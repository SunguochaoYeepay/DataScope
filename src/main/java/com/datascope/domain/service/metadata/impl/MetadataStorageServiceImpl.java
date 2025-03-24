package com.datascope.domain.service.metadata.impl;

import com.datascope.common.exception.DataScopeException;
import com.datascope.domain.entity.ColumnMetadataEntity;
import com.datascope.domain.entity.IndexMetadataEntity;
import com.datascope.domain.entity.TableMetadataEntity;
import com.datascope.domain.model.metadata.ColumnMetadata;
import com.datascope.domain.model.metadata.IndexMetadata;
import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.repository.ColumnMetadataRepository;
import com.datascope.domain.repository.IndexMetadataRepository;
import com.datascope.domain.repository.TableMetadataRepository;
import com.datascope.domain.service.metadata.MetadataStorageService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 元数据存储服务实现类 */
@Service
@RequiredArgsConstructor
public class MetadataStorageServiceImpl implements MetadataStorageService {

  private final TableMetadataRepository tableMetadataRepository;
  private final ColumnMetadataRepository columnMetadataRepository;
  private final IndexMetadataRepository indexMetadataRepository;

  @Override
  @Transactional
  public void saveTableMetadata(TableMetadata tableMetadata) {
    // 保存表元数据
    final TableMetadataEntity tableEntity = convertToTableEntity(tableMetadata);
    final TableMetadataEntity savedTableEntity = tableMetadataRepository.save(tableEntity);

    // 保存列元数据
    final List<ColumnMetadataEntity> columnEntities =
        tableMetadata.getColumns().stream()
            .map(column -> convertToColumnEntity(column, savedTableEntity.getId()))
            .collect(Collectors.toList());
    columnMetadataRepository.saveAll(columnEntities);

    // 保存索引元数据
    final List<IndexMetadataEntity> indexEntities =
        tableMetadata.getIndices().stream()
            .map(index -> convertToIndexEntity(index, savedTableEntity.getId()))
            .collect(Collectors.toList());
    indexMetadataRepository.saveAll(indexEntities);
  }

  @Override
  @Transactional
  public void saveTableMetadataBatch(List<TableMetadata> tableMetadataList) {
    tableMetadataList.forEach(this::saveTableMetadata);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TableMetadata> getTableMetadataByDataSource(String dataSourceId) {
    List<TableMetadataEntity> entities = tableMetadataRepository.findByDataSourceId(dataSourceId);
    return entities.stream().map(this::convertToTableMetadata).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TableMetadata> getTableMetadataBySchema(String dataSourceId, String schema) {
    List<TableMetadataEntity> entities =
        tableMetadataRepository.findByDataSourceIdAndSchema(dataSourceId, schema);
    return entities.stream().map(this::convertToTableMetadata).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public TableMetadata getTableMetadata(String dataSourceId, String schema, String tableName) {
    final TableMetadataEntity tableEntity =
        tableMetadataRepository
            .findByDataSourceIdAndSchemaAndName(dataSourceId, schema, tableName)
            .orElseThrow(() -> new DataScopeException("Table metadata not found"));

    final TableMetadata metadata = new TableMetadata();
    metadata.setDataSourceId(tableEntity.getDataSourceId());
    metadata.setSchema(tableEntity.getSchema());
    metadata.setName(tableEntity.getName());
    metadata.setComment(tableEntity.getComment());
    metadata.setRowCount(tableEntity.getRowCount());
    metadata.setDataSize(tableEntity.getDataSize());
    metadata.setIndexSize(tableEntity.getIndexSize());

    // 加载列信息
    final List<ColumnMetadataEntity> columnEntities =
        columnMetadataRepository.findByTableMetadataId(tableEntity.getId());
    columnEntities.forEach(
        columnEntity -> {
          final ColumnMetadata column = convertToColumnMetadata(columnEntity);
          metadata.addColumn(column);
        });

    // 加载索引信息
    final List<IndexMetadataEntity> indexEntities =
        indexMetadataRepository.findByTableMetadataId(tableEntity.getId());
    indexEntities.forEach(
        indexEntity -> {
          final IndexMetadata index = convertToIndexMetadata(indexEntity);
          metadata.addIndex(index);
        });

    return metadata;
  }

  @Override
  @Transactional(readOnly = true)
  public List<String> getSchemas(String dataSourceId) {
    return tableMetadataRepository.findSchemasByDataSourceId(dataSourceId);
  }

  @Override
  @Transactional
  public void deleteByDataSource(String dataSourceId) {
    List<TableMetadataEntity> tables = tableMetadataRepository.findByDataSourceId(dataSourceId);
    for (TableMetadataEntity table : tables) {
      columnMetadataRepository.deleteByTableMetadataId(table.getId());
      indexMetadataRepository.deleteByTableMetadataId(table.getId());
    }
    tableMetadataRepository.deleteByDataSourceId(dataSourceId);
  }

  private TableMetadataEntity convertToTableEntity(TableMetadata metadata) {
    TableMetadataEntity entity = new TableMetadataEntity();
    entity.setDataSourceId(metadata.getDataSourceId());
    entity.setSchema(metadata.getSchema());
    entity.setName(metadata.getName());
    entity.setComment(metadata.getComment());
    entity.setRowCount(metadata.getRowCount());
    entity.setDataSize(metadata.getDataSize());
    entity.setIndexSize(metadata.getIndexSize());
    return entity;
  }

  private ColumnMetadataEntity convertToColumnEntity(
      ColumnMetadata metadata, Long tableMetadataId) {
    ColumnMetadataEntity entity = new ColumnMetadataEntity();
    entity.setTableMetadataId(tableMetadataId);
    entity.setName(metadata.getName());
    entity.setType(metadata.getType());
    entity.setLength(metadata.getLength());
    entity.setPrecision(metadata.getPrecision());
    entity.setNullable(metadata.isNullable());
    entity.setPrimaryKey(metadata.isPrimaryKey());
    entity.setOrdinalPosition(metadata.getOrdinalPosition());
    entity.setDefaultValue(metadata.getDefaultValue());
    entity.setComment(metadata.getComment());
    entity.setAutoIncrement(metadata.isAutoIncrement());
    return entity;
  }

  private IndexMetadataEntity convertToIndexEntity(IndexMetadata metadata, Long tableMetadataId) {
    IndexMetadataEntity entity = new IndexMetadataEntity();
    entity.setTableMetadataId(tableMetadataId);
    entity.setName(metadata.getName());
    entity.setUnique(metadata.isUnique());
    entity.setCardinality(metadata.getCardinality());
    entity.setIndexSize(metadata.getIndexSize());
    entity.setColumnNames(String.join(",", metadata.getColumns()));
    return entity;
  }

  private TableMetadata convertToTableMetadata(TableMetadataEntity entity) {
    final TableMetadata metadata = new TableMetadata();
    metadata.setDataSourceId(entity.getDataSourceId());
    metadata.setSchema(entity.getSchema());
    metadata.setName(entity.getName());
    metadata.setComment(entity.getComment());
    metadata.setRowCount(entity.getRowCount());
    metadata.setDataSize(entity.getDataSize());
    metadata.setIndexSize(entity.getIndexSize());

    // 加载列信息
    final List<ColumnMetadataEntity> columnEntities =
        columnMetadataRepository.findByTableMetadataId(entity.getId());
    columnEntities.forEach(
        columnEntity -> {
          final ColumnMetadata column = convertToColumnMetadata(columnEntity);
          metadata.addColumn(column);
        });

    // 加载索引信息
    final List<IndexMetadataEntity> indexEntities =
        indexMetadataRepository.findByTableMetadataId(entity.getId());
    indexEntities.forEach(
        indexEntity -> {
          final IndexMetadata index = convertToIndexMetadata(indexEntity);
          metadata.addIndex(index);
        });

    return metadata;
  }

  private ColumnMetadata convertToColumnMetadata(ColumnMetadataEntity entity) {
    ColumnMetadata column = new ColumnMetadata();
    column.setName(entity.getName());
    column.setType(entity.getType());
    column.setLength(entity.getLength());
    column.setPrecision(entity.getPrecision());
    column.setNullable(entity.isNullable());
    column.setPrimaryKey(entity.isPrimaryKey());
    column.setOrdinalPosition(entity.getOrdinalPosition());
    column.setDefaultValue(entity.getDefaultValue());
    column.setComment(entity.getComment());
    column.setAutoIncrement(entity.isAutoIncrement());
    return column;
  }

  private IndexMetadata convertToIndexMetadata(IndexMetadataEntity entity) {
    IndexMetadata metadata = new IndexMetadata();
    metadata.setName(entity.getName());
    metadata.setUnique(entity.isUnique());
    metadata.setCardinality(entity.getCardinality());
    metadata.setIndexSize(entity.getIndexSize());

    // 从 columnNames 字符串中解析列名
    if (entity.getColumnNames() != null && !entity.getColumnNames().isEmpty()) {
      String[] columnNames = entity.getColumnNames().split(",");
      for (String columnName : columnNames) {
        metadata.addColumn(columnName.trim());
      }
    }

    return metadata;
  }
}
