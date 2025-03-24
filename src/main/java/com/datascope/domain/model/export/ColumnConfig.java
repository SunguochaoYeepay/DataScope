package com.datascope.domain.model.export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导出列配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnConfig {
    
    /**
     * 数据库字段名
     */
    private String field;
    
    /**
     * 列标题
     */
    private String label;
    
    /**
     * 列宽度
     */
    private Integer width;
    
    /**
     * 日期格式化
     */
    private String format;
    
    /**
     * 数据掩码规则
     */
    private MaskConfig maskConfig;
    
    /**
     * 是否导出
     */
    private Boolean export;
    
    /**
     * 列排序
     */
    private Integer order;
}