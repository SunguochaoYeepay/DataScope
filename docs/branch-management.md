# 分支管理规范

## 分支命名规范

### 主要分支
- `master`: 主分支，用于生产环境部署
- `develop`: 开发分支，用于功能集成和测试

### 功能分支
- 格式：`feature/{module}-{description}`
- 示例：`feature/user-login`

### 修复分支
- 格式：`fix/{module}-{issue-id}`
- 示例：`fix/auth-123`

### 发布分支
- 格式：`release/v{major}.{minor}.{patch}`
- 示例：`release/v1.2.0`

### 热修复分支
- 格式：`hotfix/{description}`
- 示例：`hotfix/user-login-fix`

## 工作流程

1. 功能开发
   ```
   # 从develop分支创建功能分支
   git checkout develop
   git pull
   git checkout -b feature/new-feature
   
   # 开发完成后合并回develop分支
   git checkout develop
   git merge feature/new-feature
   ```

2. Bug修复
   ```
   # 从develop分支创建修复分支
   git checkout develop
   git pull
   git checkout -b fix/bug-fix
   
   # 修复完成后合并回develop分支
   git checkout develop
   git merge fix/bug-fix
   ```

3. 版本发布
   ```
   # 从develop分支创建发布分支
   git checkout develop
   git pull
   git checkout -b release/v1.0.0
   
   # 测试和修复
   # 完成后合并到master和develop
   git checkout master
   git merge release/v1.0.0
   git checkout develop
   git merge release/v1.0.0
   ```

## 提交规范

1. 提交信息格式
   ```
   <type>(<scope>): <subject>
   
   <body>
   
   <footer>
   ```

2. Type类型
   - feat: 新功能
   - fix: Bug修复
   - docs: 文档更新
   - style: 代码格式化
   - refactor: 代码重构
   - test: 测试相关
   - chore: 构建/工具链/辅助工具

3. 示例
   ```
   feat(user): 添加用户登录功能
   
   - 实现用户名密码登录
   - 添加JWT token生成
   - 添加登录测试用例
   
   Closes #123
   ```

## 合并请求规范

1. 创建PR前
   - 确保代码已格式化
   - 确保测试通过
   - 更新相关文档
   - 添加必要的注释

2. PR描述
   - 使用PR模板
   - 清晰描述变更内容
   - 关联相关issue
   - 添加测试说明

3. 代码审查
   - 至少需要一个审查者批准
   - 解决所有评论
   - 确保CI检查通过

## 发布流程

1. 版本号规范
   - 主版本号：不兼容的API修改
   - 次版本号：向下兼容的功能性新增
   - 修订号：向下兼容的问题修正

2. 发布步骤
   ```
   # 创建发布分支
   git checkout develop
   git pull
   git checkout -b release/v1.0.0
   
   # 更新版本号
   mvn versions:set -DnewVersion=1.0.0
   
   # 更新CHANGELOG.md
   
   # 提交更改
   git add .
   git commit -m "chore(release): prepare v1.0.0"
   
   # 合并到master
   git checkout master
   git merge release/v1.0.0
   git tag -a v1.0.0 -m "Release v1.0.0"
   
   # 合并回develop
   git checkout develop
   git merge release/v1.0.0
   
   # 推送
   git push origin master develop --tags
   ```