-- 导出配置表
CREATE TABLE IF NOT EXISTS export_config (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '配置名称',
    query_config_id VARCHAR(36) COMMENT '查询配置ID',
    data_source_id VARCHAR(36) NOT NULL COMMENT '数据源ID',
    table_name VARCHAR(100) NOT NULL COMMENT '表名',
    export_type VARCHAR(20) NOT NULL COMMENT '导出类型',
    column_config JSON NOT NULL COMMENT '列配置',
    filter_config JSON COMMENT '过滤条件配置',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by VARCHAR(100) NOT NULL COMMENT '创建人',
    updated_by VARCHAR(100) NOT NULL COMMENT '更新人',
    INDEX idx_export_config_query_config_id (query_config_id),
    INDEX idx_export_config_data_source_id (data_source_id)
) COMMENT='导出配置表';

-- 导出历史表
CREATE TABLE IF NOT EXISTS export_history (
    id VARCHAR(36) PRIMARY KEY,
    export_config_id VARCHAR(36) NOT NULL COMMENT '导出配置ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    row_count INT COMMENT '导出行数',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status VARCHAR(20) NOT NULL COMMENT '状态',
    error_message TEXT COMMENT '错误信息',
    created_by VARCHAR(100) NOT NULL COMMENT '执行人',
    INDEX idx_export_history_config_id (export_config_id),
    INDEX idx_export_history_created_by (created_by),
    INDEX idx_export_history_status (status)
) COMMENT='导出历史表';