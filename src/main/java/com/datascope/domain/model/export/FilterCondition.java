package com.datascope.domain.model.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导出过滤条件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterCondition {
    
    /**
     * 字段名
     */
    private String field;
    
    /**
     * 操作符
     */
    private String operator;
    
    /**
     * 值
     */
    private Object value;
    
    /**
     * 值类型
     */
    private String valueType;
}