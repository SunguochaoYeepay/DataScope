package com.datascope.domain.service.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.datascope.domain.model.sql.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于Druid的SQL解析器实现
 */
@Service
public class DruidSQLParser implements SQLParser {
    
    @Override
    public com.datascope.domain.model.sql.SQLStatement parse(String sql) {
        // 解析SQL
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, "mysql");
        if (stmtList.isEmpty()) {
            throw new IllegalArgumentException("Invalid SQL statement");
        }
        
        // 获取第一个语句
        SQLStatement stmt = stmtList.get(0);
        
        // 使用访问者模式获取SQL信息
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);
        
        // 构建返回结果
        return com.datascope.domain.model.sql.SQLStatement.builder()
                .type(determineSQLType(stmt))
                .tables(extractTables(visitor))
                .columns(extractColumns(visitor))
                .conditions(extractConditions(visitor))
                .originalSql(sql)
                .formattedSql(SQLUtils.formatMySql(sql))
                .build();
    }
    
    /**
     * 确定SQL类型
     */
    private SQLType determineSQLType(SQLStatement stmt) {
        if (stmt instanceof SQLSelectStatement) {
            return SQLType.SELECT;
        } else if (stmt instanceof SQLInsertStatement) {
            return SQLType.INSERT;
        } else if (stmt instanceof SQLUpdateStatement) {
            return SQLType.UPDATE;
        } else if (stmt instanceof SQLDeleteStatement) {
            return SQLType.DELETE;
        }
        return SQLType.OTHER;
    }
    
    /**
     * 提取表信息
     */
    private List<TableInfo> extractTables(MySqlSchemaStatVisitor visitor) {
        return visitor.getTables().keySet().stream()
                .map(tableName -> TableInfo.builder()
                        .name(tableName.getName())
                        .schema(tableName.toString().contains(".") ? 
                               tableName.toString().split("\\.")[0] : null)
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 提取字段信息
     */
    private List<ColumnInfo> extractColumns(MySqlSchemaStatVisitor visitor) {
        return visitor.getColumns().stream()
                .map(column -> ColumnInfo.builder()
                        .name(column.getName())
                        .tableName(column.getTable())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 提取条件信息
     */
    private List<Condition> extractConditions(MySqlSchemaStatVisitor visitor) {
        return visitor.getColumns().stream()
                .filter(TableStat.Column::isWhere)
                .map(column -> Condition.builder()
                        .tableName(column.getTable())
                        .columnName(column.getName())
                        .build())
                .collect(Collectors.toList());
    }
}