package com.datascope.domain.model.lowcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作按钮配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationConfig {

    /**
     * 操作类型
     */
    private String type;

    /**
     * 标签
     */
    private String label;

    /**
     * 动作配置
     */
    private ActionConfig action;

    /**
     * 图标
     */
    private String icon;

    /**
     * 样式
     */
    private String style;

    /**
     * 条件表达式(满足条件才显示)
     */
    private String condition;
}