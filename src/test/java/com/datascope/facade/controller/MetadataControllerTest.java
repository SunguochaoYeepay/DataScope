package com.datascope.facade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.service.metadata.MetadataFetchService;
import com.datascope.domain.service.metadata.MetadataStorageService;
import com.datascope.infrastructure.common.response.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetadataControllerTest {

  @Mock
  private MetadataStorageService metadataStorageService;

  @Mock
  private MetadataFetchService metadataFetchService;

  @InjectMocks
  private MetadataController metadataController;

  private final String dataSourceId = "test-datasource-id";
  private final String schema = "test_schema";
  private final String tableName = "test_table";

  @BeforeEach
  void setUp() {
    // 初始化测试数据
  }

  @Test
  void listTables_ShouldReturnTablesList() {
    // 准备测试数据
    List<TableMetadata> tables = new ArrayList<>();
    tables.add(new TableMetadata());

    // 设置Mock行为
    when(metadataStorageService.getTableMetadataByDataSource(dataSourceId)).thenReturn(tables);

    // 执行测试
    R<List<TableMetadata>> response = metadataController.listTables(dataSourceId);

    // 验证结果
    assertEquals(200, response.getCode());
    assertEquals(1, response.getData().size());
    verify(metadataStorageService, times(1)).getTableMetadataByDataSource(dataSourceId);
  }

  @Test
  void getTable_ShouldReturnTableMetadata() {
    // 准备测试数据
    TableMetadata tableMetadata = new TableMetadata();
    tableMetadata.setName(tableName);
    tableMetadata.setSchema(schema);

    // 设置Mock行为
    when(metadataStorageService.getTableMetadata(dataSourceId, schema, tableName)).thenReturn(tableMetadata);

    // 执行测试
    R<TableMetadata> response = metadataController.getTable(dataSourceId, schema, tableName);

    // 验证结果
    assertEquals(200, response.getCode());
    assertEquals(tableName, response.getData().getName());
    assertEquals(schema, response.getData().getSchema());
    verify(metadataStorageService, times(1)).getTableMetadata(dataSourceId, schema, tableName);
  }

  @Test
  void refreshMetadata_ShouldDeleteAndFetchMetadata() {
    // 准备测试数据
    List<TableMetadata> tables = Collections.singletonList(new TableMetadata());

    // 设置Mock行为
    doNothing().when(metadataStorageService).deleteByDataSource(dataSourceId);
    when(metadataFetchService.fetchAndSaveAllMetadata(dataSourceId)).thenReturn(tables);

    // 执行测试
    R<Void> response = metadataController.refreshMetadata(dataSourceId);

    // 验证结果
    assertEquals(200, response.getCode());
    verify(metadataStorageService, times(1)).deleteByDataSource(dataSourceId);
    verify(metadataFetchService, times(1)).fetchAndSaveAllMetadata(dataSourceId);
  }

  @Test
  void refreshTableMetadata_ShouldFetchAndSaveTableMetadata() {
    // 准备测试数据
    TableMetadata tableMetadata = new TableMetadata();
    tableMetadata.setName(tableName);
    tableMetadata.setSchema(schema);

    // 设置Mock行为
    when(metadataFetchService.fetchAndSaveTableMetadata(dataSourceId, schema, tableName)).thenReturn(tableMetadata);

    // 执行测试
    R<Void> response = metadataController.refreshTableMetadata(dataSourceId, schema, tableName);

    // 验证结果
    assertEquals(200, response.getCode());
    verify(metadataFetchService, times(1)).fetchAndSaveTableMetadata(dataSourceId, schema, tableName);
  }
} 