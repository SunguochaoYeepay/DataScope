# DataScope UI

DataScope 是一个现代化的数据库管理和查询分析工具。

## 最新更新

- 优化了组件库的使用，采用最新的 Ant Design Vue API
- 改进了用户界面和交互体验
- 修复了已知的组件兼容性问题
- 增强了类型安全性和代码质量

## 开发指南

### 环境要求

- Node.js >= 16
- npm >= 8

### 安装依赖

```bash
npm install
```

### 开发服务器

```bash
npm run dev
```

应用将在 http://localhost:3000 启动

### 构建生产版本

```bash
npm run build
```

### 代码规范

- 使用 TypeScript 进行开发
- 遵循 ESLint 规则
- 使用 Prettier 进行代码格式化
- 组件采用 Composition API
- 保持代码注释完整

### 项目结构

```
src/
  ├── assets/        # 静态资源
  ├── components/    # 公共组件
  ├── layouts/       # 布局组件
  ├── router/        # 路由配置
  ├── stores/        # 状态管理
  ├── styles/        # 全局样式
  ├── types/         # 类型定义
  ├── utils/         # 工具函数
  └── views/         # 页面组件
```

## 主要功能

- 数据源管理
  - 支持多种数据库类型
  - 连接测试
  - 元数据同步
- SQL 编辑器
  - 语法高亮
  - 自动完成
  - 执行计划分析
- 查询历史
  - 执行记录
  - 性能分析
  - 优化建议
- 慢查询分析
  - 性能监控
  - 优化建议
  - 趋势分析

## 技术栈

- Vue 3
- TypeScript
- Ant Design Vue
- Vite
- Pinia
- Vue Router

## 贡献指南

1. Fork 本仓库
2. 创建特性分支
3. 提交变更
4. 推送到分支
5. 提交 Pull Request

## 许可证

[MIT](LICENSE)

## 联系方式

- 作者：Your Name
- 邮箱：your.email@example.com
- 项目地址：https://github.com/yourusername/datascope-ui