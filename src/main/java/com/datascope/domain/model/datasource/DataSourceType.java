package com.datascope.domain.model.datasource;

/**
 * 数据源类型枚举
 */
public enum DataSourceType {
    MYSQL,
    POSTGRESQL,
    ORACLE,
    SQLSERVER,
    DB2,
    HIVE,
    CLICKHOUSE,
    DORIS;
    
    public boolean isRelational() {
        return this == MYSQL || this == POSTGRESQL || this == ORACLE || 
               this == SQLSERVER || this == DB2;
    }
    
    public boolean isBigData() {
        return this == HIVE;
    }
    
    public boolean isAnalytical() {
        return this == CLICKHOUSE || this == DORIS;
    }
}