package com.datascope.domain.service.sql;

import com.datascope.domain.model.sql.SQLStatement;

/**
 * SQL解析器接口
 */
public interface SQLParser {
    /**
     * 解析SQL语句
     *
     * @param sql SQL语句
     * @return 解析后的SQL结构
     */
    SQLStatement parse(String sql);
}