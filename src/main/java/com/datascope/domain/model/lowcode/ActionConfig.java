package com.datascope.domain.model.lowcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 动作配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionConfig {

    /**
     * 动作类型
     */
    private String type;

    /**
     * 目标
     */
    private String target;

    /**
     * 参数
     */
    private Object params;
}