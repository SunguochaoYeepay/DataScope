CREATE TABLE IF NOT EXISTS tbl_query_history (
    id VARCHAR(36) PRIMARY KEY,
    query_id VARCHAR(36) NOT NULL,
    parameters TEXT,
    execution_time BIGINT,
    affected_rows BIGINT,
    status VARCHAR(20),
    error_message TEXT,
    execution_ip VARCHAR(50),
    created_at DATETIME,
    created_by VARCHAR(36),
    INDEX idx_query_id (query_id),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;