package com.datascope.domain.repository;

import com.datascope.domain.model.query.QueryHistory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 查询历史仓储接口
 */
public interface QueryHistoryRepository {
    
    /**
     * 保存查询历史
     */
    void save(QueryHistory history);

    /**
     * 批量保存查询历史
     */
    void batchSave(List<QueryHistory> histories);

    /**
     * 根据ID查询历史
     */
    QueryHistory findById(String id);

    /**
     * 根据查询ID查询历史列表
     */
    List<QueryHistory> findByQueryId(String queryId);

    /**
     * 根据用户ID查询历史列表
     */
    List<QueryHistory> findByUserId(String userId, int limit);

    /**
     * 删除查询历史
     */
    void deleteById(String id);

    /**
     * 删除查询相关的所有历史
     */
    void deleteByQueryId(String queryId);

    /**
     * 清理指定天数前的历史记录
     *
     * @return 清理的记录数
     */
    int cleanHistoryBefore(int days);

    /**
     * 分页查询用户的查询历史
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 状态
     * @param pageNum 页码(从1开始)
     * @param pageSize 每页大小
     * @return 查询历史列表
     */
    List<QueryHistory> findUserHistoryByPage(String userId, LocalDateTime startTime, 
            LocalDateTime endTime, String status, int pageNum, int pageSize);

    /**
     * 统计用户的查询历史总数
     */
    Long countUserHistory(String userId, LocalDateTime startTime, LocalDateTime endTime, String status);

    /**
     * 查询指定时间范围内的执行统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID（可选）
     * @param queryId 查询ID（可选）
     * @return 统计结果
     */
    Map<String, Object> getExecutionStats(LocalDateTime startTime, LocalDateTime endTime, 
            String userId, String queryId);

    /**
     * 查询最近失败的查询历史
     *
     * @param userId 用户ID（可选）
     * @param limit 限制数量
     * @return 失败的查询历史列表
     */
    List<QueryHistory> findRecentFailures(String userId, int limit);

    /**
     * 查询执行时间超过阈值的查询历史
     *
     * @param thresholdMillis 执行时间阈值（毫秒）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 慢查询历史列表
     */
    List<QueryHistory> findSlowQueries(long thresholdMillis, LocalDateTime startTime, 
            LocalDateTime endTime, int limit);
}