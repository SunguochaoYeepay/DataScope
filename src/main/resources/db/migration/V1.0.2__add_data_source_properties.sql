-- 创建数据源属性表
CREATE TABLE IF NOT EXISTS `data_source_properties` (
    `data_source_id` VARCHAR(36) NOT NULL COMMENT '数据源ID',
    `property_key` VARCHAR(100) NOT NULL COMMENT '属性键',
    `property_value` VARCHAR(500) DEFAULT NULL COMMENT '属性值',
    PRIMARY KEY (`data_source_id`, `property_key`),
    CONSTRAINT `fk_data_source_properties` FOREIGN KEY (`data_source_id`) REFERENCES `data_source` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源属性表';