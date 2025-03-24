-- V1.0.0 基础表结构迁移脚本
-- 注意：基础表已在schema.sql中创建，这里只做更新和扩展

-- 创建元数据表
CREATE TABLE IF NOT EXISTS `table_metadata` (
  `id` VARCHAR(36) NOT NULL,
  `data_source_id` VARCHAR(36) NOT NULL,
  `schema_name` VARCHAR(100) NOT NULL,
  `table_name` VARCHAR(100) NOT NULL,
  `remarks` VARCHAR(500) NULL,
  `table_type` VARCHAR(50) NULL,
  `row_count` BIGINT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `table_metadata_unique` (`data_source_id`, `schema_name`, `table_name`),
  CONSTRAINT `fk_table_metadata_data_source` FOREIGN KEY (`data_source_id`) REFERENCES `data_source_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `column_metadata` (
  `id` VARCHAR(36) NOT NULL,
  `table_metadata_id` VARCHAR(36) NOT NULL,
  `column_name` VARCHAR(100) NOT NULL,
  `column_type` VARCHAR(50) NOT NULL,
  `data_type` INT NOT NULL,
  `column_size` INT NULL,
  `decimal_digits` INT NULL,
  `nullable` BOOLEAN NOT NULL DEFAULT FALSE,
  `remarks` VARCHAR(500) NULL,
  `column_def` TEXT NULL,
  `ordinal_position` INT NOT NULL,
  `is_primary_key` BOOLEAN NOT NULL DEFAULT FALSE,
  `is_foreign_key` BOOLEAN NOT NULL DEFAULT FALSE,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `column_metadata_unique` (`table_metadata_id`, `column_name`),
  CONSTRAINT `fk_column_metadata_table` FOREIGN KEY (`table_metadata_id`) 
    REFERENCES `table_metadata` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `index_metadata` (
  `id` VARCHAR(36) NOT NULL,
  `table_metadata_id` VARCHAR(36) NOT NULL,
  `index_name` VARCHAR(100) NOT NULL,
  `column_name` VARCHAR(100) NOT NULL,
  `non_unique` BOOLEAN NOT NULL DEFAULT TRUE,
  `index_type` VARCHAR(50) NULL,
  `ordinal_position` INT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `index_metadata_unique` (`table_metadata_id`, `index_name`, `column_name`),
  CONSTRAINT `fk_index_metadata_table` FOREIGN KEY (`table_metadata_id`) 
    REFERENCES `table_metadata` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 创建导出配置表
CREATE TABLE IF NOT EXISTS `export_config` (
  `id` VARCHAR(36) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `data_source_id` VARCHAR(36) NOT NULL,
  `query_text` TEXT NULL,
  `format` VARCHAR(20) NOT NULL DEFAULT 'CSV',
  `include_headers` BOOLEAN NOT NULL DEFAULT TRUE,
  `delimiter` VARCHAR(5) NULL DEFAULT ',',
  `quote_char` VARCHAR(5) NULL DEFAULT '"',
  `file_encoding` VARCHAR(20) NULL DEFAULT 'UTF-8',
  `schedule_expression` VARCHAR(100) NULL,
  `output_path` VARCHAR(500) NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `export_config_unique` (`user_id`, `name`),
  CONSTRAINT `fk_export_config_user` FOREIGN KEY (`user_id`) 
    REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_export_config_data_source` FOREIGN KEY (`data_source_id`) 
    REFERENCES `data_source_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; 