package com.datascope.domain.model.lowcode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 掩码规则
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaskRule {

    /**
     * 掩码类型
     */
    private String type;

    /**
     * 起始位置
     */
    private Integer start;

    /**
     * 结束位置
     */
    private Integer end;

    /**
     * 掩码字符
     */
    private String maskChar;
}