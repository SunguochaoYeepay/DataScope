-- 创建列元数据表
CREATE TABLE IF NOT EXISTS `column_metadata` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `table_metadata_id` BIGINT NOT NULL COMMENT '表元数据ID',
    `column_name` VARCHAR(100) NOT NULL COMMENT '列名',
    `column_type` VARCHAR(50) NOT NULL COMMENT '列类型',
    `column_length` INT DEFAULT NULL COMMENT '列长度',
    `column_precision` INT DEFAULT NULL COMMENT '列精度',
    `is_nullable` BIT NOT NULL DEFAULT 0 COMMENT '是否可为空(0:否 1:是)',
    `is_primary_key` BIT NOT NULL DEFAULT 0 COMMENT '是否主键(0:否 1:是)',
    `ordinal_position` INT NOT NULL COMMENT '列位置',
    `default_value` VARCHAR(255) DEFAULT NULL COMMENT '默认值',
    `column_comment` VARCHAR(500) DEFAULT NULL COMMENT '列注释',
    `is_auto_increment` BIT NOT NULL DEFAULT 0 COMMENT '是否自增(0:否 1:是)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_table_metadata_id` (`table_metadata_id`),
    CONSTRAINT `fk_column_metadata_table` FOREIGN KEY (`table_metadata_id`) REFERENCES `table_metadata` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='列元数据表'; 