# 详细设计

## 1. 数据类型映射规则

### 1.1 MySQL数据类型映射

```json
{
  "mysql": {
    "varchar": {
      "component": "Input",
      "validations": ["maxLength"],
      "props": {
        "allowClear": true,
        "showCount": true
      }
    },
    "char": {
      "component": "Input",
      "validations": ["maxLength", "fixedLength"],
      "props": {
        "allowClear": true
      }
    },
    "text": {
      "component": "TextArea",
      "validations": ["maxLength"],
      "props": {
        "autoSize": { "minRows": 2, "maxRows": 6 },
        "showCount": true
      }
    },
    "datetime": {
      "component": "DateTimePicker",
      "format": "YYYY-MM-DD HH:mm:ss",
      "props": {
        "showTime": true,
        "allowClear": true
      }
    },
    "date": {
      "component": "DatePicker",
      "format": "YYYY-MM-DD",
      "props": {
        "allowClear": true
      }
    },
    "decimal": {
      "component": "InputNumber",
      "validations": ["precision", "scale"],
      "props": {
        "step": 0.01,
        "precision": 2
      }
    },
    "int": {
      "component": "InputNumber",
      "validations": ["min", "max"],
      "props": {
        "step": 1,
        "precision": 0
      }
    },
    "tinyint": {
      "component": "Switch",
      "when": "length = 1",
      "props": {
        "checkedValue": 1,
        "uncheckedValue": 0
      }
    },
    "enum": {
      "component": "Select",
      "props": {
        "allowClear": true,
        "options": "dynamicOptions"
      }
    }
  }
}
```

### 1.2 DB2数据类型映射

```json
{
  "db2": {
    "varchar": {
      "component": "Input",
      "validations": ["maxLength"],
      "props": {
        "allowClear": true,
        "showCount": true
      }
    },
    "char": {
      "component": "Input",
      "validations": ["maxLength", "fixedLength"],
      "props": {
        "allowClear": true
      }
    },
    "clob": {
      "component": "TextArea",
      "validations": ["maxLength"],
      "props": {
        "autoSize": { "minRows": 2, "maxRows": 6 },
        "showCount": true
      }
    },
    "timestamp": {
      "component": "DateTimePicker",
      "format": "YYYY-MM-DD HH:mm:ss",
      "props": {
        "showTime": true,
        "allowClear": true
      }
    },
    "date": {
      "component": "DatePicker",
      "format": "YYYY-MM-DD",
      "props": {
        "allowClear": true
      }
    },
    "decimal": {
      "component": "InputNumber",
      "validations": ["precision", "scale"],
      "props": {
        "step": 0.01,
        "precision": 2
      }
    },
    "integer": {
      "component": "InputNumber",
      "validations": ["min", "max"],
      "props": {
        "step": 1,
        "precision": 0
      }
    }
  }
}
```

## 2. 低代码配置JSON结构

### 2.1 查询条件配置

```json
{
  "conditions": [
    {
      "field": "username",
      "label": "用户名",
      "component": "Input",
      "required": true,
      "defaultValue": null,
      "order": 1,
      "visible": true,
      "props": {
        "allowClear": true,
        "placeholder": "请输入用户名"
      },
      "validations": [
        {
          "type": "maxLength",
          "value": 50,
          "message": "用户名最多50个字符"
        }
      ]
    },
    {
      "field": "age",
      "label": "年龄",
      "component": "InputNumber",
      "required": false,
      "defaultValue": null,
      "order": 2,
      "visible": true,
      "props": {
        "min": 0,
        "max": 150,
        "placeholder": "请输入年龄"
      }
    },
    {
      "field": "status",
      "label": "状态",
      "component": "Select",
      "required": true,
      "defaultValue": "active",
      "order": 3,
      "visible": true,
      "props": {
        "options": [
          {"label": "活跃", "value": "active"},
          {"label": "禁用", "value": "disabled"}
        ],
        "allowClear": true
      }
    }
  ]
}
```

