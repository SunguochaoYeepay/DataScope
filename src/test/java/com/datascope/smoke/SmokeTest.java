package com.datascope.smoke;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("冒烟测试")
public class SmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("测试数据源管理 - 创建MySQL数据源")
    void testCreateMySQLDataSource() throws Exception {
        String requestBody = """
            {
                "name": "test-mysql",
                "type": "MYSQL",
                "host": "localhost",
                "port": 3306,
                "databaseName": "test_db",
                "username": "test_user",
                "password": "test_pass",
                "status": 1
            }
            """;

        mockMvc.perform(post("/api/datasources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("test-mysql"));
    }

    @Test
    @DisplayName("测试元数据管理 - 提取MySQL表信息")
    void testExtractMySQLMetadata() throws Exception {
        mockMvc.perform(get("/api/metadata/extract/{dataSourceId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("测试数据预览 - 表数据预览")
    void testPreviewTableData() throws Exception {
        mockMvc.perform(get("/api/preview/data")
                .param("dataSourceId", "1")
                .param("tableName", "test_table")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.totalElements").exists());
    }
}