package com.datascope.infrastructure.repository.impl;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.domain.repository.QueryHistoryRepository;
import com.datascope.infrastructure.repository.mapper.QueryHistoryMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 查询历史仓储实现类
 */
@Repository
public class QueryHistoryRepositoryImpl implements QueryHistoryRepository {

    private final QueryHistoryMapper queryHistoryMapper;

    public QueryHistoryRepositoryImpl(QueryHistoryMapper queryHistoryMapper) {
        this.queryHistoryMapper = queryHistoryMapper;
    }

    @Override
    @Transactional
    public void save(QueryHistory history) {
        queryHistoryMapper.insert(history);
    }

    @Override
    @Transactional
    public void batchSave(List<QueryHistory> histories) {
        queryHistoryMapper.batchInsert(histories);
    }

    @Override
    public QueryHistory findById(String id) {
        return queryHistoryMapper.findById(id);
    }

    @Override
    public List<QueryHistory> findByQueryId(String queryId) {
        return queryHistoryMapper.findByQueryId(queryId);
    }

    @Override
    public List<QueryHistory> findByUserId(String userId, int limit) {
        return queryHistoryMapper.findByUserId(userId, limit);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        queryHistoryMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByQueryId(String queryId) {
        queryHistoryMapper.deleteByQueryId(queryId);
    }

    @Override
    @Transactional
    public int cleanHistoryBefore(int days) {
        return queryHistoryMapper.cleanHistoryBefore(days);
    }

    @Override
    public List<QueryHistory> findUserHistoryByPage(String userId, LocalDateTime startTime, 
            LocalDateTime endTime, String status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return queryHistoryMapper.findUserHistoryByPage(userId, startTime, endTime, 
                status, offset, pageSize);
    }

    @Override
    public Long countUserHistory(String userId, LocalDateTime startTime, LocalDateTime endTime, String status) {
        return queryHistoryMapper.countUserHistory(userId, startTime, endTime, status);
    }

    @Override
    public Map<String, Object> getExecutionStats(LocalDateTime startTime, LocalDateTime endTime, 
            String userId, String queryId) {
        return queryHistoryMapper.getExecutionStats(startTime, endTime, userId, queryId);
    }

    @Override
    public List<QueryHistory> findRecentFailures(String userId, int limit) {
        return queryHistoryMapper.findRecentFailures(userId, limit);
    }

    @Override
    public List<QueryHistory> findSlowQueries(long thresholdMillis, LocalDateTime startTime, 
            LocalDateTime endTime, int limit) {
        return queryHistoryMapper.findSlowQueries(thresholdMillis, startTime, endTime, limit);
    }
}