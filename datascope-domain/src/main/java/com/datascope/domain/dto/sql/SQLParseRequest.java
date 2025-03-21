package com.datascope.domain.dto.sql;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * SQL解析请求
 */
@Data
public class SQLParseRequest {
    /**
     * SQL语句
     */
    @NotBlank(message = "SQL语句不能为空")
    private String sql;
    
    /**
     * 数据库类型，默认为MySQL
     */
    private String dbType = "mysql";
}