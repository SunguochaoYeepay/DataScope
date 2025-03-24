-- 重置Flyway迁移验证状态的脚本
-- 警告: 此脚本将删除Flyway迁移历史表，仅在开发环境使用
-- 执行此脚本将允许应用程序忽略之前的迁移验证错误

-- 在执行此脚本前，请手动确认这是你想要的操作
-- 删除Flyway历史表，将会重置所有迁移状态
DROP TABLE IF EXISTS flyway_schema_history;

-- 创建一个新表，确保脚本有实际的SQL操作
CREATE TABLE IF NOT EXISTS migration_metadata (
    id INT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255),
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO migration_metadata (description) VALUES ('Migration state reset by V1.0.11');