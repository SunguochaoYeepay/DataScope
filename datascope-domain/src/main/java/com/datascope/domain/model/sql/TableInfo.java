package com.datascope.domain.model.sql;

import lombok.Builder;
import lombok.Data;

/**
 * SQL中的表信息
 */
@Data
@Builder
public class TableInfo {
    /**
     * 表名
     */
    private String name;
    
    /**
     * 表别名
     */
    private String alias;
    
    /**
     * 数据库名
     */
    private String schema;
}