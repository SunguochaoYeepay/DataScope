# API接口设计

## 1. 数据源管理接口

### 1.1 创建数据源

```yaml
POST /api/v1/datasources
请求体:
{
    "name": "string",
    "type": "string",
    "host": "string",
    "port": "integer",
    "databaseName": "string",
    "username": "string",
    "password": "string"
}
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "id": "string",
        "name": "string",
        "type": "string",
        "status": "string"
    }
}
```

### 1.2 获取数据源列表

```yaml
GET /api/v1/datasources
请求参数:
    page: integer
    size: integer
    status: string (optional)
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "total": "integer",
        "items": [{
            "id": "string",
            "name": "string",
            "type": "string",
            "status": "string",
            "lastSyncTime": "string"
        }]
    }
}
```

### 1.3 测试数据源连接

```yaml
POST /api/v1/datasources/{id}/test
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "success": "boolean",
        "errorMessage": "string"
    }
}
```

## 2. 元数据管理接口

### 2.1 获取表列表

```yaml
GET /api/v1/metadata/tables
请求参数:
    datasourceId: string
    keyword: string (optional)
    page: integer
    size: integer
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "total": "integer",
        "items": [{
            "id": "string",
            "tableName": "string",
            "tableComment": "string"
        }]
    }
}
```

### 2.2 获取列信息

```yaml
GET /api/v1/metadata/columns
请求参数:
    tableId: string
响应体:
{
    "code": "integer",
    "message": "string",
    "data": [{
        "id": "string",
        "columnName": "string",
        "columnType": "string",
        "columnLength": "integer",
        "columnComment": "string",
        "isNullable": "boolean",
        "isPrimaryKey": "boolean"
    }]
}
```

## 3. 查询管理接口

### 3.1 创建查询配置

```yaml
POST /api/v1/queries
请求体:
{
    "name": "string",
    "description": "string",
    "datasourceId": "string",
    "sqlTemplate": "string",
    "timeout": "integer"
}
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "id": "string",
        "name": "string",
        "version": "integer"
    }
}
```

### 3.2 执行查询

```yaml
POST /api/v1/queries/{id}/execute
请求体:
{
    "parameters": {
        "key1": "value1",
        "key2": "value2"
    },
    "pagination": {
        "page": "integer",
        "size": "integer"
    }
}
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "total": "integer",
        "columns": [{
            "name": "string",
            "type": "string"
        }],
        "rows": [
            ["value1", "value2"]
        ]
    }
}
```

## 4. 低代码配置接口

### 4.1 创建低代码配置

```yaml
POST /api/v1/lowcode/configs
请求体:
{
    "queryConfigId": "string",
    "displayType": "string",
    "config": {
        "conditions": [{
            "field": "string",
            "label": "string",
            "component": "string",
            "required": "boolean",
            "defaultValue": "any",
            "order": "integer",
            "visible": "boolean"
        }],
        "results": [{
            "field": "string",
            "label": "string",
            "width": "integer",
            "sortable": "boolean",
            "maskRule": {
                "type": "string",
                "start": "integer",
                "end": "integer"
            }
        }],
        "operations": [{
            "type": "string",
            "label": "string",
            "action": {
                "type": "string",
                "target": "string"
            }
        }]
    }
}
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "id": "string",
        "version": "integer"
    }
}
```

### 4.2 获取低代码配置

```yaml
GET /api/v1/lowcode/configs/{id}
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "id": "string",
        "queryConfigId": "string",
        "displayType": "string",
        "config": {
            "conditions": [],
            "results": [],
            "operations": []
        },
        "version": "integer"
    }
}
```

## 5. 用户配置接口

### 5.1 保存用户显示配置

```yaml
POST /api/v1/users/display-configs
请求体:
{
    "queryConfigId": "string",
    "config": {
        "hiddenConditions": ["string"],
        "columnSettings": [{
            "field": "string",
            "width": "integer",
            "visible": "boolean",
            "order": "integer"
        }]
    }
}
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "id": "string"
    }
}
```

### 5.2 获取用户收藏

```yaml
GET /api/v1/users/favorites
请求参数:
    page: integer
    size: integer
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "total": "integer",
        "items": [{
            "id": "string",
            "queryConfig": {
                "id": "string",
                "name": "string",
                "description": "string"
            }
        }]
    }
}
```

## 6. 系统管理接口

### 6.1 获取系统状态

```yaml
GET /api/v1/system/status
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "activeDataSources": "integer",
        "activeQueries": "integer",
        "cacheStatus": {
            "size": "integer",
            "hitRate": "number"
        },
        "systemResources": {
            "cpu": "number",
            "memory": "number",
            "disk": "number"
        }
    }
}
```

### 6.2 清理缓存

```yaml
POST /api/v1/system/cache/clear
请求体:
{
    "type": "string",
    "key": "string"
}
响应体:
{
    "code": "integer",
    "message": "string",
    "data": {
        "success": "boolean"
    }
}
```

## 7. 错误码定义

```yaml
成功响应:
    200: 操作成功

客户端错误:
    400: 请求参数错误
    401: 未授权
    403: 权限不足
    404: 资源不存在
    429: 请求过于频繁

服务端错误:
    500: 服务器内部错误
    503: 服务不可用
    504: 网关超时

业务错误:
    1001: 数据源连接失败
    1002: 查询超时
    1003: SQL语法错误
    1004: 数据源不存在
    1005: 表不存在
    1006: 列不存在
```