# 数据库设计

## 1. 表结构设计

### 1.1 数据源配置表 (tbl_datasource)

```sql
CREATE TABLE tbl_datasource (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '数据源名称',
    type VARCHAR(20) NOT NULL COMMENT '数据源类型(MYSQL/DB2)',
    host VARCHAR(255) NOT NULL COMMENT '主机地址',
    port INT NOT NULL COMMENT '端口',
    database_name VARCHAR(100) NOT NULL COMMENT '数据库名',
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    password_encrypted VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    password_salt VARCHAR(36) NOT NULL COMMENT '密码加密盐值',
    status VARCHAR(20) NOT NULL COMMENT '状态(ACTIVE/INACTIVE)',
    last_sync_time DATETIME COMMENT '最后同步时间',
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    modified_at DATETIME NOT NULL,
    modified_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_datasource_name (name)
);
```

### 1.2 元数据-表信息 (tbl_metadata_table)

```sql
CREATE TABLE tbl_metadata_table (
    id VARCHAR(36) PRIMARY KEY,
    datasource_id VARCHAR(36) NOT NULL COMMENT '数据源ID',
    table_name VARCHAR(100) NOT NULL COMMENT '表名',
    table_comment VARCHAR(500) COMMENT '表注释',
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    modified_at DATETIME NOT NULL,
    modified_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_metadata_table (datasource_id, table_name),
    KEY idx_metadata_table_datasource (datasource_id)
);
```

### 1.3 元数据-列信息 (tbl_metadata_column)

```sql
CREATE TABLE tbl_metadata_column (
    id VARCHAR(36) PRIMARY KEY,
    table_id VARCHAR(36) NOT NULL COMMENT '表ID',
    column_name VARCHAR(100) NOT NULL COMMENT '列名',
    column_type VARCHAR(50) NOT NULL COMMENT '数据类型',
    column_length INT COMMENT '长度',
    column_comment VARCHAR(500) COMMENT '列注释',
    is_nullable BOOLEAN NOT NULL COMMENT '是否可空',
    is_primary_key BOOLEAN NOT NULL COMMENT '是否主键',
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    modified_at DATETIME NOT NULL,
    modified_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_metadata_column (table_id, column_name),
    KEY idx_metadata_column_table (table_id)
);
```

### 1.4 表关系 (tbl_table_relation)

```sql
CREATE TABLE tbl_table_relation (
    id VARCHAR(36) PRIMARY KEY,
    source_table_id VARCHAR(36) NOT NULL COMMENT '源表ID',
    target_table_id VARCHAR(36) NOT NULL COMMENT '目标表ID',
    relation_type VARCHAR(20) NOT NULL COMMENT '关系类型(FOREIGN_KEY/AI_INFERENCE)',
    source_columns TEXT NOT NULL COMMENT '源表关联列(JSON)',
    target_columns TEXT NOT NULL COMMENT '目标表关联列(JSON)',
    confidence DECIMAL(5,2) COMMENT 'AI推断的置信度',
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    modified_at DATETIME NOT NULL,
    modified_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_table_relation (source_table_id, target_table_id),
    KEY idx_table_relation_source (source_table_id),
    KEY idx_table_relation_target (target_table_id)
);
```

### 1.5 查询配置 (tbl_query_config)

```sql
CREATE TABLE tbl_query_config (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '查询名称',
    description TEXT COMMENT '查询描述',
    datasource_id VARCHAR(36) NOT NULL COMMENT '数据源ID',
    sql_template TEXT NOT NULL COMMENT 'SQL模板',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    status VARCHAR(20) NOT NULL COMMENT '状态(DRAFT/PUBLISHED)',
    timeout INT NOT NULL DEFAULT 30 COMMENT '超时时间(秒)',
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    modified_at DATETIME NOT NULL,
    modified_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_query_config_name_version (name, version)
);
```

### 1.6 低代码配置 (tbl_lowcode_config)

```sql
CREATE TABLE tbl_lowcode_config (
    id VARCHAR(36) PRIMARY KEY,
    query_config_id VARCHAR(36) NOT NULL COMMENT '查询配置ID',
    display_type VARCHAR(20) NOT NULL COMMENT '显示类型(FORM/TABLE/CHART)',
    config_json TEXT NOT NULL COMMENT '配置JSON',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    status VARCHAR(20) NOT NULL COMMENT '状态(DRAFT/PUBLISHED)',
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    modified_at DATETIME NOT NULL,
    modified_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_lowcode_config_query_version (query_config_id, version)
);
```

### 1.7 查询历史 (tbl_query_history)

```sql
CREATE TABLE tbl_query_history (
    id VARCHAR(36) PRIMARY KEY,
    query_config_id VARCHAR(36) NOT NULL COMMENT '查询配置ID',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    parameters TEXT COMMENT '查询参数(JSON)',
    execution_time INT COMMENT '执行时间(毫秒)',
    status VARCHAR(20) NOT NULL COMMENT '状态(SUCCESS/FAILED)',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    KEY idx_query_history_user (user_id),
    KEY idx_query_history_query (query_config_id)
);
```

### 1.8 用户收藏 (tbl_user_favorite)

```sql
CREATE TABLE tbl_user_favorite (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    query_config_id VARCHAR(36) NOT NULL COMMENT '查询配置ID',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_user_favorite (user_id, query_config_id)
);
```

### 1.9 用户显示属性配置 (tbl_user_display_config)

```sql
CREATE TABLE tbl_user_display_config (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    query_config_id VARCHAR(36) NOT NULL COMMENT '查询配置ID',
    config_json TEXT NOT NULL COMMENT '配置JSON',
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL,
    created_by VARCHAR(36) NOT NULL,
    modified_at DATETIME NOT NULL,
    modified_by VARCHAR(36) NOT NULL,
    UNIQUE KEY u_idx_user_display_config (user_id, query_config_id)
);
```

## 2. 索引设计说明

1. 所有表都使用UUID作为主键
2. 创建合适的唯一索引确保数据一致性
3. 为外键关系创建普通索引提升查询性能
4. 考虑查询模式创建组合索引

## 3. 数据库规范

1. 表名使用 tbl_ 前缀
2. 字段名使用小写字母和下划线
3. 必须包含审计字段 (created_at, created_by, modified_at, modified_by)
4. 需要乐观锁的表包含 nonce 字段
5. 所有表和字段都必须有注释