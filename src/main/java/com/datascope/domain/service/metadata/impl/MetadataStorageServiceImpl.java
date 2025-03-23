package com.datascope.domain.service.metadata.impl;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        TableMetadataEntity tableEntity = convertToTableEntity(tableMetadata);
        tableEntity = tableMetadataRepository.save(tableEntity);

        // 保存列元数据
        List<ColumnMetadataEntity> columnEntities = tableMetadata.getColumns().stream()
            .map(column -> convertToColumnEntity(column, tableEntity.getId()))
            .collect(Collectors.toList());
        columnMetadataRepository.saveAll(columnEntities);

        // 保存索引元数据
        List<IndexMetadataEntity> indexEntities = tableMetadata.getIndices().stream()
            .map(index -> convertToIndexEntity(index, tableEntity.getId()))
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
    public List<TableMetadata> getTableMetadataByDataSource(Long dataSourceId) {
        return tableMetadataRepository.findByDataSourceId(dataSourceId).stream()
            .map(this::convertToTableMetadata)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableMetadata> getTableMetadataBySchema(Long dataSourceId, String schema) {
        return tableMetadataRepository.findByDataSourceIdAndSchema(dataSourceId, schema).stream()
            .map(this::convertToTableMetadata)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TableMetadata getTableMetadata(Long dataSourceId, String schema, String tableName) {
        return tableMetadataRepository.findByDataSourceIdAndSchemaAndName(dataSourceId, schema, tableName)
            .map(this::convertToTableMetadata)
            .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSchemas(Long dataSourceId) {
        return tableMetadataRepository.findSchemasByDataSourceId(dataSourceId);
    }

    @Override
    @Transactional
    public void deleteByDataSource(Long dataSourceId) {
        List<TableMetadataEntity> tables = tableMetadataRepository.findByDataSourceId(dataSourceId);
        tables.forEach(table -> {
            columnMetadataRepository.deleteByTableMetadataId(table.getId());
            indexMetadataRepository.deleteByTableMetadataId(table.getId());
        });
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

    private ColumnMetadataEntity convertToColumnEntity(ColumnMetadata metadata, Long tableMetadataId) {
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
        TableMetadata metadata = new TableMetadata();
        metadata.setDataSourceId(entity.getDataSourceId());
        metadata.setSchema(entity.getSchema());
        metadata.setName(entity.getName());
        metadata.setComment(entity.getComment());
        metadata.setRowCount(entity.getRowCount());
        metadata.setDataSize(entity.getDataSize());
        metadata.setIndexSize(entity.getIndexSize());

        // 加载列信息
        List<ColumnMetadataEntity> columnEntities = columnMetadataRepository.findByTableMetadataId(entity.getId());
        columnEntities.forEach(columnEntity -> {
            ColumnMetadata column = new ColumnMetadata();
            column.setName(columnEntity.getName());
            column.setType(columnEntity.getType());
            column.setLength(columnEntity.getLength());
            column.setPrecision(columnEntity.getPrecision());
            column.setNullable(columnEntity.isNullable());
            column.setPrimaryKey(columnEntity.isPrimaryKey());
            column.setOrdinalPosition(columnEntity.getOrdinalPosition());
            column.setDefaultValue(columnEntity.getDefaultValue());
            column.setComment(columnEntity.getComment());
            column.setAutoIncrement(columnEntity.isAutoIncrement());
            metadata.addColumn(column);
        });

        // 加载索引信息
        List<IndexMetadataEntity> indexEntities = indexMetadataRepository.findByTableMetadataId(entity.getId());
        indexEntities.forEach(indexEntity -> {
            IndexMetadata index = new IndexMetadata();
            index.setName(indexEntity.getName());
            index.setUnique(indexEntity.isUnique());
            index.setCardinality(indexEntity.getCardinality());
            index.setIndexSize(indexEntity.getIndexSize());
            for (String columnName : indexEntity.getColumnNames().split(",")) {
                index.addColumn(columnName);
            }
            metadata.addIndex(index);
        });

        return metadata;
    }
}