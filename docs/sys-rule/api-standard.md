# API 设计规范

## 1. 基本原则

### 1.1 URL规范
- 使用 RESTful 风格
- 使用小写字母，单词间用连字符(-)分隔
- 使用复数形式表示资源集合
- 格式：`/api/v1/{resource}/{id}/{sub-resource}`

```bash
# 正确示例
GET    /api/v1/datasources           # 获取数据源列表
GET    /api/v1/datasources/123       # 获取单个数据源
POST   /api/v1/datasources           # 创建数据源
PUT    /api/v1/datasources/123       # 更新数据源
DELETE /api/v1/datasources/123       # 删除数据源

# 错误示例
GET    /api/v1/getDatasources        # 不要在URL中使用动词
GET    /api/v1/datasource/getById/1  # 不要在URL中使用动词
```

### 1.2 HTTP方法使用
| 方法   | 用途     | 是否幂等 | 响应码 |
|--------|---------|----------|--------|
| GET    | 查询资源 | 是      | 200    |
| POST   | 创建资源 | 否      | 201    |
| PUT    | 全量更新 | 是      | 200    |
| PATCH  | 部分更新 | 否      | 200    |
| DELETE | 删除资源 | 是      | 204    |

## 2. 请求规范

### 2.1 请求参数
```typescript
// 查询参数
interface QueryParams {
    // 分页
    current?: number;    // 当前页码，从1开始
    size?: number;       // 每页大小
    
    // 排序
    sort?: string;       // 排序字段
    order?: 'asc' | 'desc';  // 排序方向
    
    // 过滤
    keyword?: string;    // 搜索关键词
    status?: string;     // 状态过滤
}

// 创建/更新请求体
interface RequestBody {
    name: string;        // 必填字段不带问号
    type?: string;       // 可选字段带问号
    config?: object;     // 复杂对象
}
```

### 2.2 请求头
```http
Content-Type: application/json
Authorization: Bearer <token>
X-Request-ID: <uuid>
```

## 3. 响应规范

### 3.1 响应格式
```typescript
interface ApiResponse<T> {
    code: number;        // 状态码
    message: string;     // 响应消息
    data?: T;           // 响应数据
    requestId: string;   // 请求ID
    timestamp: string;   // 响应时间
}

interface PaginationResponse<T> {
    records: T[];       // 数据列表
    total: number;      // 总记录数
    size: number;       // 每页大小
    current: number;    // 当前页码
}
```

### 3.2 响应示例

#### 成功响应
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "name": "示例数据源",
        "type": "mysql",
        "createdAt": "2024-03-20T10:00:00Z"
    },
    "requestId": "550e8400-e29b-41d4-a716-446655440000",
    "timestamp": "2024-03-20T10:00:00Z"
}
```

#### 错误响应
```json
{
    "code": 400,
    "message": "请求参数错误",
    "data": {
        "fields": {
            "name": ["名称不能为空"]
        }
    },
    "requestId": "550e8400-e29b-41d4-a716-446655440001",
    "timestamp": "2024-03-20T10:00:00Z"
}
```

## 4. 安全规范

### 4.1 认证与授权
- 使用JWT进行身份认证
- 敏感操作需要验证权限
- 使用HTTPS传输

### 4.2 数据验证
- 验证所有输入参数
- 防止SQL注入
- 防止XSS攻击

## 5. 最佳实践

### 5.1 性能优化
- 合理使用缓存
- 分页获取大量数据
- 避免N+1查询问题
- 使用批量接口

### 5.2 版本控制
- 在URL中使用版本号
- 保持向后兼容
- 记录API变更

### 5.3 文档维护
- 使用Swagger/OpenAPI
- 及时更新文档
- 提供详细示例