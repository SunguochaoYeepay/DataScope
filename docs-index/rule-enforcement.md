# DataScope 规则执行和修正指南

## 1. 规则执行检查

### 1.1 检查清单
在每次对话开始时，您可以通过以下方式检查我是否遵守规则：

```yaml
conversation_start_check:
  - 提醒: "请确保遵守所有开发规范和要求"
  - 引用: "请参考<custom_instructions>中的规则"
  - 确认: "我会以'我开始拉~~'作为开始"
```

### 1.2 修正触发语句
当您发现我没有遵守规则时，可以使用以下标准语句来提醒我：

```yaml
correction_triggers:
  general:
    - "请遵守<custom_instructions>中的规则"
    - "你没有遵守规则[规则编号]"
    - "请按照规范[具体规范]执行"
    
  specific:
    workflow:
      - "请先评估风险再执行"
      - "请先给出方案再执行"
    
    code_quality:
      - "代码缺少类型注解"
      - "函数超过50行限制"
      - "缺少必要注释"
    
    documentation:
      - "请更新changelog"
      - "请更新README"
      - "缺少流程说明"
```

## 2. 规则违反识别

### 2.1 常见违规模式
```yaml
violation_patterns:
  workflow:
    - 未经评估直接执行
    - 未经同意直接修改
    - 未更新文档
    
  code_quality:
    - 缺少类型声明
    - 函数过长
    - 变量命名不规范
    
  security:
    - 硬编码敏感信息
    - 未处理异常
    - 未验证输入
```

### 2.2 修正要求格式
```yaml
correction_format:
  request:
    - 指出具体违反的规则
    - 说明期望的正确行为
    - 要求重新执行
    
  example:
    - "你违反了[规则1.2]，请先评估风险再执行"
    - "根据[规则2.3]，需要添加类型注解"
    - "按照[规则3.1]，请更新changelog"
```

## 3. 规则执行监督

### 3.1 监督检查点
```yaml
supervision_checkpoints:
  before_execution:
    - 风险评估完成
    - 方案已提出
    - 获得执行同意
    
  during_execution:
    - 遵守代码规范
    - 添加必要注释
    - 处理异常情况
    
  after_execution:
    - 更新相关文档
    - 完成测试验证
    - 提交变更记录
```

### 3.2 纠正流程
```yaml
correction_process:
  steps:
    1. 指出违规:
       - 明确指出哪条规则被违反
       - 说明违反的具体表现
    
    2. 要求修正:
       - 说明需要如何修正
       - 设定修正的期望结果
    
    3. 确认执行:
       - 等待修正完成
       - 验证修正结果
```

## 4. 规则遵守确认

### 4.1 确认清单
```yaml
compliance_checklist:
  workflow:
    - "我开始拉~~"开始标识
    - 风险评估已完成
    - 方案已获得同意
    
  code:
    - 包含类型注解
    - 函数长度合规
    - 命名规范合理
    
  documentation:
    - 更新changelog
    - 更新README
    - 添加必要注释
```

### 4.2 修正反馈
```yaml
correction_feedback:
  acknowledgment:
    - "收到修正要求，我将重新执行"
    - "明白，我会按照规则[规则编号]重做"
    
  confirmation:
    - "已按照规则完成修正"
    - "请检查修正是否符合要求"
```

## 5. 用户指导

### 5.1 如何提出修正要求
```yaml
correction_request_guide:
  format:
    - 明确指出违反的规则
    - 说明期望的行为
    - 要求重新执行
    
  examples:
    - "请遵守规则1.2，先进行风险评估"
    - "根据规则2.3，请添加类型注解"
    - "违反了规则3.1，需要更新changelog"
```

### 5.2 如何确认修正完成
```yaml
correction_verification:
  steps:
    1. 检查修正内容是否符合规则
    2. 验证所有相关文档是否更新
    3. 确认是否需要其他改进
    
  response:
    - 如果满意："修正已完成，符合要求"
    - 如果不满意："请继续修正[具体问题]"
```

## 6. 持续改进

### 6.1 规则优化建议
```yaml
rule_improvement:
  feedback:
    - 记录常见违规情况
    - 收集规则执行难点
    - 提出优化建议
    
  update:
    - 定期评审规则有效性
    - 根据实践调整规则
    - 完善规则执行机制
```

### 6.2 执行效果评估
```yaml
effectiveness_evaluation:
  metrics:
    - 规则遵守率
    - 修正请求次数
    - 完成质量评分
    
  improvement:
    - 分析常见问题
    - 优化执行流程
    - 更新规则说明
```