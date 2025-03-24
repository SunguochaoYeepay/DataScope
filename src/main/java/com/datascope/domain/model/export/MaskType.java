package com.datascope.domain.model.export;

/**
 * 掩码类型枚举
 */
public enum MaskType {
    /**
     * 不掩码
     */
    NONE,
    
    /**
     * 固定位置掩码
     */
    FIXED,
    
    /**
     * 正则表达式掩码
     */
    REGEX,
    
    /**
     * 邮箱掩码
     */
    EMAIL,
    
    /**
     * 电话号码掩码
     */
    PHONE,
    
    /**
     * 身份证号掩码
     */
    ID_CARD,
    
    /**
     * 银行卡号掩码
     */
    BANK_CARD,
    
    /**
     * 地址掩码
     */
    ADDRESS,
    
    /**
     * 姓名掩码
     */
    NAME
}