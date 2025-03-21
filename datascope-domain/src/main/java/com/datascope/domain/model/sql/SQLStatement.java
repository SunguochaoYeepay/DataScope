package com.datascope.domain.model.sql;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * SQL语句解析后的结构化表示
 */
@Data
@Builder
public class SQLStatement {
    /**
     * SQL类型(SELECT/INSERT/UPDATE/DELETE)
     */
    private SQLType type;
    
    /**
     * 涉及的表信息
     */
    private List<TableInfo> tables;
    
    /**
     * 涉及的字段信息
     */
    private List<ColumnInfo> columns;
    
    /**
     * WHERE条件
     */
    private List<Condition> conditions;
    
    /**
     * 原始SQL
     */
    private String originalSql;
    
    /**
     * 格式化后的SQL
     */
    private String formattedSql;
}