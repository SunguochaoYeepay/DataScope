# DataScope API文档规范

## 1. OpenAPI规范

### 1.1 基础信息配置
```yaml
openapi: 3.0.3
info:
  title: DataScope API
  description: DataScope数据管理与查询系统API文档
  version: 1.0.0
  contact:
    name: DataScope Team
    email: team@datascope.com
  license:
    name: Private
    
servers:
  - url: https://api.datascope.com/v1
    description: 生产环境
  - url: https://api-staging.datascope.com/v1
    description: 预发布环境
  - url: http://localhost:8000/v1
    description: 本地开发环境
```

### 1.2 安全配置
```yaml
components:
  securitySchemes:
    BearerAuth:
      type: http
      scheme: bearer
      bearerFormat: JWT
    ApiKeyAuth:
      type: apiKey
      in: header
      name: X-API-Key
      
security:
  - BearerAuth: []
  - ApiKeyAuth: []
```

## 2. API端点规范

### 2.1 数据源管理API
```yaml
paths:
  /datasources:
    get:
      summary: 获取数据源列表
      description: 获取所有可访问的数据源列表
      parameters:
        - name: page
          in: query
          description: 页码
          schema:
            type: integer
            default: 1
        - name: size
          in: query
          description: 每页大小
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: 成功返回数据源列表
          content:
            application/json:
              schema:
                type: object
                properties:
                  total:
                    type: integer
                    description: 总记录数
                  data:
                    type: array
                    items:
                      $ref: '#/components/schemas/DataSource'
        '401':
          $ref: '#/components/responses/Unauthorized'
        '403':
          $ref: '#/components/responses/Forbidden'
```

### 2.2 查询管理API
```yaml
paths:
  /queries/execute:
    post:
      summary: 执行SQL查询
      description: 在指定数据源上执行SQL查询
      requestBody:
        required: true
        content:
          application/json:
            schema:
              type: object
              required:
                - dataSourceId
                - sql
              properties:
                dataSourceId:
                  type: string
                  description: 数据源ID
                sql:
                  type: string
                  description: SQL查询语句
                parameters:
                  type: object
                  description: 查询参数
      responses:
        '200':
          description: 查询执行成功
          content:
            application/json:
              schema:
                type: object
                properties:
                  columns:
                    type: array
                    items:
                      $ref: '#/components/schemas/Column'
                  data:
                    type: array
                    items:
                      type: object
                  total:
                    type: integer
        '400':
          $ref: '#/components/responses/BadRequest'
```

## 3. 数据模型定义

### 3.1 基础模型
```yaml
components:
  schemas:
    DataSource:
      type: object
      required:
        - id
        - name
        - type
      properties:
        id:
          type: string
          format: uuid
          description: 数据源唯一标识
        name:
          type: string
          description: 数据源名称
        type:
          type: string
          enum: [MySQL, DB2]
          description: 数据源类型
        host:
          type: string
          description: 主机地址
        port:
          type: integer
          description: 端口号
        database:
          type: string
          description: 数据库名称
        username:
          type: string
          description: 用户名
        status:
          type: string
          enum: [ACTIVE, INACTIVE, ERROR]
          description: 数据源状态
```

### 3.2 响应模型
```yaml
components:
  responses:
    BadRequest:
      description: 请求参数错误
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
    Unauthorized:
      description: 未授权访问
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
    Forbidden:
      description: 禁止访问
      content:
        application/json:
          schema:
            $ref: '#/components/schemas/Error'
            
  schemas:
    Error:
      type: object
      required:
        - code
        - message
      properties:
        code:
          type: integer
          description: 错误码
        message:
          type: string
          description: 错误信息
        details:
          type: object
          description: 详细错误信息
```

## 4. API文档示例

