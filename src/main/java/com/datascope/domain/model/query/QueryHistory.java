package com.datascope.domain.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.time.LocalDateTime;

/**
 * 查询历史实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "query_history")
public class QueryHistory {

    /**
     * 主键ID
     */
    @Id
    private String id;

    /**
     * 查询配置ID
     */
    @Column(name = "query_config_id", nullable = false, length = 36)
    private String queryConfigId;

    /**
     * 执行参数(JSON)
     */
    @Column(columnDefinition = "JSON")
    private String parameters;

    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 执行时长(毫秒)
     */
    @Column(name = "duration_ms")
    private Long durationMs;

    /**
     * 状态
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private QueryStatus status;

    /**
     * 返回行数
     */
    @Column(name = "row_count")
    private Integer rowCount;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 执行人
     */
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
}