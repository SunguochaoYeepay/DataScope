package com.datascope.domain.model.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 导出过滤条件配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterConfig {
    
    /**
     * 过滤条件
     */
    private List<FilterCondition> conditions;
    
    /**
     * 条件组合方式: AND/OR
     */
    private String operator;
}