# 错误码规范

## 1. 错误码设计

### 1.1 错误码格式
- 3位数字编码
- 遵循HTTP状态码规范
- 分类明确，易于理解

### 1.2 错误码分类
| 范围 | 类型 | 说明 |
|-----|------|-----|
| 2xx | 成功 | 请求成功处理 |
| 4xx | 客户端错误 | 请求参数错误等 |
| 5xx | 服务器错误 | 服务器内部错误 |

## 2. 标准错误码

### 2.1 成功响应
```python
class SuccessCode:
    SUCCESS = 200        # 操作成功
    CREATED = 201       # 创建成功
    ACCEPTED = 202      # 请求已接受
    NO_CONTENT = 204    # 删除成功
```

### 2.2 客户端错误
```python
class ClientError:
    # 通用错误 (40x)
    BAD_REQUEST = 400    # 请求参数错误
    UNAUTHORIZED = 401   # 未认证
    FORBIDDEN = 403      # 未授权
    NOT_FOUND = 404      # 资源不存在
    CONFLICT = 409       # 资源冲突
    
    # 业务错误 (42x)
    INVALID_PARAMS = 420     # 参数验证失败
    RESOURCE_EXISTS = 421    # 资源已存在
    RESOURCE_IN_USE = 422    # 资源使用中
    STATUS_ERROR = 423       # 状态错误
    OPERATION_FAILED = 424   # 操作失败
```

### 2.3 服务器错误
```python
class ServerError:
    SERVER_ERROR = 500      # 服务器错误
    NOT_IMPLEMENTED = 501   # 未实现
    BAD_GATEWAY = 502      # 网关错误
    UNAVAILABLE = 503      # 服务不可用
    TIMEOUT = 504          # 超时
```

## 3. 错误响应格式

### 3.1 基本格式
```typescript
interface ErrorResponse {
    code: number;        // 错误码
    message: string;     // 错误消息
    data?: {
        field?: string;  // 错误字段
        value?: any;     // 错误值
        reason?: string; // 错误原因
    };
    requestId: string;   // 请求ID
    timestamp: string;   // 响应时间
}
```

### 3.2 错误响应示例

#### 参数验证错误
```json
{
    "code": 400,
    "message": "请求参数错误",
    "data": {
        "fields": {
            "name": ["名称不能为空"]
        }
    },
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2024-03-20T10:00:00Z"
}
```

#### 认证错误
```json
{
    "code": 401,
    "message": "未认证或认证已过期",
    "data": {
        "reason": "Token已过期"
    },
    "requestId": "550e8400-e29b-41d4-a716-446655440001",
    "timestamp": "2024-03-20T10:00:00Z"
}
```

## 4. 错误处理

### 4.1 后端处理
```python
def handle_error(e: Exception) -> ErrorResponse:
    if isinstance(e, ValidationError):
        return ErrorResponse(
            code=400,
            message="请求参数错误",
            data={"fields": e.errors()}
        )
    elif isinstance(e, AuthenticationError):
        return ErrorResponse(
            code=401,
            message="未认证或认证已过期"
        )
    else:
        return ErrorResponse(
            code=500,
            message="服务器内部错误"
        )
```

### 4.2 前端处理
```typescript
// 错误处理器
class ErrorHandler {
    static handle(error: ErrorResponse) {
        switch (error.code) {
            case 401:
                // 跳转到登录页
                router.push('/login');
                break;
            case 403:
                notification.error({
                    message: "权限错误",
                    description: error.message
                });
                break;
            default:
                notification.error({
                    message: "操作失败",
                    description: error.message
                });
        }
    }
}
```

## 5. 最佳实践

### 5.1 错误码使用
- 使用合适的错误码
- 提供清晰的错误消息
- 包含必要的错误详情
- 不暴露敏感信息

### 5.2 错误日志
- 记录详细的错误信息
- 包含错误堆栈
- 记录请求上下文
- 便于问题定位

### 5.3 错误监控
- 监控错误发生频率
- 设置告警阈值
- 定期分析错误趋势
- 及时处理异常情况