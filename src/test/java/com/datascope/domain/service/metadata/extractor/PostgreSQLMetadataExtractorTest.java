package com.datascope.domain.service.metadata.extractor;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.metadata.TableMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PostgreSQLMetadataExtractorTest {

    @Autowired
    private PostgreSQLMetadataExtractor extractor;

    private DataSource dataSource;
    private Connection connection;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = new DataSource();
        dataSource.setId(1L);
        dataSource.setType(DataSourceType.POSTGRESQL);
        dataSource.setHost("localhost");
        dataSource.setPort(5432);
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
        dataSource.setDatabase("test");

        String url = String.format("jdbc:postgresql://%s:%d/%s",
            dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
        connection = DriverManager.getConnection(url, dataSource.getUsername(), dataSource.getPassword());
    }

    @Test
    void testSupports() {
        assertTrue(extractor.supports(dataSource));

        DataSource mysqlDataSource = new DataSource();
        mysqlDataSource.setType(DataSourceType.MYSQL);
        assertFalse(extractor.supports(mysqlDataSource));
    }

    @Test
    void testGetSchemas() {
        List<String> schemas = extractor.getSchemas(connection);
        assertNotNull(schemas);
        assertFalse(schemas.isEmpty());
        assertTrue(schemas.contains("public"));
    }

    @Test
    void testGetTables() {
        List<String> tables = extractor.getTables(connection, "public");
        assertNotNull(tables);
        assertFalse(tables.isEmpty());
    }

    @Test
    void testExtractTable() {
        TableMetadata table = extractor.extractTable(dataSource, connection, "public", "users");
        assertNotNull(table);
        assertEquals("users", table.getName());
        assertEquals("public", table.getSchema());
        assertEquals(dataSource.getId(), table.getDataSourceId());
        assertFalse(table.getColumns().isEmpty());
        assertFalse(table.getIndices().isEmpty());
    }

    @Test
    void testExtractAll() {
        List<TableMetadata> tables = extractor.extractAll(dataSource, connection);
        assertNotNull(tables);
        assertFalse(tables.isEmpty());

        for (TableMetadata table : tables) {
            assertNotNull(table.getName());
            assertNotNull(table.getSchema());
            assertEquals(dataSource.getId(), table.getDataSourceId());
            assertFalse(table.getColumns().isEmpty());
        }
    }
}