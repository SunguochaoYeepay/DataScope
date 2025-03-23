# DataScope 任务进度跟踪

## 1. 任务执行状态记录

### 1.1 状态记录模板
```yaml
task_status:
  task_id: "TASK-001"
  name: "任务名称"
  start_time: "2024-03-20 10:00:00"
  current_status: "进行中"
  
  completed_steps:
    - timestamp: "2024-03-20 10:00:00"
      step: "任务开始"
      details: "已完成的具体内容"
      
    - timestamp: "2024-03-20 10:30:00"
      step: "风险评估"
      details: "评估结果和建议"
      
  current_step:
    step: "正在执行的步骤"
    start_time: "2024-03-20 11:00:00"
    expected_completion: "2024-03-20 12:00:00"
    
  next_steps:
    - step: "待执行步骤1"
      prerequisites: ["前置条件1", "前置条件2"]
    - step: "待执行步骤2"
      prerequisites: ["前置条件3"]
      
  blockers:
    - type: "阻塞类型"
      description: "阻塞描述"
      solution: "解决方案"
```

### 1.2 进度确认清单
```yaml
progress_checklist:
  preparation:
    - [ ] 理解需求和目标
    - [ ] 完成风险评估
    - [ ] 制定实施方案
    - [ ] 获得执行许可
    
  execution:
    - [ ] 开始执行任务
    - [ ] 记录执行步骤
    - [ ] 更新执行状态
    - [ ] 处理异常情况
    
  completion:
    - [ ] 验证执行结果
    - [ ] 更新相关文档
    - [ ] 提交变更记录
    - [ ] 完成总结报告
```

## 2. 任务执行记录

### 2.1 当前任务状态
```yaml
current_task:
  id: "TASK-{timestamp}"
  name: "当前执行的任务"
  priority: "优先级"
  status: "执行状态"
  
  progress:
    completed: ["已完成的步骤"]
    current: "当前步骤"
    pending: ["待完成的步骤"]
    
  tracking:
    start_time: "开始时间"
    last_update: "最后更新时间"
    estimated_completion: "预计完成时间"
```

### 2.2 执行日志
```yaml
execution_log:
  - timestamp: "时间戳"
    action: "执行的动作"
    details: "详细说明"
    status: "执行状态"
    
  - timestamp: "时间戳"
    action: "执行的动作"
    details: "详细说明"
    status: "执行状态"
```

## 3. 进度同步机制

### 3.1 状态更新规则
```yaml
status_update:
  frequency:
    - 每个步骤完成后
    - 遇到问题时
    - 状态变更时
    - 定时检查点
    
  content:
    - 当前进度
    - 完成情况
    - 遇到的问题
    - 下一步计划
```

### 3.2 进度回顾
```yaml
progress_review:
  daily:
    - 已完成工作
    - 当前进度
    - 遇到的问题
    - 下一步计划
    
  weekly:
    - 本周完成
    - 下周计划
    - 风险分析
    - 资源需求
```

## 4. 问题跟踪

### 4.1 问题记录
```yaml
issue_tracking:
  - issue_id: "ISSUE-001"
    type: "问题类型"
    description: "问题描述"
    status: "处理状态"
    solution: "解决方案"
    
  - issue_id: "ISSUE-002"
    type: "问题类型"
    description: "问题描述"
    status: "处理状态"
    solution: "解决方案"
```

### 4.2 解决方案
```yaml
solutions:
  - issue_id: "ISSUE-001"
    steps:
      - "解决步骤1"
      - "解决步骤2"
    status: "解决状态"
    verification: "验证方法"
```

## 5. 任务完成确认

### 5.1 完成检查清单
```yaml
completion_checklist:
  functionality:
    - [ ] 所有功能正常工作
    - [ ] 测试用例全部通过
    - [ ] 性能指标达标
    - [ ] 无严重bug
    
  documentation:
    - [ ] 代码注释完整
    - [ ] 文档已更新
    - [ ] 变更记录已提交
    - [ ] API文档已更新
    
  deployment:
    - [ ] 部署脚本准备就绪
    - [ ] 配置文件已更新
    - [ ] 数据迁移已测试
    - [ ] 回滚方案已准备
```

### 5.2 验收标准
```yaml
acceptance_criteria:
  functional:
    - 功能符合需求
    - 性能达到要求
    - 安全性符合标准
    
  technical:
    - 代码质量达标
    - 测试覆盖率满足要求
    - 文档完整规范
    
  business:
    - 满足业务目标
    - 用户反馈良好
    - 运维成本可接受
```

## 6. 进度报告模板

### 6.1 日报模板
```yaml
daily_report:
  date: "报告日期"
  
  completed:
    - task: "已完成任务1"
      details: "完成情况"
    - task: "已完成任务2"
      details: "完成情况"
      
  in_progress:
    - task: "进行中任务1"
      status: "当前状态"
      blockers: "遇到的问题"
      
  planned:
    - task: "计划任务1"
      priority: "优先级"
      timeline: "计划时间"
```

### 6.2 周报模板
```yaml
weekly_report:
  week: "第X周"
  period: "起止日期"
  
  achievements:
    - "本周完成的主要工作1"
    - "本周完成的主要工作2"
    
  challenges:
    - "遇到的主要问题1"
    - "遇到的主要问题2"
    
  next_week:
    - "下周计划1"
    - "下周计划2"
    
  risks:
    - "潜在风险1"
    - "潜在风险2"
```

## 7. 任务归档

### 7.1 归档内容
```yaml
archive_contents:
  task_info:
    - 任务描述
    - 执行过程
    - 完成状态
    - 遇到的问题
    
  deliverables:
    - 代码变更
    - 文档更新
    - 测试报告
    - 部署记录
    
  lessons_learned:
    - 经验总结
    - 改进建议
    - 最佳实践
```

### 7.2 归档检查
```yaml
archive_checklist:
  documentation:
    - [ ] 所有文档已更新
    - [ ] 变更记录完整
    - [ ] 问题解决方案已记录
    - [ ] 经验教训已总结
    
  code:
    - [ ] 代码已提交
    - [ ] 测试用例已更新
    - [ ] 构建脚本已更新
    - [ ] 部署文档已更新
    
  review:
    - [ ] 技术评审完成
    - [ ] 文档审查通过
    - [ ] 测试报告确认
    - [ ] 部署验证通过
```