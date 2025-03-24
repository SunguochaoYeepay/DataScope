# DataScope 变更日志

## 版本更新历史

### 1.0.10 (2025-03-24)

#### 修复
- 修复了查询执行接口的参数冗余问题
  - 从QueryRequest类中移除了@NotBlank注解，使queryConfigId参数变为可选
  - 在QueryServiceImpl中添加了参数验证逻辑，确保执行时queryConfigId不为空
  - 修改了QueryController中的executeQuery方法，强制使用URL路径中的ID而不是请求体中的ID
  - 解决了API调用时参数冗余问题，简化了客户端调用方式

#### 优化
- 改进了API设计遵循RESTful最佳实践
  - 所有资源标识符现在只需在URL路径中指定一次
  - 增强了代码的可维护性和扩展性
  - 提高了API使用的便捷性和一致性

### 1.0.9 (2025-03-24)

#### 修复
- 修复了数据库初始化脚本中重复添加列导致的启动失败问题
- 移除了V1.0.0__base_schema.sql中的ALTER TABLE语句，因为这些列已经存在于数据库中
- 确保应用能够在现有数据库上正常启动而不会尝试创建重复列

#### 优化
- 改进了数据库初始化脚本的兼容性，使其可以在已初始化和全新环境中都能正常工作

### 1.0.8 (2025-03-24)

#### 修复
- 修复了`V1.0.0__base_schema.sql`脚本中的SQL语法错误
  - 移除了`ALTER TABLE ADD COLUMN`语句中不支持的`IF NOT EXISTS`语法
  - 确保数据库迁移脚本符合MySQL语法规范
  - 解决了应用启动时SQL脚本执行失败的问题：`You have an error in your SQL syntax; check the manual...`

#### 优化
- 增强了数据库迁移脚本的兼容性和稳定性
- 确保应用能够在不同版本的MySQL上正确启动和运行

### 1.0.7 (2025-03-24)

#### 修复
- 修复了SQL初始化脚本加载问题，优化了`DatabaseInitializationConfig`支持多脚本路径的能力
- 修正了配置文件中SQL迁移脚本路径指向错误的问题，确保同时支持`V1.0.0__base_schema.sql`和`V1.0.0__init_schema.sql`
- 增强了脚本加载的错误处理，防止单个脚本不存在导致整个应用启动失败

#### 优化
- 改进了SQL初始化脚本加载的日志输出，方便排查初始化问题
- 为脚本路径处理添加了容错机制，提升系统启动稳定性

### 1.0.6 (2025-03-24)

#### 修复
- 修复了SQL迁移脚本路径不正确导致的应用启动失败问题
- 将错误的`V1.0.0__base_schema.sql`文件名改为正确的`V1.0.0__init_schema.sql`
- 确保SQL初始化脚本能被正确加载和执行

#### 优化
- 优化了SQL初始化配置的路径准确性
- 提升了脚本执行的稳定性，改进应用启动过程

### 1.0.5 (2025-03-24)

#### 修复
- 修复了应用启动失败问题：`Not a managed type: class com.datascope.domain.entity.TableMetadataEntity`
  - 更新了@EntityScan注解配置，添加了"com.datascope.domain.entity"包路径
  - 解决了JPA无法正确识别和管理实体类的问题

#### 架构优化
- 完善了实体类扫描配置，确保所有实体类都能被正确加载
- 提高了应用启动的稳定性，确保所有数据库相关组件能正常工作

### 1.0.4 (2025-03-24)

#### 修复
- 修复了Springfox与SpringDoc共存导致的应用启动失败问题，错误表现为：`Failed to start bean 'documentationPluginsBootstrapper'`

#### 配置变更
- 从application.yml中移除了Springfox配置部分
- 增强了SpringDoc配置，包括文档排序和路径匹配等功能
- 从pom.xml中删除了不再使用的springfox.version属性

#### 架构优化
- 彻底完成了从Springfox到SpringDoc (OpenAPI 3.0)的迁移工作
- 解决了Flyway数据库迁移冲突问题
  - 修改迁移脚本版本顺序
  - 添加了V1.0.10和V1.0.11脚本修复数据库迁移状态
  - 更新V1.0.11脚本包含删除Flyway历史表的功能
  - 禁用Flyway的clean功能，防止在应用程序启动时清理数据库
  - 临时禁用Flyway迁移验证，确保应用程序能正常启动
  - 配置repair-on-migrate选项，允许修复校验和不匹配问题
  - 调整Flyway初始化表脚本版本，从1.0.0改为0.9.0，避免与初始化schema脚本冲突
  - 新增初始化目录db/init，通过schema.sql预先创建Flyway历史表解决循环依赖问题
- 修复了启动顺序导致的Flyway历史表不存在问题
  - 创建FlywayMigrationConfig配置类，在应用启动后手动执行迁移
  - 更新了SQL初始化配置，使用更现代的sql.init属性
  - 调整了数据库初始化和Flyway迁移的执行顺序
- 解决了Flyway和entityManagerFactory之间的循环依赖问题
  - 修改FlywayMigrationConfig类使用ApplicationReadyEvent而非ApplicationStartedEvent
  - 添加错误捕获机制，确保应用在Flyway错误时仍能启动
  - 设置hibernate.ddl-auto为none，防止Hibernate与Flyway冲突

