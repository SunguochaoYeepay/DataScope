#!/usr/bin/env python3
import sys
import json
import re
import os
from datetime import datetime
from typing import List, Dict, Tuple, Optional

def load_rules(rules_file: str = None) -> Dict:
    """加载规则配置"""
    if rules_file is None:
        # 获取当前脚本所在目录
        script_dir = os.path.dirname(os.path.abspath(__file__))
        rules_file = os.path.join(script_dir, "rules.json")
    """加载规则配置"""
    try:
        with open(rules_file, "r", encoding="utf-8") as f:
            return json.load(f)
    except Exception as e:
        print(f"Error loading rules: {e}")
        return {}

def check_workflow_rules(content: str, rules: Dict) -> List[Tuple[str, str, str]]:
    """检查工作流程规范"""
    issues = []
    for rule_name, rule in rules.items():
        pattern = rule["pattern"]
        if rule["required"]:
            if not re.search(pattern, content, re.MULTILINE):
                issues.append(("workflow", rule_name, rule["description"]))
    return issues

def check_code_quality_rules(content: str, rules: Dict) -> List[Tuple[str, str, str]]:
    """检查代码质量规范"""
    issues = []
    code_blocks = re.findall(r"```.*?\n(.*?)```", content, re.DOTALL)
    if not code_blocks:
        return []
        
    for code in code_blocks:
        if not code.strip():
            continue
            
        for rule_name, rule in rules.items():
            pattern = rule["pattern"]
            if rule["required"]:
                if rule_name == "function_length":
                    # 检查函数长度
                    functions = re.finditer(r"def\s+\w+[^:]*:[\s\S]*?(?=\n\s*def|\Z)", code)
                    for func in functions:
                        if len(func.group().split('\n')) > 50:
                            issues.append(("code_quality", rule_name, rule["description"]))
                else:
                    # 其他规则检查
                    if not re.search(pattern, code, re.MULTILINE):
                        issues.append(("code_quality", rule_name, rule["description"]))
    return issues

def check_security_rules(content: str, rules: Dict) -> List[Tuple[str, str, str]]:
    """检查安全规范"""
    issues = []
    code_blocks = re.findall(r"```.*?\n(.*?)```", content, re.DOTALL)
    if not code_blocks:
        return []
        
    for code in code_blocks:
        if not code.strip():
            continue
            
        for rule_name, rule in rules.items():
            pattern = rule["pattern"]
            if rule_name == "sensitive_info":
                # 检查敏感信息
                matches = re.findall(pattern, code, re.MULTILINE)
                if matches:
                    issues.append(("security", rule_name, rule["description"]))
            elif rule["required"]:
                # 其他必需规则检查
                if not re.search(pattern, code, re.MULTILINE):
                    issues.append(("security", rule_name, rule["description"]))
    return issues

def generate_report(issues: List[Tuple[str, str, str]]) -> str:
    """生成检查报告"""
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    report = [
        "# AI响应规范检查报告",
        f"检查时间: {now}\n"
    ]
    
    if not issues:
        report.append("✅ 检查通过，未发现问题")
    else:
        report.append("⚠️ 发现以下问题：")
        for category, rule_name, description in issues:
            report.append(f"- [{category}] {description}")
            
    return "\n".join(report)

def check_response(content: str) -> List[Tuple[str, str, str]]:
    """检查AI响应内容"""
    rules = load_rules()
    if not rules:
        return []
        
    issues = []
    issues.extend(check_workflow_rules(content, rules.get("workflow_rules", {})))
    issues.extend(check_code_quality_rules(content, rules.get("code_quality_rules", {})))
    issues.extend(check_security_rules(content, rules.get("security_rules", {})))
    return issues

def main():
    # 从标准输入读取内容
    content = sys.stdin.read()
    
    # 检查内容
    issues = check_response(content)
    
    # 生成并打印报告
    report = generate_report(issues)
    print(report)
    
    # 根据是否有问题设置返回值
    return 1 if issues else 0

if __name__ == "__main__":
    sys.exit(main())