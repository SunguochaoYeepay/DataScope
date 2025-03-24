package com.datascope.domain.model.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询参数定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryParameter {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数标签
     */
    private String label;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 参数描述
     */
    private String description;

    /**
     * 参数顺序
     */
    private Integer order;
}