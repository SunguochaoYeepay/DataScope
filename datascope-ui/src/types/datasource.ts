// 数据源类型定义
export interface Datasource {
  id: number
  name: string
  type: DatabaseType
  host: string
  port: number
  database: string
  username: string
  password: string
  status: ConnectionStatus
  createdAt: string
  updatedAt: string
  description?: string
  testing?: boolean
}

// 数据库类型
export enum DatabaseType {
  MySQL = 'MySQL',
  PostgreSQL = 'PostgreSQL',
  Oracle = 'Oracle',
  SQLServer = 'SQLServer'
}

// 连接状态
export enum ConnectionStatus {
  Active = 'active',
  Error = 'error',
  Testing = 'testing'
}

// 数据源表单数据
export interface DatasourceFormData {
  name: string
  type: DatabaseType
  host: string
  port: number | string
  database: string
  username: string
  password: string
  description?: string
}

// API 响应类型
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 分页响应
export interface PaginatedResponse<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
}