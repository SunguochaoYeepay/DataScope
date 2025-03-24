-- 初始化基础数据

-- 添加默认管理员用户（密码：admin123）
INSERT INTO `users` (`id`, `username`, `password`, `email`, `full_name`, `role`, `status`) 
VALUES 
('1', 'admin', '$2a$10$3.apmmFD0/JYT0CdoUv7yO1S/KJ5bOIjx/x8yPwPQiYvWmypPvLxS', 'admin@datascope.com', 'System Administrator', 'ADMIN', 'ACTIVE')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP;

-- 添加默认测试数据源（可选，方便测试）
INSERT INTO `data_source_config` (`id`, `name`, `description`, `type`, `driver_class`, `url`, `username`, `password`, `status`) 
VALUES 
('1', 'Local MySQL', 'DataScope本地MySQL数据库', 'MYSQL', 'com.mysql.cj.jdbc.Driver', 'jdbc:mysql://localhost:3306/datascope?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai', 'root', 'DataScope@123', 'ACTIVE')
ON DUPLICATE KEY UPDATE `updated_at` = CURRENT_TIMESTAMP; 