package com.datascope.domain.model.lowcode;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 显示配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DisplayConfig extends BaseEntity {
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 列ID
     */
    private String columnId;
    
    /**
     * 显示类型
     */
    private String displayType;
    
    /**
     * 格式化规则（JSON格式）
     */
    private String formatRule;
    
    /**
     * 掩码规则（JSON格式）
     */
    private String maskRule;
    
    /**
     * 是否显示
     */
    private Boolean visible;
    
    /**
     * 显示顺序
     */
    private Integer displayOrder;
    
    /**
     * 是否必填
     */
    private Boolean required;
    
    /**
     * 验证规则（JSON格式）
     */
    private String validationRule;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 提示信息
     */
    private String placeholder;
    
    /**
     * 帮助文本
     */
    private String helpText;
}