package com.datascope.domain.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * 查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {

    /**
     * 查询配置ID
     */
    private String queryConfigId;

    /**
     * 查询参数
     */
    private Map<String, Object> parameters;

    /**
     * 分页信息
     */
    private QueryPagination pagination;
}