### 4.1 接口描述示例
```java
/**
 * 创建数据源
 * 
 * @api {post} /datasources 创建数据源
 * @apiName CreateDataSource
 * @apiGroup DataSource
 * @apiVersion 1.0.0
 * 
 * @apiParam {String} name 数据源名称
 * @apiParam {String} type 数据源类型(MySQL/DB2)
 * @apiParam {String} host 主机地址
 * @apiParam {Number} port 端口号
 * @apiParam {String} database 数据库名称
 * @apiParam {String} username 用户名
 * @apiParam {String} password 密码
 * 
 * @apiSuccess {String} id 数据源ID
 * @apiSuccess {String} name 数据源名称
 * @apiSuccess {String} type 数据源类型
 * @apiSuccess {String} status 数据源状态
 * 
 * @apiError (400) BadRequest 请求参数错误
 * @apiError (401) Unauthorized 未授权访问
 * @apiError (403) Forbidden 禁止访问
 */
@PostMapping("/datasources")
public ResponseEntity<DataSourceDTO> createDataSource(@RequestBody @Valid CreateDataSourceRequest request) {
    // Implementation
}
```

### 4.2 请求示例
```json
{
  "name": "Production MySQL",
  "type": "MySQL",
  "host": "mysql.production.com",
  "port": 3306,
  "database": "business_db",
  "username": "reader",
  "password": "encrypted_password"
}
```

### 4.3 响应示例
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Production MySQL",
  "type": "MySQL",
  "host": "mysql.production.com",
  "port": 3306,
  "database": "business_db",
  "username": "reader",
  "status": "ACTIVE",
  "createTime": "2024-03-20T10:00:00Z",
  "updateTime": "2024-03-20T10:00:00Z"
}
```

## 5. API版本控制

### 5.1 版本号规范
```yaml
version_control:
  format: "v{major}.{minor}"
  rules:
    major_change:
      - 不兼容的API变更
      - 删除或重命名字段
      - 修改字段类型
    minor_change:
      - 向后兼容的功能新增
      - 新增可选字段
      - 新增接口参数
```

### 5.2 版本迁移指南
```yaml
version_migration:
  v1_to_v2:
    breaking_changes:
      - description: "修改数据源认证方式"
        old_format:
          username: string
          password: string
        new_format:
          auth:
            type: string
            credentials: object
    migration_steps:
      - "更新认证信息格式"
      - "重新生成访问令牌"
      - "更新客户端代码"
```

## 6. API变更管理

### 6.1 变更日志格式
```markdown
# API变更日志

## [2.0.0] - 2024-03-20
### 新增
- 添加数据源标签管理接口
- 支持查询结果导出功能

### 变更
- 数据源认证方式改为OAuth2
- 查询接口支持异步执行

### 废弃
- 基于用户名密码的认证方式
- 同步查询执行接口

### 修复
- 修复大数据量查询超时问题
- 修复数据源连接池泄漏问题
```

### 6.2 废弃声明示例
```java
/**
 * @deprecated 从v2.0.0开始废弃，请使用{@link #executeQueryAsync}替代
 * 将在v3.0.0中移除
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public QueryResult executeQuery(String sql) {
    // Implementation
}
```

## 7. API文档工具配置

### 7.1 Swagger配置
```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI dataSourceAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DataScope API")
                .version("1.0.0")
                .description("DataScope数据管理与查询系统API文档"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .components(new Components()
                .addSecuritySchemes("Bearer", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

### 7.2 文档生成配置
```yaml
springdoc:
  api-docs:
    path: /api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui
    enabled: true
    doc-expansion: none
    tags-sorter: alpha
    operations-sorter: method
  packages-to-scan: com.datascope.api
  paths-to-match: /api/**
```

## 8. API文档维护指南

### 8.1 文档更新流程
```yaml
documentation_process:
  triggers:
    - API接口变更
    - 参数变更
    - 响应格式变更
    - 业务逻辑变更
    
  steps:
    - 更新OpenAPI规范文件
    - 更新接口注释
    - 更新示例代码
    - 更新变更日志
    - 评审文档变更
    - 发布文档更新
```

### 8.2 文档质量检查
```yaml
quality_checks:
  automated:
    - OpenAPI规范验证
    - 文档完整性检查
    - 示例代码验证
    - 链接有效性检查
    
  manual:
    - 文档可读性审查
    - 示例的准确性验证
    - 错误码完整性检查
    - 接口描述准确性验证
```

### 8.3 文档评审清单
```yaml
review_checklist:
  content:
    - 接口描述是否清晰
    - 参数说明是否完整
    - 响应示例是否准确
    - 错误码是否覆盖全面
    
  format:
    - Markdown格式是否规范
    - 代码示例是否格式化
    - 表格是否对齐
    - 链接是否有效
```