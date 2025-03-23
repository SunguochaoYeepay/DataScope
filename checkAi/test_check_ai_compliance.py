import unittest
import json
import os
from check_ai_compliance import (
    load_rules,
    check_workflow_rules,
    check_code_quality_rules,
    check_security_rules,
    generate_report
)

class TestAIComplianceChecker(unittest.TestCase):
    def setUp(self):
        # 创建测试用的规则文件
        self.test_rules = {
            "workflow_rules": {
                "start_statement": {
                    "pattern": "^我开始拉~~",
                    "description": "响应必须以'我开始拉~~'开始",
                    "required": True
                }
            },
            "code_quality_rules": {
                "type_annotation": {
                    "pattern": "def.*\\) -> \\w+:",
                    "description": "函数必须包含类型注解",
                    "required": True
                }
            },
            "security_rules": {
                "sensitive_info": {
                    "pattern": "(password|secret|key)\\s*=\\s*['\"][^'\"]+['\"]",
                    "description": "不能包含硬编码的敏感信息",
                    "required": False
                }
            }
        }
        
        with open("test_rules.json", "w", encoding="utf-8") as f:
            json.dump(self.test_rules, f, ensure_ascii=False, indent=4)

    def tearDown(self):
        # 清理测试文件
        if os.path.exists("test_rules.json"):
            os.remove("test_rules.json")

    def test_load_rules(self):
        """测试规则加载功能"""
        rules = load_rules("test_rules.json")
        self.assertEqual(rules, self.test_rules)

    def test_workflow_rules_pass(self):
        """测试工作流规则检查 - 通过场景"""
        content = "我开始拉~~\n执行任务A"
        issues = check_workflow_rules(content, self.test_rules["workflow_rules"])
        self.assertEqual(len(issues), 0)

    def test_workflow_rules_fail(self):
        """测试工作流规则检查 - 失败场景"""
        content = "开始执行任务A"
        issues = check_workflow_rules(content, self.test_rules["workflow_rules"])
        self.assertEqual(len(issues), 1)

    def test_code_quality_rules_pass(self):
        """测试代码质量规则检查 - 通过场景"""
        content = "def process_data(input: str) -> str:"
        issues = check_code_quality_rules(content, self.test_rules["code_quality_rules"])
        self.assertEqual(len(issues), 0)

    def test_code_quality_rules_fail(self):
        """测试代码质量规则检查 - 失败场景"""
        content = "def process_data(input):"
        issues = check_code_quality_rules(content, self.test_rules["code_quality_rules"])
        self.assertEqual(len(issues), 1)

    def test_security_rules_pass(self):
        """测试安全规则检查 - 通过场景"""
        content = "config = load_config()"
        issues = check_security_rules(content, self.test_rules["security_rules"])
        self.assertEqual(len(issues), 0)

    def test_security_rules_fail(self):
        """测试安全规则检查 - 失败场景"""
        content = "password = 'secret123'"
        issues = check_security_rules(content, self.test_rules["security_rules"])
        self.assertEqual(len(issues), 1)

    def test_generate_report(self):
        """测试报告生成功能"""
        issues = [
            ("workflow", "start_statement", "响应必须以'我开始拉~~'开始"),
            ("code_quality", "type_annotation", "函数必须包含类型注解")
        ]
        report = generate_report(issues)
        self.assertIn("AI响应规范检查报告", report)
        self.assertIn("workflow", report)
        self.assertIn("code_quality", report)

if __name__ == '__main__':
    unittest.main()