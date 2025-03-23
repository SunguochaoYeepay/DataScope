package com.datascope.domain.service.preview;

import com.datascope.domain.model.datasource.DataSource;
import com.datascope.domain.model.datasource.DataSourceType;
import com.datascope.domain.model.preview.DataPreviewRequest;
import com.datascope.domain.model.preview.DataPreviewResponse;
import com.datascope.domain.service.datasource.DataSourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DataPreviewServiceTest {

    @Autowired
    private DataPreviewService dataPreviewService;

    @Autowired
    private DataSourceService dataSourceService;

    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // 创建测试数据源
        dataSource = new DataSource();
        dataSource.setName("Test MySQL");
        dataSource.setType(DataSourceType.MYSQL);
        dataSource.setHost("localhost");
        dataSource.setPort(3306);
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDatabase("test");
        dataSourceService.save(dataSource);
    }

    @Test
    void testPreviewData() {
        DataPreviewRequest request = new DataPreviewRequest();
        request.setDataSourceId(dataSource.getId());
        request.setSchema("test");
        request.setTableName("users");
        request.setSampleSize(10);

        DataPreviewResponse response = dataPreviewService.previewData(request);

        assertNotNull(response);
        assertNotNull(response.getColumns());
        assertFalse(response.getColumns().isEmpty());
        assertNotNull(response.getRows());
        assertFalse(response.getRows().isEmpty());
        assertTrue(response.getRows().size() <= request.getSampleSize());
        assertNotNull(response.getTotalRows());
        assertEquals(request.getSampleSize(), response.getSampleSize());
        assertTrue(response.getExecutionTime() > 0);
    }

    @Test
    void testPreviewDataWithFilters() {
        DataPreviewRequest request = new DataPreviewRequest();
        request.setDataSourceId(dataSource.getId());
        request.setSchema("test");
        request.setTableName("users");
        request.setSampleSize(5);
        request.setOrderBy("id");
        request.setDesc(true);
        request.setWhereClause("age > 18");

        DataPreviewResponse response = dataPreviewService.previewData(request);

        assertNotNull(response);
        assertNotNull(response.getRows());
        assertTrue(response.getRows().size() <= request.getSampleSize());
    }

    @Test
    void testPreviewDataWithSystemColumns() {
        DataPreviewRequest request = new DataPreviewRequest();
        request.setDataSourceId(dataSource.getId());
        request.setSchema("test");
        request.setTableName("users");
        request.setSampleSize(5);
        request.setIncludeSystemColumns(true);

        DataPreviewResponse response = dataPreviewService.previewData(request);

        assertNotNull(response);
        assertTrue(response.getColumns().stream()
            .anyMatch(col -> col.getName().equals("created_at") || 
                           col.getName().equals("updated_at")));
    }

    @Test
    void testPreviewDataInvalidDataSource() {
        DataPreviewRequest request = new DataPreviewRequest();
        request.setDataSourceId(999L);
        request.setSchema("test");
        request.setTableName("users");

        assertThrows(IllegalArgumentException.class, () -> 
            dataPreviewService.previewData(request));
    }

    @Test
    void testPreviewDataInvalidTable() {
        DataPreviewRequest request = new DataPreviewRequest();
        request.setDataSourceId(dataSource.getId());
        request.setSchema("test");
        request.setTableName("non_existent_table");

        assertThrows(IllegalArgumentException.class, () -> 
            dataPreviewService.previewData(request));
    }
}