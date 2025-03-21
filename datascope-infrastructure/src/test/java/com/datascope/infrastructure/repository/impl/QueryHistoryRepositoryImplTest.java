package com.datascope.infrastructure.repository.impl;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.domain.repository.QueryHistoryRepository;
import com.datascope.infrastructure.mapper.QueryHistoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * 查询历史仓储实现类测试
 */
class QueryHistoryRepositoryImplTest {

    @Mock
    private QueryHistoryMapper queryHistoryMapper;

    private QueryHistoryRepositoryImpl queryHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        queryHistoryRepository = new QueryHistoryRepositoryImpl(queryHistoryMapper);
    }

    @Test
    void save_ShouldCallMapperInsert() {
        // Arrange
        QueryHistory history = createSampleHistory();
        when(queryHistoryMapper.insert(history)).thenReturn(1);

        // Act
        queryHistoryRepository.save(history);

        // Assert
        verify(queryHistoryMapper).insert(history);
    }

    @Test
    void batchSave_ShouldCallMapperBatchInsert() {
        // Arrange
        List<QueryHistory> histories = Arrays.asList(
            createSampleHistory(),
            createSampleHistory()
        );
        when(queryHistoryMapper.batchInsert(histories)).thenReturn(2);

        // Act
        queryHistoryRepository.batchSave(histories);

        // Assert
        verify(queryHistoryMapper).batchInsert(histories);
    }

    @Test
    void findById_ShouldReturnHistory() {
        // Arrange
        String id = "test-id";
        QueryHistory expected = createSampleHistory();
        when(queryHistoryMapper.findById(id)).thenReturn(expected);

        // Act
        QueryHistory actual = queryHistoryRepository.findById(id);

        // Assert
        assertEquals(expected, actual);
        verify(queryHistoryMapper).findById(id);
    }

    @Test
    void findUserHistoryByPage_ShouldCalculateOffsetCorrectly() {
        // Arrange
        String userId = "user-1";
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        String status = "SUCCESS";
        int pageNum = 2;
        int pageSize = 10;
        int expectedOffset = 10; // (pageNum - 1) * pageSize

        // Act
        queryHistoryRepository.findUserHistoryByPage(userId, startTime, endTime, 
                status, pageNum, pageSize);

        // Assert
        verify(queryHistoryMapper).findUserHistoryByPage(
            eq(userId),
            eq(startTime),
            eq(endTime),
            eq(status),
            eq(expectedOffset),
            eq(pageSize)
        );
    }

    @Test
    void getExecutionStats_ShouldReturnStats() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        String userId = "user-1";
        String queryId = "query-1";
        Map<String, Object> expected = new HashMap<>();
        expected.put("total_executions", 100L);
        expected.put("avg_execution_time", 150.5);
        when(queryHistoryMapper.getExecutionStats(startTime, endTime, userId, queryId))
            .thenReturn(expected);

        // Act
        Map<String, Object> actual = queryHistoryRepository.getExecutionStats(
            startTime, endTime, userId, queryId);

        // Assert
        assertEquals(expected, actual);
        verify(queryHistoryMapper).getExecutionStats(startTime, endTime, userId, queryId);
    }

    @Test
    void findSlowQueries_ShouldReturnSlowQueries() {
        // Arrange
        long threshold = 1000L;
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        int limit = 10;
        List<QueryHistory> expected = Arrays.asList(createSampleHistory());
        when(queryHistoryMapper.findSlowQueries(threshold, startTime, endTime, limit))
            .thenReturn(expected);

        // Act
        List<QueryHistory> actual = queryHistoryRepository.findSlowQueries(
            threshold, startTime, endTime, limit);

        // Assert
        assertEquals(expected, actual);
        verify(queryHistoryMapper).findSlowQueries(threshold, startTime, endTime, limit);
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