### 2.2 结果列配置

```json
{
  "results": [
    {
      "field": "id",
      "label": "ID",
      "width": 100,
      "fixed": "left",
      "sortable": false,
      "visible": true
    },
    {
      "field": "username",
      "label": "用户名",
      "width": 150,
      "sortable": true,
      "visible": true,
      "maskRule": {
        "type": "partial",
        "start": 3,
        "end": 7,
        "mask": "*"
      }
    },
    {
      "field": "email",
      "label": "邮箱",
      "width": 200,
      "sortable": true,
      "visible": true,
      "maskRule": {
        "type": "regex",
        "pattern": "(?<=.{3}).(?=.*@)",
        "mask": "*"
      }
    },
    {
      "field": "amount",
      "label": "金额",
      "width": 120,
      "sortable": true,
      "visible": true,
      "format": {
        "type": "number",
        "precision": 2,
        "prefix": "¥"
      }
    }
  ]
}
```

### 2.3 操作列配置

```json
{
  "operations": [
    {
      "type": "link",
      "label": "查看",
      "action": {
        "type": "navigate",
        "target": "/detail/{id}"
      },
      "visible": true,
      "permissions": ["view"]
    },
    {
      "type": "button",
      "label": "编辑",
      "action": {
        "type": "modal",
        "target": "EditForm",
        "params": {
          "id": "{id}"
        }
      },
      "visible": true,
      "permissions": ["edit"],
      "style": {
        "type": "primary",
        "size": "small"
      }
    },
    {
      "type": "button",
      "label": "删除",
      "action": {
        "type": "confirm",
        "title": "确认删除",
        "content": "是否确认删除该记录？",
        "api": "/api/v1/records/{id}",
        "method": "DELETE"
      },
      "visible": true,
      "permissions": ["delete"],
      "style": {
        "type": "danger",
        "size": "small"
      }
    }
  ]
}
```

## 3. 显示属性配置

### 3.1 用户显示属性

```json
{
  "queryId": "query123",
  "userId": "user123",
  "settings": {
    "conditions": {
      "hiddenFields": ["status", "createTime"],
      "defaultValues": {
        "status": "active"
      }
    },
    "results": {
      "columns": [
        {
          "field": "id",
          "width": 100,
          "visible": false
        },
        {
          "field": "username",
          "width": 150,
          "visible": true,
          "fixed": "left"
        }
      ],
      "sortInfo": {
        "field": "createTime",
        "order": "desc"
      }
    }
  }
}
```

### 3.2 智能推荐配置

```json
{
  "recommendations": {
    "conditions": {
      "frequentlyUsed": ["username", "status"],
      "rarelyUsed": ["createTime", "updateTime"],
      "defaultCollapsed": true
    },
    "results": {
      "preferredColumns": ["username", "email", "phone"],
      "preferredOrder": ["username", "status", "createTime"]
    }
  }
}
```

## 4. 查询优化配置

### 4.1 查询超时配置

```json
{
  "timeout": {
    "default": 30,
    "maximum": 120,
    "rules": [
      {
        "condition": "rows > 10000",
        "timeout": 60
      },
      {
        "condition": "joins > 3",
        "timeout": 90
      }
    ]
  }
}
```

### 4.2 查询限流配置

```json
{
  "rateLimiting": {
    "user": {
      "requestsPerMinute": 60,
      "burstSize": 100
    },
    "api": {
      "requestsPerMinute": 1000,
      "burstSize": 2000
    },
    "download": {
      "requestsPerMinute": 10,
      "burstSize": 20,
      "maxRows": 50000
    }
  }
}
```

## 5. 缓存策略配置

```json
{
  "cache": {
    "metadata": {
      "ttl": 3600,
      "strategy": "write-through"
    },
    "queryResult": {
      "ttl": 300,
      "maxSize": "100MB",
      "strategy": "lru"
    },
    "userPreference": {
      "ttl": 1800,
      "strategy": "write-back"
    }
  }
}
```