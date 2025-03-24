-- 初始化数据库结构
-- 创建数据源表
CREATE TABLE IF NOT EXISTS `data_source` (
    `id` VARCHAR(36) NOT NULL COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '数据源名称',
    `type` VARCHAR(50) NOT NULL COMMENT '数据源类型(MYSQL/POSTGRESQL)',
    `host` VARCHAR(255) NOT NULL COMMENT '主机地址',
    `port` INT NOT NULL COMMENT '端口号',
    `database_name` VARCHAR(100) NOT NULL COMMENT '数据库名称',
    `username` VARCHAR(100) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用 1:启用)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(100) NOT NULL COMMENT '创建人',
    `updated_by` VARCHAR(100) NOT NULL COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源配置表';

-- 创建元数据表
CREATE TABLE IF NOT EXISTS `metadata` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `data_source_id` VARCHAR(36) NOT NULL COMMENT '数据源ID',
    `schema_name` VARCHAR(100) NOT NULL COMMENT '模式名',
    `table_name` VARCHAR(100) NOT NULL COMMENT '表名',
    `table_type` VARCHAR(50) NOT NULL COMMENT '表类型(TABLE/VIEW)',
    `column_name` VARCHAR(100) NOT NULL COMMENT '列名',
    `column_type` VARCHAR(50) NOT NULL COMMENT '列类型',
    `column_length` INT DEFAULT NULL COMMENT '列长度',
    `nullable` TINYINT NOT NULL COMMENT '是否可为空(0:否 1:是)',
    `primary_key` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主键(0:否 1:是)',
    `default_value` VARCHAR(255) DEFAULT NULL COMMENT '默认值',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_data_source_id` (`data_source_id`),
    KEY `idx_table_name` (`table_name`),
    CONSTRAINT `fk_metadata_data_source` FOREIGN KEY (`data_source_id`) REFERENCES `data_source` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='元数据表';

-- 创建数据预览配置表
CREATE TABLE IF NOT EXISTS `preview_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `data_source_id` VARCHAR(36) NOT NULL COMMENT '数据源ID',
    `table_name` VARCHAR(100) NOT NULL COMMENT '表名',
    `sample_size` INT NOT NULL DEFAULT 1000 COMMENT '采样大小',
    `order_by` VARCHAR(100) DEFAULT NULL COMMENT '排序字段',
    `desc` TINYINT NOT NULL DEFAULT 0 COMMENT '是否降序(0:否 1:是)',
    `where_clause` VARCHAR(500) DEFAULT NULL COMMENT '过滤条件',
    `include_system_columns` TINYINT NOT NULL DEFAULT 0 COMMENT '是否包含系统字段(0:否 1:是)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_data_source_table` (`data_source_id`, `table_name`),
    CONSTRAINT `fk_preview_config_data_source` FOREIGN KEY (`data_source_id`) REFERENCES `data_source` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据预览配置表';

-- 创建数据质量规则表
CREATE TABLE IF NOT EXISTS `quality_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `data_source_id` VARCHAR(36) NOT NULL COMMENT '数据源ID',
    `table_name` VARCHAR(100) NOT NULL COMMENT '表名',
    `rule_type` VARCHAR(50) NOT NULL COMMENT '规则类型',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `rule_config` TEXT NOT NULL COMMENT '规则配置(JSON)',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用(0:否 1:是)',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
    `last_check_time` DATETIME DEFAULT NULL COMMENT '最后检查时间',
    `last_check_result` TINYINT DEFAULT NULL COMMENT '最后检查结果(0:失败 1:成功)',
    `last_check_message` VARCHAR(500) DEFAULT NULL COMMENT '最后检查消息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_data_source_table` (`data_source_id`, `table_name`),
    CONSTRAINT `fk_quality_rule_data_source` FOREIGN KEY (`data_source_id`) REFERENCES `data_source` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据质量规则表';

-- 创建数据质量检查结果表
CREATE TABLE IF NOT EXISTS `data_quality_check_result` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `rule_id` BIGINT NOT NULL COMMENT '规则ID',
    `check_time` DATETIME NOT NULL COMMENT '检查时间',
    `total_count` BIGINT NOT NULL COMMENT '总记录数',
    `violation_count` BIGINT NOT NULL COMMENT '违规记录数',
    `sample_violations` JSON DEFAULT NULL COMMENT '违规样本',
    `status` VARCHAR(50) NOT NULL COMMENT '状态(SUCCESS/FAILED/ERROR)',
    `error_message` TEXT DEFAULT NULL COMMENT '错误信息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_rule_id` (`rule_id`),
    KEY `idx_check_time` (`check_time`),
    CONSTRAINT `fk_check_result_rule` FOREIGN KEY (`rule_id`) REFERENCES `quality_rule` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据质量检查结果表';