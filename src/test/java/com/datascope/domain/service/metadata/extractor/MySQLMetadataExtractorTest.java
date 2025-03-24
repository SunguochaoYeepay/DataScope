package com.datascope.domain.service.metadata.extractor;

import static org.junit.jupiter.api.Assertions.*;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.metadata.ColumnMetadata;
import com.datascope.domain.model.metadata.IndexMetadata;
import com.datascope.domain.model.metadata.TableMetadata;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MySQLMetadataExtractorTest {

  @Autowired private MySQLMetadataExtractor extractor;

  private DataSource dataSource;
  private Connection connection;

  @BeforeEach
  void setUp() throws Exception {
    dataSource = new DataSource();
    dataSource.setId("1");
    dataSource.setType(DataSourceType.MYSQL);
    dataSource.setHost("localhost");
    dataSource.setPort(3306);
    dataSource.setUsername("root");
    dataSource.setPassword("root");
    dataSource.setDatabaseName("test");

    String url =
        String.format(
            "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
            dataSource.getHost(), dataSource.getPort(), dataSource.getDatabaseName());
    connection =
        DriverManager.getConnection(url, dataSource.getUsername(), dataSource.getPassword());
  }

  @Test
  void testSupports() {
    assertTrue(extractor.supports(dataSource));

    DataSource postgresDataSource = new DataSource();
    postgresDataSource.setType(DataSourceType.POSTGRESQL);
    assertFalse(extractor.supports(postgresDataSource));
  }

  @Test
  void testGetSchemas() {
    List<String> schemas = extractor.getSchemas(connection);
    assertNotNull(schemas);
    assertFalse(schemas.isEmpty());
    assertTrue(schemas.contains("test"));
  }

  @Test
  void testGetTables() {
    List<String> tables = extractor.getTables(connection, "test");
    assertNotNull(tables);
    assertFalse(tables.isEmpty());
  }

  @Test
  void testExtractTable() {
    // 假设test数据库中有一个users表
    TableMetadata table = extractor.extractTable(dataSource, connection, "test", "users");
    assertNotNull(table);
    assertEquals("users", table.getName());
    assertEquals("test", table.getSchema());
    assertEquals(dataSource.getId(), table.getDataSourceId());

    // 验证列信息
    List<ColumnMetadata> columns = table.getColumns();
    assertFalse(columns.isEmpty());

    // 验证主键
    boolean hasPrimaryKey = columns.stream().anyMatch(ColumnMetadata::isPrimaryKey);
    assertTrue(hasPrimaryKey);

    // 验证索引
    List<IndexMetadata> indices = table.getIndices();
    assertFalse(indices.isEmpty());
  }

  @Test
  void testExtractAll() {
    List<TableMetadata> tables = extractor.extractAll(dataSource, connection);
    assertNotNull(tables);
    assertFalse(tables.isEmpty());

    // 验证每个表都有基本信息
    for (TableMetadata table : tables) {
      assertNotNull(table.getName());
      assertNotNull(table.getSchema());
      assertEquals(dataSource.getId(), table.getDataSourceId());
      assertFalse(table.getColumns().isEmpty());
    }
  }
}
