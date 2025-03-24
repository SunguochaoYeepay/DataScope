package com.datascope.infrastructure.util;

import com.datascope.domain.model.query.QueryPagination;
import com.datascope.infrastructure.common.exception.DataScopeException;
import com.datascope.infrastructure.common.exception.ErrorCode;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL工具类
 */
public class SqlUtils {

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    /**
     * 渲染SQL模板，替换其中的参数
     *
     * @param sqlTemplate SQL模板
     * @param parameters 参数Map
     * @return 渲染后的SQL
     */
    public static String renderSqlTemplate(String sqlTemplate, Map<String, Object> parameters) {
        if (sqlTemplate == null || sqlTemplate.isEmpty()) {
            return "";
        }

        if (parameters == null || parameters.isEmpty()) {
            return sqlTemplate;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = PARAM_PATTERN.matcher(sqlTemplate);

        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object paramValue = parameters.get(paramName);
            
            if (paramValue == null) {
                matcher.appendReplacement(result, "NULL");
            } else if (paramValue instanceof Number) {
                matcher.appendReplacement(result, paramValue.toString());
            } else {
                // 处理字符串，转义单引号，防止SQL注入
                String escapedValue = paramValue.toString().replace("'", "''");
                matcher.appendReplacement(result, "'" + escapedValue + "'");
            }
        }
        
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 应用分页参数到SQL语句
     *
     * @param sql 原始SQL
     * @param pagination 分页参数
     * @return 添加了分页的SQL
     */
    public static String applyPagination(String sql, QueryPagination pagination) {
        if (pagination == null) {
            return sql;
        }

        Integer page = pagination.getPage();
        Integer size = pagination.getSize();
        
        if (page == null || size == null || page < 1 || size < 1) {
            return sql;
        }

        // 计算偏移量
        int offset = (page - 1) * size;
        
        // 默认使用MySQL的分页语法
        return sql + " LIMIT " + offset + ", " + size;
    }
} 