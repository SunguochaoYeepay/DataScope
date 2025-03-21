package com.datascope.domain.model.sql;

import lombok.Builder;
import lombok.Data;

/**
 * SQL中的条件信息
 */
@Data
@Builder
public class Condition {
    /**
     * 字段名
     */
    private String columnName;
    
    /**
     * 操作符
     */
    private String operator;
    
    /**
     * 条件值
     */
    private String value;
    
    /**
     * 所属表
     */
    private String tableName;
}