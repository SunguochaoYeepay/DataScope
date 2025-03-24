package com.datascope.domain.service.metadata.extractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.metadata.TableMetadata;

public class MySQLMetadataExtractorTest {

  private MySQLMetadataExtractor extractor;

  @Mock
  private Connection mockConnection;
  
  @Mock
  private Statement mockStatement;
  
  @Mock
  private PreparedStatement mockPreparedStatement;
  
  @Mock
  private ResultSet mockResultSet;
  
  @Mock
  private ResultSet mockTablesResultSet;
  
  @Mock
  private ResultSet mockColumnsResultSet;
  
  @Mock
  private ResultSet mockIndexResultSet;
  
  @Mock
  private ResultSet mockPrimaryKeysResultSet;
  
  @Mock
  private ResultSet mockIndexStatsResultSet;
  
  @Mock
  private DatabaseMetaData mockDatabaseMetaData;
  
  @Mock
  private DataSource mockDataSource;

  @BeforeEach
  void setUp() throws Exception {
    mockDataSource = mock(DataSource.class);
    mockConnection = mock(Connection.class);
    mockStatement = mock(Statement.class);
    mockPreparedStatement = mock(PreparedStatement.class);
    mockDatabaseMetaData = mock(DatabaseMetaData.class);
    mockResultSet = mock(ResultSet.class);

    // 设置DataSource模拟行为
    when(mockDataSource.getType()).thenReturn(DataSourceType.MYSQL);
    when(mockDataSource.getId()).thenReturn("test-mysql-ds");
    // DataSource接口没有getConnection方法，在测试中我们直接传入mockConnection

    // 设置Connection模拟行为
    when(mockConnection.createStatement()).thenReturn(mockStatement);
    when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    when(mockConnection.getMetaData()).thenReturn(mockDatabaseMetaData);

    // 为PreparedStatement的executeQuery设置mock返回值
    ResultSet tableInfoResultSet = mockResultSetForTableInfo();
    when(mockPreparedStatement.executeQuery()).thenReturn(tableInfoResultSet);

    // 准备各种ResultSet
    ResultSet schemasResultSet = mockResultSetForSchemas();
    ResultSet tablesResultSet = mockResultSetForTables();
    ResultSet columnsResultSet = mockResultSetForColumns();
    ResultSet indexesResultSet = mockResultSetForIndexes();
    ResultSet primaryKeysResultSet = mockResultSetForPrimaryKeys();

    // 设置DatabaseMetaData模拟行为
    // 1. 获取所有schema
    when(mockDatabaseMetaData.getCatalogs()).thenReturn(schemasResultSet);
    when(mockStatement.executeQuery(contains("SHOW DATABASES"))).thenReturn(schemasResultSet);

    // 2. 获取表信息
    String[] tableTypes = new String[]{"TABLE", "VIEW"};
    when(mockDatabaseMetaData.getTables(isNull(), eq("test_db"), isNull(), eq(tableTypes)))
        .thenReturn(tablesResultSet);

    // 3. 获取列信息
    when(mockDatabaseMetaData.getColumns(isNull(), eq("test_db"), eq("users"), isNull()))
        .thenReturn(columnsResultSet);
    when(mockDatabaseMetaData.getColumns(isNull(), eq("test_db"), eq("orders"), isNull()))
        .thenReturn(columnsResultSet);

    // 4. 获取索引信息
    when(mockDatabaseMetaData.getIndexInfo(isNull(), eq("test_db"), eq("users"), eq(false), eq(false)))
        .thenReturn(indexesResultSet);
    when(mockDatabaseMetaData.getIndexInfo(isNull(), eq("test_db"), eq("orders"), eq(false), eq(false)))
        .thenReturn(indexesResultSet);

    // 5. 获取主键信息
    when(mockDatabaseMetaData.getPrimaryKeys(isNull(), eq("test_db"), eq("users")))
        .thenReturn(primaryKeysResultSet);
    when(mockDatabaseMetaData.getPrimaryKeys(isNull(), eq("test_db"), eq("orders")))
        .thenReturn(primaryKeysResultSet);

    // 初始化测试对象
    extractor = new MySQLMetadataExtractor();
  }

  private ResultSet mockResultSetForTableInfo() throws SQLException {
    ResultSet tableInfoResultSet = mock(ResultSet.class);
    
    // 模拟表信息
    when(tableInfoResultSet.next()).thenReturn(true, true, false);
    when(tableInfoResultSet.getString("table_comment")).thenReturn("用户表", "订单表");
    when(tableInfoResultSet.getLong("table_rows")).thenReturn(100L, 200L);
    when(tableInfoResultSet.getLong("data_length")).thenReturn(1024L, 2048L);
    when(tableInfoResultSet.getLong("index_length")).thenReturn(512L, 1024L);
    
    return tableInfoResultSet;
  }

