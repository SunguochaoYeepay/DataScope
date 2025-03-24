package com.datascope.domain.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {

    /**
     * 列定义
     */
    private List<QueryResultColumn> columns;

    /**
     * 数据行
     */
    private List<Map<String, Object>> rows;

    /**
     * 总行数
     */
    private Long total;

    /**
     * 执行时间(毫秒)
     */
    private Long executionTime;

    /**
     * 是否截断(达到最大行数限制)
     */
    private Boolean truncated;

    /**
     * 查询状态
     */
    private QueryStatus status;

    /**
     * 错误信息
     */
    private String errorMessage;
}