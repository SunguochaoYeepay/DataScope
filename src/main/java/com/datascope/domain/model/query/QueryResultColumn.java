package com.datascope.domain.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询结果列定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResultColumn {

    /**
     * 列名
     */
    private String name;

    /**
     * 列标签
     */
    private String label;

    /**
     * 列类型
     */
    private String type;

    /**
     * 是否可排序
     */
    private Boolean sortable;
}