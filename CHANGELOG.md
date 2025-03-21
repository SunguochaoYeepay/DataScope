# Changelog

所有重要的更改都会记录在此文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)。

## [Unreleased]

### Added

- 添加前端组件
  - 添加仪表板组件
    - 创建WidgetContainer基础容器组件
    - 支持图表、指标、文本、过滤器等多种组件类型
    - 实现组件的编辑、复制、删除等操作
    - 添加数据刷新和错误重试功能
    - 支持图表全屏显示和数据导出
  
  - 添加可视化组件
    - 创建ChartPreview图表预览组件
    - 支持多种图表类型（折线图、柱状图、饼图等）
    - 实现数据集选择和配置功能
    - 添加图表配置和样式设置

  - 添加系统设置
    - 创建Settings设置页面
    - 支持个人信息设置
    - 实现安全设置（密码修改）
    - 添加通知设置
    - 支持主题设置（明暗主题、界面密度）

## [Unreleased]

### Added

- 创建项目基础结构
  - 创建多模块Maven项目
  - 配置依赖管理
  - 添加基础工具类

- 添加领域模型
  - BaseEntity基础实体
  - Response统一响应
  - PageRequest/PageResponse分页
  - ErrorCode错误码
  - BusinessException业务异常

- 添加数据源管理
  - DataSourceType数据源类型
  - DataSource数据源配置
  - Schema模式定义
  - Table表定义
  - Column列定义
  - Relationship关系定义

- 添加查询管理
  - Query查询定义
  - QueryHistory查询历史
  - LowCodeConfig低代码配置
  - DisplayConfig显示配置

- 添加数据访问层
  - 创建Mapper接口
  - 实现基础CRUD操作
  - 添加批量操作支持
  - 配置MyBatis映射

- 添加API层
  - 创建QueryHistoryController
  - 实现历史查询接口
  - 添加统计分析接口
  - 配置Swagger文档

- 完善API文档
  - 添加详细的API描述
  - 添加请求/响应示例
  - 添加错误码说明
  - 添加使用场景说明
  - 添加参数验证规则
  - 添加统计指标说明

### Changed

- 更新项目文档
  - 完善README说明
  - 添加API文档
  - 更新模块说明
  - 优化API文档结构
  - 添加更多使用示例

### Fixed

- 修复类型注解
- 完善异常处理
- 规范代码格式
- 完善参数验证

## [1.0.0] - 2024-03-XX

### Added

- 初始版本发布