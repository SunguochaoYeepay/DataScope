package com.datascope.domain.model.preview;

import lombok.Data;

@Data
public class DataPreviewRequest {
    /**
     * 数据源ID
     */
    private Long dataSourceId;

    /**
     * 模式名
     */
    private String schema;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 采样行数
     */
    private Integer sampleSize = 100;

    /**
     * 是否包含系统列
     */
    private boolean includeSystemColumns = false;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 是否降序
     */
    private boolean desc = false;

    /**
     * 过滤条件
     */
    private String whereClause;
}