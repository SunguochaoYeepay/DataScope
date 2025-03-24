# 变更日志

## V1.0.7 - 2025.03.24
### 添加
- 添加查询管理模块
  - 实现查询配置相关接口，包括创建、更新、删除和查询
  - 实现查询执行接口，支持参数化查询和分页
  - 实现查询历史记录管理
  - 添加SQL渲染和分页工具类
  - 添加错误码和异常处理机制
  - 对接数据源管理模块，实现多数据源查询

### 修复
- 修复查询管理模块中数据源连接获取问题
  - 确保通过DataSourceService正确获取数据源连接
  - 添加数据源ID到DataSource对象的转换处理

## V1.0.6 - 2025.03.24
### 修复
- 修复元数据提取中MySQL INT类型映射错误
  - 移除ColumnType枚举中的INT类型，统一使用INTEGER表示整型
  - 修改MySQLMetadataExtractor中的类型映射逻辑，避免枚举常量不存在的错误
  - 更新DataPreviewServiceImpl中相关代码以适应类型变更
  - 解决了刷新数据源元数据时报系统错误 "No enum constant com.datascope.domain.model.metadata.ColumnType.INT" 的问题

## V1.0.5 - 2025.03.24
### 添加
- 添加了导出功能相关模型和表结构
  - 添加了导出配置(ExportConfig)实体及相关值对象
  - 添加了导出历史(ExportHistory)实体及相关值对象
  - 添加了列配置(ColumnConfig)和蒙版配置(MaskConfig)
  - 添加了过滤配置(FilterConfig)和过滤条件(FilterCondition)
  - 添加了export_config和export_history表结构
- 添加了导出功能API接口
  - 创建导出配置API
  - 更新导出配置API
  - 查询导出配置API
  - 执行导出API
  - 导出历史查询API
  - 导出文件下载API

### 修复
- 增强元数据提取功能的稳定性
  - 添加对NULL值的安全处理，防止NullPointerException
  - 添加对MySQL INT类型的支持，确保与数据库类型兼容
  - 改进了数据提取过程中的错误处理