package com.datascope.domain.model.lowcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询条件配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionConfig {

    /**
     * 字段名
     */
    private String field;

    /**
     * 标签
     */
    private String label;

    /**
     * 组件类型
     */
    private String component;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 默认值
     */
    private Object defaultValue;

    /**
     * 顺序
     */
    private Integer order;

    /**
     * 是否可见
     */
    private Boolean visible;

    /**
     * 提示信息
     */
    private String placeholder;
}