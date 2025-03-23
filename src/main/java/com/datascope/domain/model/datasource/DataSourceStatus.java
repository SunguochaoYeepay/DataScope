package com.datascope.domain.model.datasource;

/**
 * 数据源状态枚举
 */
public enum DataSourceStatus {
    ACTIVE,      // 活跃状态
    INACTIVE,    // 非活跃状态
    ERROR,       // 错误状态
    TESTING,     // 测试中
    MAINTAINING  // 维护中
}