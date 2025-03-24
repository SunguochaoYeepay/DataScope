-- 创建索引元数据表
CREATE TABLE IF NOT EXISTS `index_metadata` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `table_metadata_id` BIGINT NOT NULL COMMENT '表元数据ID',
    `index_name` VARCHAR(100) NOT NULL COMMENT '索引名',
    `is_unique` BIT NOT NULL DEFAULT 0 COMMENT '是否唯一索引',
    `cardinality` BIGINT DEFAULT NULL COMMENT '基数',
    `index_size` BIGINT DEFAULT NULL COMMENT '索引大小(字节)',
    `column_names` VARCHAR(500) NOT NULL COMMENT '索引列名(逗号分隔)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_table_metadata_id` (`table_metadata_id`),
    CONSTRAINT `fk_index_metadata_table` FOREIGN KEY (`table_metadata_id`) REFERENCES `table_metadata` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='索引元数据表'; 