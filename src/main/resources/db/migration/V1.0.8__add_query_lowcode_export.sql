-- 创建查询配置表
CREATE TABLE IF NOT EXISTS `query_config` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '查询名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `data_source_id` VARCHAR(36) NOT NULL COMMENT '数据源ID',
    `sql_template` TEXT NOT NULL COMMENT 'SQL模板',
    `parameters` JSON DEFAULT NULL COMMENT '参数定义(JSON)',
    `timeout` INT NOT NULL DEFAULT 30 COMMENT '超时时间(秒)',
    `max_rows` INT NOT NULL DEFAULT 1000 COMMENT '最大返回行数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    KEY `idx_data_source_id` (`data_source_id`),
    CONSTRAINT `fk_query_config_data_source` FOREIGN KEY (`data_source_id`) REFERENCES `data_source` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='查询配置表';

-- 创建查询历史表
CREATE TABLE IF NOT EXISTS `query_history` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `query_config_id` VARCHAR(36) NOT NULL COMMENT '查询配置ID',
    `parameters` JSON DEFAULT NULL COMMENT '执行参数(JSON)',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `duration_ms` BIGINT DEFAULT NULL COMMENT '执行时长(毫秒)',
    `status` VARCHAR(20) NOT NULL COMMENT '状态(RUNNING/COMPLETED/FAILED/TIMEOUT)',
    `row_count` INT DEFAULT NULL COMMENT '返回行数',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `created_by` VARCHAR(100) NOT NULL COMMENT '执行人',
    PRIMARY KEY (`id`),
    KEY `idx_query_config_id` (`query_config_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_query_history_config` FOREIGN KEY (`query_config_id`) REFERENCES `query_config` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='查询历史表';

-- 创建低代码配置表
CREATE TABLE IF NOT EXISTS `lowcode_config` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `query_config_id` VARCHAR(36) NOT NULL COMMENT '查询配置ID',
    `display_type` VARCHAR(50) NOT NULL COMMENT '展示类型(TABLE/CHART/FORM)',
    `config` JSON NOT NULL COMMENT '配置详情(JSON)',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_query_config_id` (`query_config_id`),
    CONSTRAINT `fk_lowcode_query_config` FOREIGN KEY (`query_config_id`) REFERENCES `query_config` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='低代码配置表';

-- 创建导出配置表
CREATE TABLE IF NOT EXISTS `export_config` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '配置名称',
    `query_config_id` VARCHAR(36) DEFAULT NULL COMMENT '查询配置ID',
    `data_source_id` VARCHAR(36) DEFAULT NULL COMMENT '数据源ID',
    `table_name` VARCHAR(100) DEFAULT NULL COMMENT '表名',
    `export_type` VARCHAR(20) NOT NULL COMMENT '导出类型(CSV/EXCEL/JSON)',
    `column_config` JSON DEFAULT NULL COMMENT '列配置(JSON)',
    `filter_config` JSON DEFAULT NULL COMMENT '过滤配置(JSON)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    KEY `idx_query_config_id` (`query_config_id`),
    KEY `idx_data_source_id` (`data_source_id`),
    CONSTRAINT `fk_export_query_config` FOREIGN KEY (`query_config_id`) REFERENCES `query_config` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_export_data_source` FOREIGN KEY (`data_source_id`) REFERENCES `data_source` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导出配置表';

-- 创建导出历史表
CREATE TABLE IF NOT EXISTS `export_history` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `export_config_id` VARCHAR(36) NOT NULL COMMENT '导出配置ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_size` BIGINT DEFAULT NULL COMMENT '文件大小(字节)',
    `row_count` INT DEFAULT NULL COMMENT '导出行数',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
    `status` VARCHAR(20) NOT NULL COMMENT '状态(RUNNING/COMPLETED/FAILED)',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `created_by` VARCHAR(100) NOT NULL COMMENT '执行人',
    PRIMARY KEY (`id`),
    KEY `idx_export_config_id` (`export_config_id`),
    KEY `idx_start_time` (`start_time`),
    CONSTRAINT `fk_export_history_config` FOREIGN KEY (`export_config_id`) REFERENCES `export_config` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导出历史表';