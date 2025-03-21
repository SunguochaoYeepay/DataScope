package com.datascope.domain.service.impl;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.common.model.PageRequest;
import com.datascope.common.model.PageResponse;
import com.datascope.domain.repository.QueryHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 查询历史服务实现类测试
 */
class QueryHistoryServiceImplTest {

    @Mock
    private QueryHistoryRepository queryHistoryRepository;

    private QueryHistoryServiceImpl queryHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        queryHistoryService = new QueryHistoryServiceImpl(queryHistoryRepository);
    }

    @Test
    void recordHistory_WithValidHistory_ShouldSave() {
        // Arrange
        QueryHistory history = createSampleHistory();
        history.setId(null); // 模拟新记录

        // Act
        queryHistoryService.recordHistory(history);

        // Assert
        assertNotNull(history.getId());
        assertNotNull(history.getCreatedAt());
        verify(queryHistoryRepository).save(history);
    }

    @Test
    void recordHistories_WithValidHistories_ShouldBatchSave() {
        // Arrange
        List<QueryHistory> histories = Arrays.asList(
            createSampleHistory(),
            createSampleHistory()
        );
        histories.forEach(h -> h.setId(null)); // 模拟新记录

        // Act
        queryHistoryService.recordHistories(histories);

        // Assert
        histories.forEach(h -> {
            assertNotNull(h.getId());
            assertNotNull(h.getCreatedAt());
        });
        verify(queryHistoryRepository).batchSave(histories);
    }

    @Test
    void getUserHistoryByPage_WithValidRequest_ShouldReturnPageResponse() {
        // Arrange
        String userId = "user-1";
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        String status = "SUCCESS";
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);
        
        List<QueryHistory> mockRecords = Arrays.asList(createSampleHistory());
        when(queryHistoryRepository.countUserHistory(userId, startTime, endTime, status))
            .thenReturn(1L);
        when(queryHistoryRepository.findUserHistoryByPage(
                eq(userId), eq(startTime), eq(endTime), eq(status), eq(1), eq(10)))
            .thenReturn(mockRecords);

        // Act
        PageResponse<QueryHistory> response = queryHistoryService.getUserHistoryByPage(
            userId, startTime, endTime, status, pageRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getTotal());
        assertEquals(mockRecords, response.getList());
        assertEquals(1, response.getPageNum());
        assertEquals(10, response.getPageSize());
    }

    @Test
    void getExecutionStats_WithValidRequest_ShouldReturnStats() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        String userId = "user-1";
        String queryId = "query-1";
        
        Map<String, Object> expectedStats = new HashMap<>();
        expectedStats.put("total_executions", 100L);
        expectedStats.put("avg_execution_time", 150.5);
        
        when(queryHistoryRepository.getExecutionStats(startTime, endTime, userId, queryId))
            .thenReturn(expectedStats);

        // Act
        Map<String, Object> actualStats = queryHistoryService.getExecutionStats(
            startTime, endTime, userId, queryId);

        // Assert
        assertEquals(expectedStats, actualStats);
        verify(queryHistoryRepository).getExecutionStats(startTime, endTime, userId, queryId);
    }

    @Test
    void getSlowQueries_WithValidRequest_ShouldReturnQueries() {
        // Arrange
        long threshold = 1000L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        int limit = 10;
        
        List<QueryHistory> expectedQueries = Arrays.asList(createSampleHistory());
        when(queryHistoryRepository.findSlowQueries(threshold, startTime, endTime, limit))
            .thenReturn(expectedQueries);

        // Act
        List<QueryHistory> actualQueries = queryHistoryService.getSlowQueries(
            threshold, startTime, endTime, limit);

        // Assert
        assertEquals(expectedQueries, actualQueries);
        verify(queryHistoryRepository).findSlowQueries(threshold, startTime, endTime, limit);
    }

    @Test
    void cleanHistoryBefore_WithValidDays_ShouldClean() {
        // Arrange
        int days = 30;
        when(queryHistoryRepository.cleanHistoryBefore(days)).thenReturn(100);

        // Act
        int cleanedCount = queryHistoryService.cleanHistoryBefore(days);

        // Assert
        assertEquals(100, cleanedCount);
        verify(queryHistoryRepository).cleanHistoryBefore(days);
    }

    // 异常测试用例
    @Test
    void recordHistory_WithNullHistory_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> 
            queryHistoryService.recordHistory(null));
    }

    @Test
    void recordHistory_WithNullQueryId_ShouldThrowException() {
        QueryHistory history = createSampleHistory();
        history.setQueryId(null);
        assertThrows(IllegalArgumentException.class, () -> 
            queryHistoryService.recordHistory(history));
    }

    @Test
    void getExecutionStats_WithInvalidTimeRange_ShouldThrowException() {
        LocalDateTime laterDate = LocalDateTime.now();
        LocalDateTime earlierDate = laterDate.minusSeconds(1);
        
        assertThrows(IllegalArgumentException.class, () ->
            queryHistoryService.getExecutionStats(laterDate, earlierDate, "user-1", "query-1"));
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