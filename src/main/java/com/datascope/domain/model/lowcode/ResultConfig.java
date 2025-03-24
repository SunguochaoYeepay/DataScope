package com.datascope.domain.model.lowcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结果字段配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultConfig {

    /**
     * 字段名
     */
    private String field;

    /**
     * 标签
     */
    private String label;

    /**
     * 宽度
     */
    private Integer width;

    /**
     * 是否可排序
     */
    private Boolean sortable;

    /**
     * 掩码规则
     */
    private MaskRule maskRule;

    /**
     * 格式化规则
     */
    private String format;

    /**
     * 对齐方式
     */
    private String align;
}