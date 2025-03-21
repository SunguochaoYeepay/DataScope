package com.datascope.api.controller;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.common.model.PageRequest;
import com.datascope.common.model.PageResponse;
import com.datascope.domain.service.QueryHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 查询历史控制器测试
 */
@WebMvcTest(QueryHistoryController.class)
class QueryHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueryHistoryService queryHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private QueryHistory sampleHistory;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        sampleHistory = createSampleHistory();
        startTime = LocalDateTime.now();
        endTime = startTime.plusDays(1); // 一天后
    }

    @Test
    void getHistoryById_ShouldReturnHistory() throws Exception {
        when(queryHistoryService.getHistoryById("test-id"))
            .thenReturn(sampleHistory);

        mockMvc.perform(get("/api/v1/query-histories/test-id"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(sampleHistory.getId()))
            .andExpect(jsonPath("$.data.queryId").value(sampleHistory.getQueryId()));
    }

    @Test
    void getHistoriesByQueryId_ShouldReturnHistories() throws Exception {
        List<QueryHistory> histories = Arrays.asList(sampleHistory);
        when(queryHistoryService.getHistoriesByQueryId("query-1"))
            .thenReturn(histories);

        mockMvc.perform(get("/api/v1/query-histories/by-query/query-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(sampleHistory.getId()));
    }

    @Test
    void getUserHistoryByPage_ShouldReturnPageResponse() throws Exception {
        List<QueryHistory> records = Arrays.asList(sampleHistory);
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);
        PageResponse<QueryHistory> pageResponse = PageResponse.of(records, 1L, pageRequest);
        
        when(queryHistoryService.getUserHistoryByPage(
                eq("user-1"), any(), any(), eq("SUCCESS"), any(PageRequest.class)))
            .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/query-histories/user/user-1")
                .param("status", "SUCCESS")
                .param("pageNum", "1")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.list[0].id").value(sampleHistory.getId()))
            .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void getExecutionStats_ShouldReturnStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_executions", 100L);
        stats.put("avg_execution_time", 150.5);
        
        when(queryHistoryService.getExecutionStats(any(), any(), eq("user-1"), eq("query-1")))
            .thenReturn(stats);

        mockMvc.perform(get("/api/v1/query-histories/stats")
                .param("startTime", "2024-01-01 00:00:00")
                .param("endTime", "2024-01-02 00:00:00")
                .param("userId", "user-1")
                .param("queryId", "query-1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.total_executions").value(100))
            .andExpect(jsonPath("$.data.avg_execution_time").value(150.5));
    }

    @Test
    void getSlowQueries_ShouldReturnQueries() throws Exception {
        List<QueryHistory> slowQueries = Arrays.asList(sampleHistory);
        when(queryHistoryService.getSlowQueries(eq(1000L), any(), any(), eq(10)))
            .thenReturn(slowQueries);

        mockMvc.perform(get("/api/v1/query-histories/slow-queries")
                .param("thresholdMillis", "1000")
                .param("startTime", "2024-01-01 00:00:00")
                .param("endTime", "2024-01-02 00:00:00")
                .param("limit", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value(sampleHistory.getId()));
    }

    @Test
    void deleteHistory_ShouldDeleteSuccessfully() throws Exception {
        doNothing().when(queryHistoryService).deleteHistory("test-id");

        mockMvc.perform(delete("/api/v1/query-histories/test-id"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        verify(queryHistoryService).deleteHistory("test-id");
    }

    @Test
    void cleanHistoryBefore_ShouldReturnCleanedCount() throws Exception {
        when(queryHistoryService.cleanHistoryBefore(30)).thenReturn(100);

        mockMvc.perform(delete("/api/v1/query-histories/clean")
                .param("days", "30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(100));
    }

    private QueryHistory createSampleHistory() {
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
}