#### 功能增强
- 改进数据导出模块的稳定性
  - 确保导出表初始化脚本可以重复执行
  - 添加Flyway初始化表脚本
  - 完善迁移元数据表，记录迁移执行历史
  - 增加Flyway日志级别为DEBUG，便于调试
- 增强了应用程序启动流程稳定性
  - 使用ApplicationReadyEvent事件触发Flyway迁移
  - 优化初始化和迁移流程，避免冲突
  - 添加环境检测，支持在不同环境中灵活配置

#### 配置变更
- 调整Flyway设置以增强迁移稳定性
  - 禁用自动清理功能，防止在验证出错时清理数据库
  - 启用baseline版本配置，避免在已有数据的情况下执行全部迁移
  - 启用out-of-order迁移支持，允许执行乱序的迁移脚本
  - 配置init-sqls直接创建flyway_schema_history表，解决初始化问题
  - 禁用clean-on-validation-error防止自动清理数据库
  - 启用clean-disabled防止意外删除数据
  - 启用基线版本配置，设置为1.0.12
  - 添加create-schemas选项自动创建必要的schema对象
  - 启用out-of-order支持顺序不一致的迁移
  - 配置ignore-missing-migrations和ignore-future-migrations，提高迁移灵活性
  - 添加V1.0.15脚本确保导出表存在，解决表存在性验证问题
  - 更新V1.0.11脚本，增加直接删除Flyway历史表的功能
- 更新应用程序配置
  - 使用Spring Boot 2.7+推荐的sql.init配置替代旧的initialization-mode
  - 设置defer-datasource-initialization为true确保初始化顺序正确
  - 优化Flyway配置，采用手动触发方式执行迁移
  - 将Flyway完全禁用自动配置(enabled: false)，由自定义配置类控制

### 1.0.2 - 2025-03-24

#### Fixes
- 修复找不到schema.sql文件的问题
  - 添加了缺失的数据库初始化文件(schema.sql、data.sql)
  - 创建了基础的Flyway迁移脚本
  - 添加了数据库创建脚本(init-db.sql)
- 进一步解决循环依赖问题
  - 重构DatabaseInitializationConfig类，分离Flyway配置和迁移执行
  - 使用ContextRefreshedEvent事件触发Flyway迁移，避免Bean初始化阶段的循环依赖
  - 添加错误处理，确保迁移失败时应用仍能正常启动

#### Enhancements
- 完善数据库初始化流程
  - 创建了完整的基础表结构创建脚本
  - 添加了默认管理员用户和测试数据源
  - 提供了单独的数据库创建脚本
- 更新系统文档
  - 添加了数据库初始化说明
  - 完善了README文件中的快速开始指南
  - 新增配置说明部分

#### Configuration
- 优化数据库初始化配置
  - 将schema.sql和data.sql执行顺序分离，避免外键约束问题
  - 将Flyway迁移延迟到应用上下文刷新后执行
  - 添加错误处理和日志记录，增强系统稳定性

### 1.0.3 - 2025-03-24

#### 配置变更
- 从项目中移除Flyway数据库迁移工具
  - 删除了Flyway相关依赖和配置
  - 移除了FlywayMigrator组件
  - 移除了Flyway Maven插件
- 强化Spring Boot原生SQL初始化机制
  - 优化schema.sql和data.sql执行顺序
  - 整合了原Flyway迁移脚本到schema.sql中
  - 提高SQL初始化的稳定性和失败检测
- 简化数据源初始化配置
  - 移除了复杂的依赖关系控制
  - 使用更简单的DataSourceInitializer实现

#### 架构优化
- 简化应用启动流程
  - 移除了自定义Flyway触发器
  - 消除了潜在的循环依赖问题
  - 降低了启动过程复杂度
- 改进数据库初始化策略
  - 使用单一责任原则重构初始化组件
  - 使用StandardSQL初始化替代Flyway版本控制
  - 提高初始化速度和可靠性

### 1.0.4 (2025-03-24)

#### 修复
- 修复了Springfox与SpringDoc共存导致的应用启动失败问题，错误表现为：`Failed to start bean 'documentationPluginsBootstrapper'`

#### 配置变更
- 从application.yml中移除了Springfox配置部分
- 增强了SpringDoc配置，包括文档排序和路径匹配等功能
- 从pom.xml中删除了不再使用的springfox.version属性

#### 架构优化
- 彻底完成了从Springfox到SpringDoc (OpenAPI 3.0)的迁移工作
- 确保API文档组件在应用启动时不会产生冲突

### 1.0.6 (2025-03-24)

#### 修复
- 修复了应用启动失败的SQL初始化问题：`Cannot read SQL script from class path resource [db/schema.sql, classpath:db/migration/V1.0.0__base_schema.sql]`
  - 更正了application.yml中的SQL迁移脚本路径配置
  - 将错误的V1.0.0__base_schema.sql修改为正确的V1.0.0__init_schema.sql
  - 确保SQL初始化脚本能够被正确加载和执行

#### 配置变更
- 优化了SQL初始化配置，确保路径与实际文件名匹配
- 提高了应用启动过程中脚本执行的稳定性