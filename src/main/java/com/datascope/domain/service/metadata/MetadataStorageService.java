package com.datascope.domain.service.metadata;

import com.datascope.domain.model.metadata.TableMetadata;
import java.util.List;

public interface MetadataStorageService {
    
    /**
     * 保存表元数据
     */
    void saveTableMetadata(TableMetadata tableMetadata);
    
    /**
     * 批量保存表元数据
     */
    void saveTableMetadataBatch(List<TableMetadata> tableMetadataList);
    
    /**
     * 获取数据源下的所有表元数据
     */
    List<TableMetadata> getTableMetadataByDataSource(Long dataSourceId);
    
    /**
     * 获取指定schema下的所有表元数据
     */
    List<TableMetadata> getTableMetadataBySchema(Long dataSourceId, String schema);
    
    /**
     * 获取指定表的元数据
     */
    TableMetadata getTableMetadata(Long dataSourceId, String schema, String tableName);
    
    /**
     * 获取数据源下的所有schema
     */
    List<String> getSchemas(Long dataSourceId);
    
    /**
     * 删除数据源的所有元数据
     */
    void deleteByDataSource(Long dataSourceId);
}