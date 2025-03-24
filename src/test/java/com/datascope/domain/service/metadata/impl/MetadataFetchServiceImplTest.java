package com.datascope.domain.service.metadata.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.datascope.common.exception.DataScopeException;
import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.datasource.vo.DataSourceVO;
import com.datascope.domain.model.metadata.TableMetadata;
import com.datascope.domain.service.datasource.DataSourceService;
import com.datascope.domain.service.metadata.MetadataStorageService;
import com.datascope.domain.service.metadata.extractor.MetadataExtractor;
import java.sql.Connection;
import java.sql.SQLException;
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
class MetadataFetchServiceImplTest {

  @Mock
  private DataSourceService dataSourceService;

  @Mock
  private MetadataStorageService metadataStorageService;

  @Mock
  private List<MetadataExtractor> metadataExtractors;

  @Mock
  private MetadataExtractor metadataExtractor;

  @Mock
  private Connection connection;

  @InjectMocks
  private MetadataFetchServiceImpl metadataFetchService;

  private DataSourceVO dataSourceVO;
  private DataSource dataSource;
  private String dataSourceId = "test-datasource-id";
  private String schema = "test_schema";
  private String tableName = "test_table";

  @BeforeEach
  void setUp() {
    // 准备测试数据
    dataSourceVO = new DataSourceVO();
    dataSourceVO.setId(dataSourceId);
    dataSourceVO.setName("Test DataSource");
    dataSourceVO.setType(DataSourceType.MYSQL);
    dataSourceVO.setHost("localhost");
    dataSourceVO.setPort(3306);
    dataSourceVO.setUsername("test_user");
    dataSourceVO.setPassword("test_password");
    dataSourceVO.setDatabaseName("test_db");

    dataSource = dataSourceVO.toEntity();

    // 重置metadataExtractors
    metadataExtractors = new ArrayList<>();
    metadataExtractors.add(metadataExtractor);
    metadataFetchService = new MetadataFetchServiceImpl(
        dataSourceService, metadataStorageService, metadataExtractors);
  }

  @Test
  void fetchAndSaveAllMetadata_ShouldReturnTablesWhenSuccess() throws SQLException {
    // 准备测试数据
    List<TableMetadata> tables = Collections.singletonList(new TableMetadata());

    // 设置Mock行为
    when(dataSourceService.get(dataSourceId)).thenReturn(dataSourceVO);
    when(metadataExtractor.supports(any(DataSource.class))).thenReturn(true);
    when(dataSourceService.getConnection(any(DataSource.class))).thenReturn(connection);
    when(metadataExtractor.extractAll(any(DataSource.class), any(Connection.class))).thenReturn(tables);
    doNothing().when(metadataStorageService).saveTableMetadataBatch(tables);

    // 执行测试
    List<TableMetadata> result = metadataFetchService.fetchAndSaveAllMetadata(dataSourceId);

    // 验证结果
    assertNotNull(result);
    assertEquals(1, result.size());
    verify(dataSourceService, times(1)).get(dataSourceId);
    verify(dataSourceService, times(1)).getConnection(any(DataSource.class));
    verify(metadataExtractor, times(1)).extractAll(any(DataSource.class), any(Connection.class));
    verify(metadataStorageService, times(1)).saveTableMetadataBatch(tables);
  }

  @Test
  void fetchAndSaveAllMetadata_ShouldThrowExceptionWhenNoExtractorFound() throws SQLException {
    // 设置Mock行为 - 不支持的数据源类型
    when(dataSourceService.get(dataSourceId)).thenReturn(dataSourceVO);
    when(metadataExtractor.supports(any(DataSource.class))).thenReturn(false);

    // 执行测试并验证异常
    Exception exception = assertThrows(
        DataScopeException.class,
        () -> metadataFetchService.fetchAndSaveAllMetadata(dataSourceId));

    assertTrue(exception.getMessage().contains("Unsupported data source type"));
    verify(dataSourceService, times(1)).get(dataSourceId);
    verify(metadataExtractor, times(1)).supports(any(DataSource.class));
    verify(dataSourceService, never()).getConnection(any(DataSource.class));
  }

  @Test
  void fetchAndSaveTableMetadata_ShouldReturnTableWhenSuccess() throws SQLException {
    // 准备测试数据
    TableMetadata table = new TableMetadata();

    // 设置Mock行为
    when(dataSourceService.get(dataSourceId)).thenReturn(dataSourceVO);
    when(metadataExtractor.supports(any(DataSource.class))).thenReturn(true);
    when(dataSourceService.getConnection(any(DataSource.class))).thenReturn(connection);
    when(metadataExtractor.extractTable(any(DataSource.class), any(Connection.class), anyString(), anyString()))
        .thenReturn(table);
    doNothing().when(metadataStorageService).saveTableMetadata(table);

    // 执行测试
    TableMetadata result = metadataFetchService.fetchAndSaveTableMetadata(dataSourceId, schema, tableName);

    // 验证结果
    assertNotNull(result);
    verify(dataSourceService, times(1)).get(dataSourceId);
    verify(dataSourceService, times(1)).getConnection(any(DataSource.class));
    verify(metadataExtractor, times(1)).extractTable(
        any(DataSource.class), any(Connection.class), eq(schema), eq(tableName));
    verify(metadataStorageService, times(1)).saveTableMetadata(table);
  }

  @Test
  void fetchAndSaveTableMetadata_ShouldThrowExceptionWhenTableNotFound() throws SQLException {
    // 设置Mock行为 - 表不存在
    when(dataSourceService.get(dataSourceId)).thenReturn(dataSourceVO);
    when(metadataExtractor.supports(any(DataSource.class))).thenReturn(true);
    when(dataSourceService.getConnection(any(DataSource.class))).thenReturn(connection);
    when(metadataExtractor.extractTable(any(DataSource.class), any(Connection.class), anyString(), anyString()))
        .thenReturn(null);

    // 执行测试并验证异常
    Exception exception = assertThrows(
        DataScopeException.class,
        () -> metadataFetchService.fetchAndSaveTableMetadata(dataSourceId, schema, tableName));

    assertTrue(exception.getMessage().contains("Table not found"));
    verify(dataSourceService, times(1)).get(dataSourceId);
    verify(dataSourceService, times(1)).getConnection(any(DataSource.class));
    verify(metadataExtractor, times(1)).extractTable(
        any(DataSource.class), any(Connection.class), eq(schema), eq(tableName));
    verify(metadataStorageService, never()).saveTableMetadata(any(TableMetadata.class));
  }
}