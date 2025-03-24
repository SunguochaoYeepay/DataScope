package com.datascope.domain.repository.query;

import com.datascope.domain.model.query.QueryHistory;
import com.datascope.domain.model.query.QueryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询历史仓库接口
 */
@Repository
public interface QueryHistoryRepository extends JpaRepository<QueryHistory, String> {

    /**
     * 根据查询配置ID查询历史记录
     */
    List<QueryHistory> findByQueryConfigId(String queryConfigId);
    
    /**
     * 根据状态查询历史记录
     */
    List<QueryHistory> findByStatus(QueryStatus status);
    
    /**
     * 根据创建者查询历史记录
     */
    List<QueryHistory> findByCreatedBy(String createdBy);
    
    /**
     * 分页查询历史记录
     */
    Page<QueryHistory> findByQueryConfigId(String queryConfigId, Pageable pageable);
    
    /**
     * 根据条件分页查询历史记录
     */
    @Query("SELECT qh FROM QueryHistory qh WHERE " +
            "(:queryConfigId IS NULL OR qh.queryConfigId = :queryConfigId) AND " +
            "(:status IS NULL OR qh.status = :status) AND " +
            "(:startTimeFrom IS NULL OR qh.startTime >= :startTimeFrom) AND " +
            "(:startTimeTo IS NULL OR qh.startTime <= :startTimeTo) AND " +
            "(:createdBy IS NULL OR qh.createdBy = :createdBy)")
    Page<QueryHistory> findByConditions(
            @Param("queryConfigId") String queryConfigId,
            @Param("status") QueryStatus status,
            @Param("startTimeFrom") LocalDateTime startTimeFrom,
            @Param("startTimeTo") LocalDateTime startTimeTo,
            @Param("createdBy") String createdBy,
            Pageable pageable);
    
    /**
     * 查询最近的查询历史记录
     */
    List<QueryHistory> findTop10ByCreatedByOrderByStartTimeDesc(String createdBy);
} 