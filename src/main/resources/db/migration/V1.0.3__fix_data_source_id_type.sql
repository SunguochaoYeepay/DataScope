-- 删除外键约束
ALTER TABLE preview_config DROP FOREIGN KEY fk_preview_config_data_source;
ALTER TABLE quality_rule DROP FOREIGN KEY fk_quality_rule_data_source;
ALTER TABLE data_source_properties DROP FOREIGN KEY fk_data_source_properties;
ALTER TABLE metadata DROP FOREIGN KEY fk_metadata_data_source;

-- 修改引用表中的列类型
ALTER TABLE preview_config MODIFY data_source_id VARCHAR(36);
ALTER TABLE quality_rule MODIFY data_source_id VARCHAR(36);
ALTER TABLE data_source_properties MODIFY data_source_id VARCHAR(36);
ALTER TABLE metadata MODIFY data_source_id VARCHAR(36);

-- 修改主表的列类型
ALTER TABLE data_source MODIFY id VARCHAR(36);
ALTER TABLE data_source MODIFY status VARCHAR(255);

-- 重新添加外键约束
ALTER TABLE preview_config ADD CONSTRAINT fk_preview_config_data_source 
    FOREIGN KEY (data_source_id) REFERENCES data_source(id) ON DELETE CASCADE;
ALTER TABLE quality_rule ADD CONSTRAINT fk_quality_rule_data_source 
    FOREIGN KEY (data_source_id) REFERENCES data_source(id) ON DELETE CASCADE;
ALTER TABLE data_source_properties ADD CONSTRAINT fk_data_source_properties 
    FOREIGN KEY (data_source_id) REFERENCES data_source(id) ON DELETE CASCADE;
ALTER TABLE metadata ADD CONSTRAINT fk_metadata_data_source 
    FOREIGN KEY (data_source_id) REFERENCES data_source(id) ON DELETE CASCADE;