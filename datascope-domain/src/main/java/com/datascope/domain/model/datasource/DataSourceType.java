package com.datascope.domain.model.datasource;

/**
 * 数据源类型枚举
 */
public enum DataSourceType {
    MYSQL("MySQL"),
    DB2("DB2");

    private final String displayName;

    DataSourceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}