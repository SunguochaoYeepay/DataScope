-- 修改 data_source 表的 status 列类型
ALTER TABLE data_source MODIFY status VARCHAR(255) NOT NULL DEFAULT 'INACTIVE';

-- 更新现有数据（如果有的话）
UPDATE data_source SET status = 'INACTIVE' WHERE status = '1';
UPDATE data_source SET status = 'ACTIVE' WHERE status = '2';
UPDATE data_source SET status = 'ERROR' WHERE status = '3';
UPDATE data_source SET status = 'TESTING' WHERE status = '4';
UPDATE data_source SET status = 'MAINTAINING' WHERE status = '5';