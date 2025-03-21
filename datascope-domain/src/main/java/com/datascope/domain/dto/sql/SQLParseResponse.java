package com.datascope.domain.dto.sql;

import com.datascope.domain.model.sql.*;
import lombok.Data;

import java.util.List;

/**
 * SQL解析响应
 */
@Data
public class SQLParseResponse {
    /**
     * SQL类型
     */
    private SQLType type;
    
    /**
     * 涉及的表
     */
    private List<TableInfo> tables;
    
    /**
     * 涉及的字段
     */
    private List<ColumnInfo> columns;
    
    /**
     * 条件信息
     */
    private List<Condition> conditions;
    
    /**
     * 格式化后的SQL
     */
    private String formattedSql;
    
    /**
     * 从SQLStatement转换为响应对象
     */
    public static SQLParseResponse fromStatement(SQLStatement statement) {
        SQLParseResponse response = new SQLParseResponse();
        response.setType(statement.getType());
        response.setTables(statement.getTables());
        response.setColumns(statement.getColumns());
        response.setConditions(statement.getConditions());
        response.setFormattedSql(statement.getFormattedSql());
        return response;
    }
}