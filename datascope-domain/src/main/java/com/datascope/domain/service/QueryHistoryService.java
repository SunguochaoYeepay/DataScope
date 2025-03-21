package com.datascope.domain.service;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.common.model.PageRequest;
import com.datascope.common.model.PageResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 查询历史服务接口
 */
public interface QueryHistoryService {
    
    /**
     * 记录查询历史
     *
     * @param history 查询历史记录
     */
    void recordHistory(QueryHistory history);

    /**
     * 批量记录查询历史
     *
     * @param histories 查询历史记录列表
     */
    void recordHistories(List<QueryHistory> histories);

    /**
     * 获取查询历史详情
     *
     * @param id 历史记录ID
     * @return 查询历史记录
     */
    QueryHistory getHistoryById(String id);

    /**
     * 获取查询相关的历史记录
     *
     * @param queryId 查询ID
     * @return 查询历史记录列表
     */
    List<QueryHistory> getHistoriesByQueryId(String queryId);

    /**
     * 分页查询用户的查询历史
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 状态
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResponse<QueryHistory> getUserHistoryByPage(
            String userId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String status,
            PageRequest pageRequest);

    /**
     * 获取用户最近的查询历史
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 查询历史记录列表
     */
    List<QueryHistory> getRecentHistories(String userId, int limit);

    /**
     * 删除历史记录
     *
     * @param id 历史记录ID
     */
    void deleteHistory(String id);

    /**
     * 删除查询相关的所有历史记录
     *
     * @param queryId 查询ID
     */
    void deleteHistoriesByQueryId(String queryId);

    /**
     * 清理指定天数前的历史记录
     *
     * @param days 天数
     * @return 清理的记录数
     */
    int cleanHistoryBefore(int days);

    /**
     * 获取执行统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param userId 用户ID（可选）
     * @param queryId 查询ID（可选）
     * @return 统计信息
     */
    Map<String, Object> getExecutionStats(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String userId,
            String queryId);

    /**
     * 获取最近失败的查询记录
     *
     * @param userId 用户ID（可选）
     * @param limit 限制数量
     * @return 失败的查询记录列表
     */
    List<QueryHistory> getRecentFailures(String userId, int limit);

    /**
     * 获取慢查询记录
     *
     * @param thresholdMillis 执行时间阈值（毫秒）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 慢查询记录列表
     */
    List<QueryHistory> getSlowQueries(
            long thresholdMillis,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int limit);
}