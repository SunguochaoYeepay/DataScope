package com.datascope.domain.model.export;

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
import javax.persistence.Convert;
import java.time.LocalDateTime;
import java.util.List;

import com.datascope.infrastructure.util.JpaConverterJson;

/**
 * 导出配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "export_config")
public class ExportConfig {

    /**
     * 主键ID
     */
    @Id
    private String id;

    /**
     * 配置名称
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 查询配置ID
     */
    @Column(name = "query_config_id", length = 36)
    private String queryConfigId;

    /**
     * 数据源ID
     */
    @Column(name = "data_source_id", nullable = false, length = 36)
    private String dataSourceId;

    /**
     * 表名
     */
    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    /**
     * 导出类型
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ExportType exportType;

    /**
     * 列配置
     */
    @Column(name = "column_config", nullable = false, columnDefinition = "JSON")
    @Convert(converter = JpaConverterJson.class)
    private List<ColumnConfig> columnConfig;

    /**
     * 过滤条件配置
     */
    @Column(name = "filter_config", columnDefinition = "JSON")
    @Convert(converter = JpaConverterJson.class)
    private FilterConfig filterConfig;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
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