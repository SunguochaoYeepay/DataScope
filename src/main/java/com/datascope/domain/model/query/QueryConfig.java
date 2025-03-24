package com.datascope.domain.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import java.time.LocalDateTime;

/**
 * 查询配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "query_config")
public class QueryConfig {

    /**
     * 主键ID
     */
    @Id
    private String id;

    /**
     * 查询名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 数据源ID
     */
    @Column(name = "data_source_id", nullable = false, length = 36)
    private String dataSourceId;

    /**
     * SQL模板
     */
    @Column(name = "sql_template", nullable = false, columnDefinition = "TEXT")
    private String sqlTemplate;

    /**
     * 参数定义(JSON)
     */
    @Column(columnDefinition = "JSON")
    private String parameters;

    /**
     * 超时时间(秒)
     */
    @Column(nullable = false)
    private Integer timeout;

    /**
     * 最大返回行数
     */
    @Column(name = "max_rows", nullable = false)
    private Integer maxRows;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    /**
     * 更新人
     */
    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;
}