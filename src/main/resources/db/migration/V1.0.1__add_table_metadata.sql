-- 创建表元数据表
CREATE TABLE IF NOT EXISTS `table_metadata` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `data_source_id` VARCHAR(36) NOT NULL COMMENT '数据源ID',
    `schema_name` VARCHAR(100) NOT NULL COMMENT '模式名',
    `table_name` VARCHAR(100) NOT NULL COMMENT '表名',
    `table_comment` VARCHAR(500) DEFAULT NULL COMMENT '表注释',
    `row_count` BIGINT DEFAULT NULL COMMENT '行数',
    `data_size` BIGINT DEFAULT NULL COMMENT '数据大小(字节)',
    `index_size` BIGINT DEFAULT NULL COMMENT '索引大小(字节)',
    `last_analyzed` DATETIME NOT NULL COMMENT '最后分析时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_data_source_id` (`data_source_id`),
    KEY `idx_table_name` (`table_name`),
    CONSTRAINT `fk_table_metadata_data_source` FOREIGN KEY (`data_source_id`) REFERENCES `data_source` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表元数据表';