-- 创建Flyway历史表，确保在Flyway迁移执行前表结构已存在
-- 这可以解决循环依赖问题，因为Flyway会检查这个表是否存在

-- 如果表不存在则创建flyway_schema_history表
CREATE TABLE IF NOT EXISTS `flyway_schema_history` (
  `installed_rank` INT NOT NULL,
  `version` VARCHAR(50) NULL,
  `description` VARCHAR(200) NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `script` VARCHAR(1000) NOT NULL,
  `checksum` INT NULL,
  `installed_by` VARCHAR(100) NOT NULL,
  `installed_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` INT NOT NULL,
  `success` TINYINT(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  INDEX `flyway_schema_history_s_idx` (`success` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 创建系统必要的数据库表
-- 数据源配置表
CREATE TABLE IF NOT EXISTS `data_source_config` (
  `id` VARCHAR(36) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) NULL,
  `type` VARCHAR(50) NOT NULL,
  `driver_class` VARCHAR(255) NOT NULL,
  `url` VARCHAR(500) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(500) NOT NULL,
  `connection_properties` TEXT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `data_source_name_unique` (`name` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 查询历史表
CREATE TABLE IF NOT EXISTS `query_history` (
  `id` VARCHAR(36) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  `data_source_id` VARCHAR(36) NOT NULL,
  `query_text` TEXT NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `rows_affected` INT NULL,
  `execution_time` INT NULL,
  `error_message` TEXT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `query_history_user_idx` (`user_id` ASC),
  INDEX `query_history_ds_idx` (`data_source_id` ASC),
  CONSTRAINT `fk_query_history_data_source` FOREIGN KEY (`data_source_id`) 
    REFERENCES `data_source_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 导出历史表
CREATE TABLE IF NOT EXISTS `export_history` (
  `id` VARCHAR(36) NOT NULL,
  `user_id` VARCHAR(36) NOT NULL,
  `data_source_id` VARCHAR(36) NOT NULL,
  `query_id` VARCHAR(36) NULL,
  `file_name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(500) NOT NULL,
  `file_size` BIGINT NULL,
  `row_count` INT NULL,
  `format` VARCHAR(20) NOT NULL,
  `status` VARCHAR(20) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `completed_at` TIMESTAMP NULL,
  PRIMARY KEY (`id`),
  INDEX `export_history_user_idx` (`user_id` ASC),
  INDEX `export_history_ds_idx` (`data_source_id` ASC),
  INDEX `export_history_query_idx` (`query_id` ASC),
  CONSTRAINT `fk_export_history_data_source` FOREIGN KEY (`data_source_id`) 
    REFERENCES `data_source_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_export_history_query` FOREIGN KEY (`query_id`) 
    REFERENCES `query_history` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` VARCHAR(36) NOT NULL,
  `username` VARCHAR(50) NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `full_name` VARCHAR(100) NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` TIMESTAMP NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `user_username_unique` (`username` ASC),
  UNIQUE INDEX `user_email_unique` (`email` ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; 