  private ResultSet mockResultSetForSchemas() throws SQLException {
    ResultSet schemasResultSet = mock(ResultSet.class);
    
    // 模拟迭代：返回三个数据库，其中一个是测试数据库，两个是系统数据库
    when(schemasResultSet.next())
        .thenReturn(true)  // information_schema (系统schema)
        .thenReturn(true)  // mysql (系统schema)
        .thenReturn(true)  // test_db (用户schema)
        .thenReturn(false);
    
    // 模拟获取schema名称
    when(schemasResultSet.getString(1))
        .thenReturn("information_schema")
        .thenReturn("mysql")
        .thenReturn("test_db");

    // 确保当执行statement时，返回schemasResultSet
    when(mockStatement.executeQuery(anyString())).thenReturn(schemasResultSet);
    
    return schemasResultSet;
  }
  
  private ResultSet mockResultSetForTables() throws SQLException {
    ResultSet tablesResultSet = mock(ResultSet.class);
    
    // 表迭代模拟：返回两个表，然后结束
    when(tablesResultSet.next())
        .thenReturn(true)  // users表
        .thenReturn(true)  // orders表
        .thenReturn(false);
    
    // 表名和类型模拟
    when(tablesResultSet.getString("TABLE_NAME"))
        .thenReturn("users")
        .thenReturn("orders");
    
    when(tablesResultSet.getString("TABLE_TYPE"))
        .thenReturn("BASE TABLE")
        .thenReturn("BASE TABLE");
    
    when(tablesResultSet.getString("REMARKS"))
        .thenReturn("用户表")
        .thenReturn("订单表");
    
    // 配置metadata.getTables调用返回tablesResultSet
    when(mockDatabaseMetaData.getTables(anyString(), eq("test_db"), anyString(), any(String[].class)))
        .thenReturn(tablesResultSet);
    
    return tablesResultSet;
  }
  
  private ResultSet mockResultSetForColumns() throws SQLException {
    ResultSet columnsResultSet = mock(ResultSet.class);
    
    // 列迭代模拟：返回两个列，然后结束
    when(columnsResultSet.next())
        .thenReturn(true)  // id列
        .thenReturn(true)  // name列
        .thenReturn(false);
    
    // 列信息模拟
    when(columnsResultSet.getString("COLUMN_NAME"))
        .thenReturn("id")
        .thenReturn("name");
    
    when(columnsResultSet.getInt("DATA_TYPE"))
        .thenReturn(Types.INTEGER)
        .thenReturn(Types.VARCHAR);
    
    when(columnsResultSet.getString("TYPE_NAME"))
        .thenReturn("INT")
        .thenReturn("VARCHAR");
    
    when(columnsResultSet.getInt("COLUMN_SIZE"))
        .thenReturn(11)
        .thenReturn(255);
    
    when(columnsResultSet.getInt("DECIMAL_DIGITS"))
        .thenReturn(0)
        .thenReturn(0);
    
    when(columnsResultSet.getInt("NULLABLE"))
        .thenReturn(DatabaseMetaData.columnNoNulls)
        .thenReturn(DatabaseMetaData.columnNullable);
    
    when(columnsResultSet.getString("COLUMN_DEF"))
        .thenReturn(null)
        .thenReturn(null);
    
    when(columnsResultSet.getString("REMARKS"))
        .thenReturn("主键ID")
        .thenReturn("用户名");
    
    // 配置metadata.getColumns调用返回columnsResultSet
    when(mockDatabaseMetaData.getColumns(anyString(), eq("test_db"), eq("users"), anyString()))
        .thenReturn(columnsResultSet);
    
    return columnsResultSet;
  }
  
  private ResultSet mockResultSetForIndexes() throws SQLException {
    ResultSet mockIndexResultSet = mock(ResultSet.class);
    // 索引迭代模拟：返回一个索引，然后结束
    when(mockIndexResultSet.next()).thenReturn(true, false);
    
    // 索引信息模拟
    when(mockIndexResultSet.getString("INDEX_NAME")).thenReturn("PRIMARY");
    when(mockIndexResultSet.getBoolean("NON_UNIQUE")).thenReturn(false);
    when(mockIndexResultSet.getString("COLUMN_NAME")).thenReturn("id");
    when(mockIndexResultSet.getShort("TYPE")).thenReturn((short) 3); // 3表示PRIMARY KEY
    when(mockIndexResultSet.getInt("ORDINAL_POSITION")).thenReturn(1);
    
    return mockIndexResultSet;
  }

