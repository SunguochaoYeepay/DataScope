# DataScope UI

DataScope是一个现代化的数据库管理平台，提供直观的界面来管理数据源、执行SQL查询、分析查询性能等功能。

## 功能特点

- 数据源管理
  - 支持多种数据库类型
  - 数据源连接测试
  - 数据源状态监控
  
- SQL编辑器
  - 语法高亮
  - 自动补全
  - 格式化SQL
  - 查询结果导出
  
- 查询历史
  - 历史记录查询
  - 查询统计分析
  - 查询详情查看
  
- 慢查询分析
  - 慢查询识别
  - 执行计划分析
  - 优化建议
  - 性能趋势分析

## 技术栈

- Vue 3
- TypeScript
- Ant Design Vue
- Vue Router
- Pinia
- Axios
- Monaco Editor

## 开发环境要求

- Node.js >= 16
- npm >= 7

## 安装和运行

1. 克隆项目
```bash
git clone https://github.com/yourusername/datascope-ui.git
cd datascope-ui
```

2. 安装依赖
```bash
npm install
```

3. 启动开发服务器
```bash
npm run dev
```

4. 构建生产版本
```bash
npm run build
```

## 项目结构

```
datascope-ui/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── layouts/         # 布局组件
│   ├── views/           # 页面组件
│   ├── router/          # 路由配置
│   ├── store/           # 状态管理
│   ├── utils/           # 工具函数
│   ├── api/            # API接口
│   └── types/          # TypeScript类型定义
├── public/             # 公共文件
├── tests/             # 测试文件
└── package.json       # 项目配置
```

## 开发规范

- 遵循TypeScript类型定义
- 使用ESLint和Prettier保持代码风格一致
- 组件和函数必须包含注释
- 提交代码前运行测试
- 遵循语义化版本控制

## 测试

```bash
# 运行单元测试
npm run test:unit

# 运行端到端测试
npm run test:e2e
```

## 部署

1. 构建生产版本
```bash
npm run build
```

2. 将`dist`目录部署到Web服务器

## 贡献指南

1. Fork项目
2. 创建特性分支
3. 提交变更
4. 推送到分支
5. 创建Pull Request

## 许可证

[MIT](LICENSE)

## 联系方式

- 作者：Your Name
- 邮箱：your.email@example.com
- 项目地址：https://github.com/yourusername/datascope-ui