-- 创建Flyway历史表
CREATE TABLE IF NOT EXISTS `flyway_schema_history` (
  `installed_rank` INT NOT NULL,
  `version` VARCHAR(50),
  `description` VARCHAR(200) NOT NULL,
  `type` VARCHAR(20) NOT NULL,
  `script` VARCHAR(1000) NOT NULL,
  `checksum` INT,
  `installed_by` VARCHAR(100) NOT NULL,
  `installed_on` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` INT NOT NULL,
  `success` TINYINT(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  INDEX `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB;

-- 创建一个跟踪迁移记录的元数据表
CREATE TABLE IF NOT EXISTS `migration_metadata` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `schema_version` VARCHAR(50) NOT NULL,
  `applied_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` VARCHAR(200) NOT NULL
) ENGINE=InnoDB;

-- 记录初始化记录
INSERT INTO `migration_metadata` (`schema_version`, `description`) 
VALUES ('0.9.0', 'Flyway初始化脚本成功执行')
ON DUPLICATE KEY UPDATE `applied_at` = CURRENT_TIMESTAMP; 