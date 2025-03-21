package com.datascope.domain.model.lowcode;

import com.datascope.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 低代码配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LowCodeConfig extends BaseEntity {
    /**
     * 查询ID
     */
    private String queryId;
    
    /**
     * 配置类型（FORM, LIST, DETAIL）
     */
    private String configType;
    
    /**
     * 配置JSON
     */
    private String configJson;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 状态（DRAFT, PUBLISHED）
     */
    private String status;
    
    /**
     * 是否默认配置
     */
    private Boolean isDefault;
    
    /**
     * 描述
     */
    private String description;
}