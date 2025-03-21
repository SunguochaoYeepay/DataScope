package com.datascope.domain.service.impl;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.common.model.PageRequest;
import com.datascope.common.model.PageResponse;
import com.datascope.domain.repository.QueryHistoryRepository;
import com.datascope.domain.service.QueryHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 查询历史服务实现类
 */
@Service
public class QueryHistoryServiceImpl implements QueryHistoryService {

    private final QueryHistoryRepository queryHistoryRepository;

    public QueryHistoryServiceImpl(QueryHistoryRepository queryHistoryRepository) {
        this.queryHistoryRepository = queryHistoryRepository;
    }

    @Override
    @Transactional
    public void recordHistory(QueryHistory history) {
        Assert.notNull(history, "查询历史记录不能为空");
        Assert.hasText(history.getQueryId(), "查询ID不能为空");
        
        // 设置ID和创建时间
        if (history.getId() == null) {
            history.setId(UUID.randomUUID().toString());
        }
        if (history.getCreatedAt() == null) {
            history.setCreatedAt(LocalDateTime.now());
        }
        
        queryHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void recordHistories(List<QueryHistory> histories) {
        Assert.notEmpty(histories, "查询历史记录列表不能为空");
        
        // 设置ID和创建时间
        LocalDateTime now = LocalDateTime.now();
        histories.forEach(history -> {
            if (history.getId() == null) {
                history.setId(UUID.randomUUID().toString());
            }
            if (history.getCreatedAt() == null) {
                history.setCreatedAt(now);
            }
        });
        
        queryHistoryRepository.batchSave(histories);
    }

    @Override
    public QueryHistory getHistoryById(String id) {
        Assert.hasText(id, "历史记录ID不能为空");
        return queryHistoryRepository.findById(id);
    }

    @Override
    public List<QueryHistory> getHistoriesByQueryId(String queryId) {
        Assert.hasText(queryId, "查询ID不能为空");
        return queryHistoryRepository.findByQueryId(queryId);
    }

    @Override
    public PageResponse<QueryHistory> getUserHistoryByPage(
            String userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String status,
            PageRequest pageRequest) {
        Assert.hasText(userId, "用户ID不能为空");
        Assert.notNull(pageRequest, "分页参数不能为空");
        
        // 查询总数
        Long total = queryHistoryRepository.countUserHistory(userId, startTime, endTime, status);
        
        // 如果总数为0，直接返回空结果
        if (total == 0) {
            return PageResponse.of(Collections.emptyList(), 0L, pageRequest);
        }
        
        // 查询数据
        List<QueryHistory> records = queryHistoryRepository.findUserHistoryByPage(
            userId,
            startTime,
            endTime,
            status,
            pageRequest.getPageNum(),
            pageRequest.getPageSize()
        );
        
        return PageResponse.of(records, total, pageRequest);
    }

    @Override
    public List<QueryHistory> getRecentHistories(String userId, int limit) {
        Assert.hasText(userId, "用户ID不能为空");
        Assert.isTrue(limit > 0, "限制数量必须大于0");
        return queryHistoryRepository.findByUserId(userId, limit);
    }

    @Override
    @Transactional
    public void deleteHistory(String id) {
        Assert.hasText(id, "历史记录ID不能为空");
        queryHistoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteHistoriesByQueryId(String queryId) {
        Assert.hasText(queryId, "查询ID不能为空");
        queryHistoryRepository.deleteByQueryId(queryId);
    }

    @Override
    @Transactional
    public int cleanHistoryBefore(int days) {
        Assert.isTrue(days > 0, "天数必须大于0");
        return queryHistoryRepository.cleanHistoryBefore(days);
    }

    @Override
    public Map<String, Object> getExecutionStats(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String userId,
            String queryId) {
        Assert.notNull(startTime, "开始时间不能为空");
        Assert.notNull(endTime, "结束时间不能为空");
        Assert.isTrue(startTime.isBefore(endTime), "开始时间必须早于结束时间");
        
        return queryHistoryRepository.getExecutionStats(startTime, endTime, userId, queryId);
    }

    @Override
    public List<QueryHistory> getRecentFailures(String userId, int limit) {
        Assert.isTrue(limit > 0, "限制数量必须大于0");
        return queryHistoryRepository.findRecentFailures(userId, limit);
    }

    @Override
    public List<QueryHistory> getSlowQueries(
            long thresholdMillis,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int limit) {
        Assert.isTrue(thresholdMillis > 0, "执行时间阈值必须大于0");
        Assert.notNull(startTime, "开始时间不能为空");
        Assert.notNull(endTime, "结束时间不能为空");
        Assert.isTrue(startTime.isBefore(endTime), "开始时间必须早于结束时间");
        Assert.isTrue(limit > 0, "限制数量必须大于0");
        
        return queryHistoryRepository.findSlowQueries(thresholdMillis, startTime, endTime, limit);
    }
}