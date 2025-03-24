package com.datascope.domain.model.lowcode;

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
 * 低代码配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lowcode_config")
public class LowcodeConfig {

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
     * 展示类型
     */
    @Column(name = "display_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DisplayType displayType;

    /**
     * 配置详情(JSON)
     */
    @Column(nullable = false, columnDefinition = "JSON")
    private String config;

    /**
     * 版本号
     */
    @Column(nullable = false)
    private Integer version;

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