package com.datascope.domain.model.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据掩码配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaskConfig {

    /**
     * 掩码类型
     */
    private MaskType type;
    
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