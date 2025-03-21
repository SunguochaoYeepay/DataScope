package com.datascope.domain.model.sql;

import lombok.Builder;
import lombok.Data;

/**
 * SQL中的字段信息
 */
@Data
@Builder
public class ColumnInfo {
    /**
     * 字段名
     */
    private String name;
    
    /**
     * 字段别名
     */
    private String alias;
    
    /**
     * 所属表
     */
    private String tableName;
    
    /**
     * 是否是聚合字段
     */
    private boolean isAggregated;
    
    /**
     * 聚合函数(如果是聚合字段)
     */
    private String aggregateFunction;
}