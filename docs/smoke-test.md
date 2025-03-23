# DataScope 冒烟测试清单

## 一、数据源管理模块

### 1. 数据源连接测试
- [ ] MySQL数据源连接
  ```sql
  -- 测试数据
  INSERT INTO data_source (name, type, host, port, database_name, username, password, status)
  VALUES ('test-mysql', 'MYSQL', 'localhost', 3306, 'test_db', 'test_user', 'test_pass', 1);
  ```
  - 验证点：
    1. 连接成功
    2. 返回正确的错误信息（密码错误等）

- [ ] PostgreSQL数据源连接
  ```sql
  -- 测试数据
  INSERT INTO data_source (name, type, host, port, database_name, username, password, status)
  VALUES ('test-pg', 'POSTGRESQL', 'localhost', 5432, 'test_db', 'test_user', 'test_pass', 1);
  ```
  - 验证点：
    1. 连接成功
    2. 返回正确的错误信息

### 2. 数据源管理操作
- [ ] 创建数据源
- [ ] 修改数据源
- [ ] 删除数据源
- [ ] 查询数据源列表

## 二、元数据管理模块

### 1. 元数据提取
- [ ] MySQL元数据提取
  - 验证点：
    1. 表信息提取
    2. 字段信息提取
    3. 索引信息提取

- [ ] PostgreSQL元数据提取
  - 验证点：
    1. 表信息提取
    2. 字段信息提取
    3. 索引信息提取

### 2. 元数据管理
- [ ] 元数据更新
- [ ] 元数据查询
- [ ] 元数据同步

## 三、数据预览模块

### 1. 基础预览功能
- [ ] 表数据预览
  ```sql
  -- 测试表
  CREATE TABLE test_table (
    id INT PRIMARY KEY,
    name VARCHAR(50),
    age INT
  );
  ```
  - 验证点：
    1. 分页功能
    2. 排序功能
    3. 过滤功能

### 2. 数据采样功能
- [ ] 随机采样
- [ ] 首N条采样

## 测试环境要求

### 1. 数据库环境
```sql
-- 创建测试数据库
CREATE DATABASE datascope_test;

-- 创建测试用户
CREATE USER 'test_user'@'localhost' IDENTIFIED BY 'test_pass';
GRANT ALL PRIVILEGES ON datascope_test.* TO 'test_user'@'localhost';
```

### 2. 测试数据准备
```sql
-- 初始化测试数据
INSERT INTO test_table VALUES
(1, 'test1', 20),
(2, 'test2', 25),
(3, 'test3', 30);
```

## 测试步骤

1. 环境准备
   ```bash
   # 启动测试环境
   cd scripts
   ./start-test.sh
   ```

2. 执行测试
   ```bash
   # 运行冒烟测试
   mvn test -Dtest=SmokeTest
   ```

3. 检查结果
   - 查看测试报告
   - 验证日志输出
   - 检查数据状态

## 问题记录

| 问题ID | 问题描述 | 严重程度 | 状态 |
|--------|----------|----------|------|
| 001    | 示例问题 | 低       | 待修复 |

## 测试通过标准

1. 所有核心功能测试通过
2. 无阻塞性bug
3. 无高危安全漏洞
4. 性能符合基本要求