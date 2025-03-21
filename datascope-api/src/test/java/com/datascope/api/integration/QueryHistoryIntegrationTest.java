package com.datascope.api.integration;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.domain.repository.QueryHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 查询历史集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QueryHistoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QueryHistoryRepository queryHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private QueryHistory testHistory;

    @BeforeEach
    void setUp() {
        testHistory = createAndSaveTestHistory();
    }

    @Test
    @Sql("/sql/clean_query_history.sql")
    void getHistoryById_ShouldReturnHistory() throws Exception {
        mockMvc.perform(get("/api/v1/query-histories/{id}", testHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(testHistory.getId()))
            .andExpect(jsonPath("$.data.queryId").value(testHistory.getQueryId()))
            .andExpect(jsonPath("$.data.status").value("SUCCESS"));
    }

    @Test
    @Sql("/sql/clean_query_history.sql")
    void getUserHistoryByPage_ShouldReturnPagedResults() throws Exception {
        // 创建多条测试数据
        for (int i = 0; i < 15; i++) {
            createAndSaveTestHistory();
        }

        mockMvc.perform(get("/api/v1/query-histories/user/{userId}", testHistory.getCreatedBy())
                .param("pageNum", "1")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.total").value(16)) // 15 + 1(testHistory)
            .andExpect(jsonPath("$.data.records", hasSize(10)))
            .andExpect(jsonPath("$.data.pageNum").value(1))
            .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    @Test
    @Sql("/sql/clean_query_history.sql")
    void getExecutionStats_ShouldReturnCorrectStats() throws Exception {
        // 创建一些测试数据，包括成功和失败的记录
        createAndSaveTestHistory("SUCCESS", 100L);
        createAndSaveTestHistory("SUCCESS", 200L);
        createAndSaveTestHistory("FAILED", 150L);

        LocalDateTime startTime = LocalDateTime.now().minusHours(1); // 1小时前
        LocalDateTime endTime = LocalDateTime.now();

        mockMvc.perform(get("/api/v1/query-histories/stats")
                .param("startTime", formatDateTime(startTime))
                .param("endTime", formatDateTime(endTime)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.total_executions").value(4L)) // 3 + 1(testHistory)
            .andExpect(jsonPath("$.data.success_count").value(3L))
            .andExpect(jsonPath("$.data.failed_count").value(1L));
    }

    @Test
    @Sql("/sql/clean_query_history.sql")
    void getSlowQueries_ShouldReturnSlowQueries() throws Exception {
        // 创建一些测试数据，包括慢查询和正常查询
        createAndSaveTestHistory("SUCCESS", 2000L);
        createAndSaveTestHistory("SUCCESS", 1500L);
        createAndSaveTestHistory("SUCCESS", 500L);

        LocalDateTime startTime = LocalDateTime.now().minusHours(1); // 1小时前
        LocalDateTime endTime = LocalDateTime.now();

        mockMvc.perform(get("/api/v1/query-histories/slow-queries")
                .param("thresholdMillis", "1000")
                .param("startTime", formatDateTime(startTime))
                .param("endTime", formatDateTime(endTime))
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].executionTime", greaterThan(1000L)));
    }

    @Test
    @Sql("/sql/clean_query_history.sql")
    void getRecentFailures_ShouldReturnFailedQueries() throws Exception {
        // 创建一些测试数据，包括失败的查询
        createAndSaveTestHistory("FAILED", 100L);
        createAndSaveTestHistory("FAILED", 200L);
        createAndSaveTestHistory("SUCCESS", 150L);

        mockMvc.perform(get("/api/v1/query-histories/failures")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].status").value("FAILED"));
    }

    @Test
    @Sql("/sql/clean_query_history.sql")
    void cleanHistoryBefore_ShouldCleanOldRecords() throws Exception {
        // 创建一些测试数据，包括较旧的记录
        QueryHistory oldHistory = createTestHistory();
        oldHistory.setCreatedAt(LocalDateTime.now().minusDays(40)); // 40天前
        queryHistoryRepository.save(oldHistory);

        mockMvc.perform(delete("/api/v1/query-histories/clean")
                .param("days", "30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(1L)); // 应该清理1条记录
    }

    private QueryHistory createAndSaveTestHistory() {
        QueryHistory history = createTestHistory();
        queryHistoryRepository.save(history);
        return history;
    }

    private QueryHistory createAndSaveTestHistory(String status, Long executionTime) {
        QueryHistory history = createTestHistory();
        history.setStatus(status);
        history.setExecutionTime(executionTime);
        queryHistoryRepository.save(history);
        return history;
    }

    private QueryHistory createTestHistory() {
        QueryHistory history = new QueryHistory();
        history.setId(UUID.randomUUID().toString());
        history.setQueryId("query-" + UUID.randomUUID().toString());
        history.setParameters("{}");
        history.setExecutionTime(100L);
        history.setAffectedRows(10L);
        history.setStatus("SUCCESS");
        history.setExecutionIp("127.0.0.1");
        history.setCreatedAt(LocalDateTime.now());
        history.setCreatedBy("test-user");
        return history;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}