  private ResultSet mockResultSetForPrimaryKeys() throws SQLException {
    ResultSet mockPrimaryKeysResultSet = mock(ResultSet.class);
    // 主键迭代模拟：返回一个主键列，然后结束
    when(mockPrimaryKeysResultSet.next()).thenReturn(true, false);
    
    // 主键信息模拟
    when(mockPrimaryKeysResultSet.getString("COLUMN_NAME")).thenReturn("id");
    when(mockPrimaryKeysResultSet.getString("PK_NAME")).thenReturn("PRIMARY");
    when(mockPrimaryKeysResultSet.getInt("KEY_SEQ")).thenReturn(1);
    
    return mockPrimaryKeysResultSet;
  }

  @Test
  public void testSupports() {
    assertTrue(extractor.supports(mockDataSource));
    
    // 测试不支持的数据源类型
    DataSource postgresDS = mock(DataSource.class);
    when(postgresDS.getType()).thenReturn(DataSourceType.POSTGRESQL);
    assertFalse(extractor.supports(postgresDS));
  }

  @Test
  public void testGetSchemas() throws SQLException {
    // 重新设置mock，确保每次测试都有干净的mock
    mockResultSetForSchemas();
    
    List<String> schemas = extractor.getSchemas(mockConnection);
    
    // 验证结果：应只返回非系统schema (test_db)
    assertEquals(1, schemas.size());
    assertEquals("test_db", schemas.get(0));
  }

  @Test
  public void testGetTables() throws SQLException {
    // 重新设置mock，确保每次测试都有干净的mock
    mockResultSetForTables();
    
    List<String> tables = extractor.getTables(mockConnection, "test_db");
    
    // 验证结果：应返回两个表
    assertEquals(2, tables.size());
    assertEquals("users", tables.get(0));
    assertEquals("orders", tables.get(1));
  }

  @Test
  public void testExtractTable() throws SQLException {
    // 重新设置mock，确保每次测试都有干净的mock
    mockResultSetForColumns();
    mockResultSetForIndexes();
    
    TableMetadata metadata = extractor.extractTable(mockDataSource, mockConnection, "test_db", "users");
    
    // 验证表元数据
    assertNotNull(metadata);
    assertEquals("users", metadata.getName());
    assertEquals("test_db", metadata.getSchema());
    
    // 验证列
    assertEquals(2, metadata.getColumns().size());
    assertEquals("id", metadata.getColumns().get(0).getName());
    assertEquals("name", metadata.getColumns().get(1).getName());
    
    // 验证索引
    assertEquals(1, metadata.getIndices().size());
    assertEquals("PRIMARY", metadata.getIndices().get(0).getName());
  }

  @Test
  public void testExtractAll() throws SQLException {
    // 调用extractAll方法
    List<TableMetadata> tables = extractor.extractAll(mockDataSource, mockConnection);
    
    // 验证调用了getSchemas方法
    verify(mockConnection).createStatement();
    
    // 必须至少返回一个表的元数据
    assertFalse(tables.isEmpty());
    assertEquals(2, tables.size());
    
    // 验证第一个表是users
    TableMetadata firstTable = tables.get(0);
    assertEquals("users", firstTable.getName());
    assertEquals("test_db", firstTable.getSchema());
  }

  @Test
  public void testSystemTableFiltering() throws SQLException {
    // 设置一个专门用于系统表测试的ResultSet
    ResultSet systemTablesResultSet = mock(ResultSet.class);
    
    // 模拟迭代：返回三个表，其中两个是系统表，一个是普通表
    when(systemTablesResultSet.next())
        .thenReturn(true)  // sys_config (系统表) 
        .thenReturn(true)  // _temp (系统表)
        .thenReturn(true)  // users (普通表)
        .thenReturn(false);
    
    // 模拟获取表名
    when(systemTablesResultSet.getString("TABLE_NAME"))
        .thenReturn("sys_config")
        .thenReturn("_temp")
        .thenReturn("users");

    // 为系统表过滤测试配置专门的mock行为
    when(mockDatabaseMetaData.getTables(isNull(), eq("test_db"), isNull(), any(String[].class)))
        .thenReturn(systemTablesResultSet);
    
    List<String> tables = extractor.getTables(mockConnection, "test_db");
    
    // 验证结果：应只返回非系统表 (users)
    assertEquals(1, tables.size());
    assertEquals("users", tables.get(0));
  }
}
