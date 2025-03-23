# 数据类型映射

## 1. MySQL数据类型映射

### 1.1 数值类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| tinyint(1) | Boolean | Switch | - | 用于布尔值 |
| tinyint | Integer | InputNumber | min, max | 范围：-128到127 |
| smallint | Integer | InputNumber | min, max | 范围：-32768到32767 |
| int | Integer | InputNumber | min, max | 范围：-2^31到2^31-1 |
| bigint | Long | InputNumber | min, max | 范围：-2^63到2^63-1 |
| float | Float | InputNumber | precision | 单精度浮点数 |
| double | Double | InputNumber | precision | 双精度浮点数 |
| decimal | BigDecimal | InputNumber | precision, scale | 精确的小数 |

### 1.2 字符串类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| char | String | Input | maxLength, fixedLength | 定长字符串 |
| varchar | String | Input | maxLength | 变长字符串 |
| text | String | TextArea | maxLength | 长文本 |
| longtext | String | TextArea | maxLength | 超长文本 |
| enum | String | Select | options | 枚举类型 |

### 1.3 日期时间类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| date | LocalDate | DatePicker | range | 日期 |
| time | LocalTime | TimePicker | range | 时间 |
| datetime | LocalDateTime | DateTimePicker | range | 日期时间 |
| timestamp | LocalDateTime | DateTimePicker | range | 时间戳 |

### 1.4 二进制类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| binary | byte[] | - | maxLength | 定长二进制 |
| varbinary | byte[] | - | maxLength | 变长二进制 |
| blob | byte[] | - | maxLength | 二进制大对象 |

## 2. DB2数据类型映射

### 2.1 数值类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| SMALLINT | Integer | InputNumber | min, max | 范围：-32768到32767 |
| INTEGER | Integer | InputNumber | min, max | 范围：-2^31到2^31-1 |
| BIGINT | Long | InputNumber | min, max | 范围：-2^63到2^63-1 |
| REAL | Float | InputNumber | precision | 单精度浮点数 |
| DOUBLE | Double | InputNumber | precision | 双精度浮点数 |
| DECIMAL | BigDecimal | InputNumber | precision, scale | 精确的小数 |

### 2.2 字符串类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| CHAR | String | Input | maxLength, fixedLength | 定长字符串 |
| VARCHAR | String | Input | maxLength | 变长字符串 |
| CLOB | String | TextArea | maxLength | 长文本 |
| DBCLOB | String | TextArea | maxLength | 双字节长文本 |

### 2.3 日期时间类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| DATE | LocalDate | DatePicker | range | 日期 |
| TIME | LocalTime | TimePicker | range | 时间 |
| TIMESTAMP | LocalDateTime | DateTimePicker | range | 时间戳 |

### 2.4 二进制类型
| 数据库类型 | Java类型 | 显示组件 | 验证规则 | 说明 |
|-----------|----------|----------|----------|------|
| BINARY | byte[] | - | maxLength | 定长二进制 |
| VARBINARY | byte[] | - | maxLength | 变长二进制 |
| BLOB | byte[] | - | maxLength | 二进制大对象 |

## 3. 显示组件配置

### 3.1 Input组件
```json
{
  "component": "Input",
  "props": {
    "allowClear": true,
    "maxLength": 100,
    "showCount": true,
    "placeholder": "请输入"
  },
  "validations": [
    {
      "type": "required",
      "message": "此项为必填项"
    },
    {
      "type": "maxLength",
      "value": 100,
      "message": "最多输入100个字符"
    }
  ]
}
```

### 3.2 InputNumber组件
```json
{
  "component": "InputNumber",
  "props": {
    "min": 0,
    "max": 100,
    "step": 1,
    "precision": 2,
    "placeholder": "请输入数字"
  },
  "validations": [
    {
      "type": "required",
      "message": "此项为必填项"
    },
    {
      "type": "range",
      "min": 0,
      "max": 100,
      "message": "请输入0-100之间的数字"
    }
  ]
}
```

### 3.3 DatePicker组件
```json
{
  "component": "DatePicker",
  "props": {
    "format": "YYYY-MM-DD",
    "showTime": false,
    "allowClear": true,
    "placeholder": "请选择日期"
  },
  "validations": [
    {
      "type": "required",
      "message": "此项为必填项"
    },
    {
      "type": "range",
      "min": "2000-01-01",
      "max": "2099-12-31",
      "message": "日期超出范围"
    }
  ]
}
```

### 3.4 Select组件
```json
{
  "component": "Select",
  "props": {
    "allowClear": true,
    "placeholder": "请选择",
    "options": [
      {
        "label": "选项1",
        "value": "1"
      },
      {
        "label": "选项2",
        "value": "2"
      }
    ]
  },
  "validations": [
    {
      "type": "required",
      "message": "此项为必填项"
    }
  ]
}
```

## 4. 验证规则

### 4.1 通用规则
```json
{
  "validations": {
    "required": {
      "message": "此项为必填项"
    },
    "maxLength": {
      "value": 100,
      "message": "最多输入{value}个字符"
    },
    "minLength": {
      "value": 1,
      "message": "最少输入{value}个字符"
    },
    "pattern": {
      "value": "^[A-Za-z0-9]+$",
      "message": "只能输入字母和数字"
    }
  }
}
```

### 4.2 数值规则
```json
{
  "validations": {
    "min": {
      "value": 0,
      "message": "不能小于{value}"
    },
    "max": {
      "value": 100,
      "message": "不能大于{value}"
    },
    "precision": {
      "value": 2,
      "message": "最多保留{value}位小数"
    }
  }
}
```

### 4.3 日期规则
```json
{
  "validations": {
    "range": {
      "min": "2000-01-01",
      "max": "2099-12-31",
      "message": "日期必须在{min}到{max}之间"
    },
    "format": {
      "value": "YYYY-MM-DD",
      "message": "日期格式不正确"
    }
  }
}
```