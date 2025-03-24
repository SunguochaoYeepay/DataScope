package com.datascope.domain.service.metadata.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.datascope.common.exception.DataScopeException;
import com.datascope.domain.entity.ColumnMetadataEntity;
import com.datascope.domain.entity.TableMetadataEntity;
import com.datascope.domain.model.metadata.ColumnMetadata;
import com.datascope.domain.model.metadata.ColumnType;
import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.repository.ColumnMetadataRepository;
import com.datascope.domain.repository.IndexMetadataRepository;
import com.datascope.domain.repository.TableMetadataRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetadataStorageServiceImplTest {

  @Mock
  private TableMetadataRepository tableMetadataRepository;

  @Mock
  private ColumnMetadataRepository columnMetadataRepository;
  
  @Mock
  private IndexMetadataRepository indexMetadataRepository;

  @InjectMocks
  private MetadataStorageServiceImpl metadataStorageService;

  private final String dataSourceId = "test-datasource-id";
  private final String schema = "test_schema";
  private final String tableName = "test_table";
  private TableMetadata tableMetadata;
  private TableMetadataEntity tableMetadataEntity;
  private List<ColumnMetadata> columnMetadataList;
  private List<ColumnMetadataEntity> columnMetadataEntities;

  @BeforeEach
  void setUp() {
    // 初始化测试数据
    tableMetadata = new TableMetadata();
    tableMetadata.setDataSourceId(dataSourceId);
    tableMetadata.setSchema(schema);
    tableMetadata.setName(tableName);
    tableMetadata.setComment("测试表");
    tableMetadata.setRowCount(100L);
    tableMetadata.setDataSize(1024L);
    tableMetadata.setIndexSize(256L);

    ColumnMetadata column1 = new ColumnMetadata();
    column1.setName("column1");
    column1.setType(ColumnType.VARCHAR);
    column1.setLength(255);
    column1.setNullable(true);
    column1.setPrimaryKey(false);
    column1.setOrdinalPosition(1);
    column1.setComment("第一列");

    ColumnMetadata column2 = new ColumnMetadata();
    column2.setName("column2");
    column2.setType(ColumnType.INTEGER);
    column2.setNullable(false);
    column2.setPrimaryKey(true);
    column2.setOrdinalPosition(2);
    column2.setComment("第二列");

    columnMetadataList = Arrays.asList(column1, column2);
    
    // 设置实体对象
    tableMetadataEntity = new TableMetadataEntity();
    tableMetadataEntity.setId(1L);
    tableMetadataEntity.setDataSourceId(dataSourceId);
    tableMetadataEntity.setSchema(schema);
    tableMetadataEntity.setName(tableName);
    tableMetadataEntity.setComment("测试表");
    tableMetadataEntity.setRowCount(100L);
    tableMetadataEntity.setDataSize(1024L);
    tableMetadataEntity.setIndexSize(256L);
    
    columnMetadataEntities = new ArrayList<>();
    ColumnMetadataEntity columnEntity1 = new ColumnMetadataEntity();
    columnEntity1.setId(1L);
    columnEntity1.setTableMetadataId(tableMetadataEntity.getId());
    columnEntity1.setName("column1");
    columnEntity1.setType(ColumnType.VARCHAR);
    columnEntity1.setLength(255);
    columnEntity1.setNullable(true);
    columnEntity1.setPrimaryKey(false);
    columnEntity1.setOrdinalPosition(1);
    columnEntity1.setComment("第一列");
    
    ColumnMetadataEntity columnEntity2 = new ColumnMetadataEntity();
    columnEntity2.setId(2L);
    columnEntity2.setTableMetadataId(tableMetadataEntity.getId());
    columnEntity2.setName("column2");
    columnEntity2.setType(ColumnType.INTEGER);
    columnEntity2.setNullable(false);
    columnEntity2.setPrimaryKey(true);
    columnEntity2.setOrdinalPosition(2);
    columnEntity2.setComment("第二列");
    
    columnMetadataEntities.add(columnEntity1);
    columnMetadataEntities.add(columnEntity2);
  }

  @Test
  void getTableMetadataByDataSource_ShouldReturnTablesList() {
    // 准备测试数据
    List<TableMetadataEntity> expectedEntities = Collections.singletonList(tableMetadataEntity);

    // 设置Mock行为
    when(tableMetadataRepository.findByDataSourceId(dataSourceId)).thenReturn(expectedEntities);
    when(columnMetadataRepository.findByTableMetadataId(tableMetadataEntity.getId())).thenReturn(columnMetadataEntities);

    // 执行测试
    List<TableMetadata> resultTables = metadataStorageService.getTableMetadataByDataSource(dataSourceId);

    // 验证结果
    assertNotNull(resultTables);
    assertEquals(1, resultTables.size());
    assertEquals(dataSourceId, resultTables.get(0).getDataSourceId());
    assertEquals(schema, resultTables.get(0).getSchema());
    assertEquals(tableName, resultTables.get(0).getName());
    assertEquals(2, resultTables.get(0).getColumns().size());
    verify(tableMetadataRepository, times(1)).findByDataSourceId(dataSourceId);
    verify(columnMetadataRepository, times(1)).findByTableMetadataId(tableMetadataEntity.getId());
  }

  @Test
  void getTableMetadata_ShouldReturnTableMetadata() {
    // 设置Mock行为
    when(tableMetadataRepository.findByDataSourceIdAndSchemaAndName(dataSourceId, schema, tableName))
        .thenReturn(Optional.of(tableMetadataEntity));
    when(columnMetadataRepository.findByTableMetadataId(tableMetadataEntity.getId())).thenReturn(columnMetadataEntities);

    // 执行测试
    TableMetadata result = metadataStorageService.getTableMetadata(dataSourceId, schema, tableName);

    // 验证结果
    assertNotNull(result);
    assertEquals(tableMetadataEntity.getDataSourceId(), result.getDataSourceId());
    assertEquals(tableMetadataEntity.getName(), result.getName());
    assertEquals(tableMetadataEntity.getSchema(), result.getSchema());
    assertEquals(2, result.getColumns().size());
    verify(tableMetadataRepository, times(1)).findByDataSourceIdAndSchemaAndName(dataSourceId, schema, tableName);
    verify(columnMetadataRepository, times(1)).findByTableMetadataId(tableMetadataEntity.getId());
  }

  @Test
  void getTableMetadata_ShouldThrowException_WhenTableNotFound() {
    // 设置Mock行为
    when(tableMetadataRepository.findByDataSourceIdAndSchemaAndName(dataSourceId, schema, tableName))
        .thenReturn(Optional.empty());

    // 执行测试并验证结果
    assertThrows(DataScopeException.class, () -> {
      metadataStorageService.getTableMetadata(dataSourceId, schema, tableName);
    });
    verify(tableMetadataRepository, times(1)).findByDataSourceIdAndSchemaAndName(dataSourceId, schema, tableName);
  }

  @Test
  void saveTableMetadata_ShouldSaveTableAndColumns() {
    // 设置Mock行为
    when(tableMetadataRepository.save(any(TableMetadataEntity.class))).thenReturn(tableMetadataEntity);
    when(columnMetadataRepository.saveAll(anyList())).thenReturn(columnMetadataEntities);

    // 执行测试
    metadataStorageService.saveTableMetadata(tableMetadata);

    // 验证结果
    verify(tableMetadataRepository, times(1)).save(any(TableMetadataEntity.class));
    verify(columnMetadataRepository, times(1)).saveAll(anyList());
  }

  @Test
  void saveTableMetadataBatch_ShouldSaveAllTablesAndColumns() {
    // 准备测试数据
    List<TableMetadata> tables = Collections.singletonList(tableMetadata);

    // 设置Mock行为
    when(tableMetadataRepository.save(any(TableMetadataEntity.class))).thenReturn(tableMetadataEntity);
    when(columnMetadataRepository.saveAll(anyList())).thenReturn(columnMetadataEntities);

    // 执行测试
    metadataStorageService.saveTableMetadataBatch(tables);

    // 验证结果
    verify(tableMetadataRepository, times(1)).save(any(TableMetadataEntity.class));
    verify(columnMetadataRepository, times(1)).saveAll(anyList());
  }

  @Test
  void deleteByDataSource_ShouldDeleteTablesAndColumns() {
    // 准备测试数据
    List<TableMetadataEntity> tables = Collections.singletonList(tableMetadataEntity);
    
    // 设置Mock行为
    when(tableMetadataRepository.findByDataSourceId(dataSourceId)).thenReturn(tables);
    doNothing().when(columnMetadataRepository).deleteByTableMetadataId(tableMetadataEntity.getId());
    doNothing().when(indexMetadataRepository).deleteByTableMetadataId(tableMetadataEntity.getId());
    doNothing().when(tableMetadataRepository).deleteByDataSourceId(dataSourceId);

    // 执行测试
    metadataStorageService.deleteByDataSource(dataSourceId);

    // 验证结果
    verify(tableMetadataRepository, times(1)).findByDataSourceId(dataSourceId);
    verify(columnMetadataRepository, times(1)).deleteByTableMetadataId(tableMetadataEntity.getId());
    verify(indexMetadataRepository, times(1)).deleteByTableMetadataId(tableMetadataEntity.getId());
    verify(tableMetadataRepository, times(1)).deleteByDataSourceId(dataSourceId);
  }
} 