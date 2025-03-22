# Changelog

## [Unreleased]

### Fixed
- 修复了 Modal 组件使用已弃用的 `visible` 属性的问题，更新为使用 `open` 属性
- 修复了 Table 组件使用已弃用的 `column.slots` 的问题，更新为使用 `v-slot:headerCell` 和 `v-slot:bodyCell`
- 修复了 ProTable 组件的类型定义和插槽实现
- 优化了全局样式和布局结构

### Changed
- 更新了 Ant Design Vue 组件的使用方式，采用最新的 API
- 优化了数据源管理、查询历史等页面的用户界面
- 改进了错误处理和状态管理

### Added
- 新增了主题配置支持
- 添加了更多的类型注解和代码注释
- 创建基础布局组件 `BasicLayout.vue`
  - 实现响应式侧边栏
  - 添加导航菜单
  - 集成用户头像和下拉菜单
  - 添加页面切换动画
- 配置路由系统
  - 实现路由守卫
  - 添加404页面
  - 配置页面标题
- 实现数据源管理页面 `DatasourceList.vue`
  - 支持数据源的增删改查
  - 添加连接测试功能
  - 使用ProTable展示数据
- 实现SQL编辑器页面 `SqlEditorView.vue`
  - 集成SQL编辑器组件
  - 支持数据源选择
  - 显示数据库表结构
  - 展示查询结果
- 实现查询历史页面 `QueryHistoryList.vue`
  - 支持按数据源和时间范围筛选
  - 展示查询统计信息
  - 显示Top 5慢查询
  - 查看查询详情
- 实现慢查询分析页面 `SlowQueriesList.vue`
  - 支持设置执行时间阈值
  - 展示执行计划
  - 提供优化建议
  - 显示统计信息和趋势

## [0.1.0] - 2024-03-XX
- 初始版本发布