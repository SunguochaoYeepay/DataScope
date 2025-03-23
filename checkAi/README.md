# AI响应规范检查工具

## 功能说明
这个工具用于检查AI的响应是否符合预定的规范要求，包括：
1. 工作流程规范
2. 代码质量规范
3. 安全规范

## 使用方法

### 基本用法
```bash
# 从文件检查
cat response.txt | python3 check.py

# 直接检查剪贴板内容 (MacOS)
pbpaste | python3 check.py

# 直接检查剪贴板内容 (Linux)
xclip -o | python3 check.py

# 直接检查剪贴板内容 (Windows)
powershell Get-Clipboard | python3 check.py
```

## 检查规则

### 工作流程规则
- 响应必须以"我开始拉~~"开始
- 必须包含风险评估
- 执行前必须提供方案
- 必须更新changelog
- 必须更新README

### 代码质量规则
- 必须包含类型注解
- 函数不能超过50行
- 变量命名必须规范
- 必须包含必要注释

### 安全规则
- 不能包含硬编码的敏感信息
- 必须包含输入验证
- 必须包含异常处理

## 配置文件
规则配置存储在 `rules.json` 文件中，可以根据需要修改规则：

```json
{
    "rule_category": {
        "rule_name": {
            "pattern": "正则表达式模式",
            "description": "规则描述",
            "required": true/false
        }
    }
}
```

## 输出示例
```
# AI响应规范检查报告
检查时间: 2024-03-20 15:30:00

⚠️ 发现以下问题：
- [workflow] 响应未以'我开始拉~~'开始
- [code_quality] 代码缺少类型注解
- [security] 代码中可能包含硬编码的敏感信息
```

## 返回值
- 0: 检查通过
- 1: 检查发现问题或执行出错

## 注意事项
1. 确保Python 3.6或更高版本
2. 检查文件使用UTF-8编码
3. 规则可以在rules.json中配置和调整
4. 建议将检查集成到代码审查流程中