import { Pagination, PaginationResult } from './common';

// 查询条件配置
export interface QueryCondition {
  field: string;
  label?: string;
  type: 'input' | 'select' | 'date' | 'datetime' | 'number';
  required?: boolean;
  defaultValue?: any;
  hidden?: boolean;
  order?: number;
  options?: Array<{
    label: string;
    value: any;
  }>;
}

// 查询结果列配置
export interface QueryColumn {
  field: string;
  label?: string;
  type: 'text' | 'number' | 'date' | 'datetime' | 'boolean';
  width?: number;
  fixed?: 'left' | 'right';
  sortable?: boolean;
  hidden?: boolean;
  format?: string;
  mask?: boolean;
}

// 查询排序配置
export interface QuerySort {
  field: string;
  order: 'ascend' | 'descend';
}

// 查询分页配置
export interface QueryPagination {
  enabled: boolean;
  pageSize: number;
}

// 查询配置
export interface QueryConfig {
  conditions: QueryCondition[];
  columns: QueryColumn[];
  sorts: QuerySort[];
  pagination: QueryPagination;
}

// 查询详情
export interface QueryDetail {
  id: string;
  name: string;
  description?: string;
  dataSourceId: string;
  dataSourceName?: string;
  sql: string;
  config: QueryConfig;
  status: 'draft' | 'published' | 'archived';
  creator: string;
  createdAt: string;
  updatedAt: string;
}

// 查询列表项
export interface QueryListItem {
  id: string;
  name: string;
  description?: string;
  dataSourceId: string;
  dataSourceName?: string;
  status: 'draft' | 'published' | 'archived';
  creator: string;
  createdAt: string;
  updatedAt: string;
}

// 创建查询参数
export interface CreateQueryParams {
  name: string;
  description?: string;
  dataSourceId: string;
  sql: string;
  config: QueryConfig;
}

// 更新查询参数
export interface UpdateQueryParams extends CreateQueryParams {
  id: string;
}

// 查询列表查询参数
export interface QueryListParams extends Pagination {
  keyword?: string;
  status?: 'draft' | 'published' | 'archived';
  dataSourceId?: string;
}

// 查询列表响应
export interface QueryListResponse extends PaginationResult {
  list: QueryListItem[];
}

// 查询执行参数
export interface QueryExecuteParams {
  id: string;
  conditions?: Record<string, any>;
  page?: number;
  pageSize?: number;
}

// 查询执行结果
export interface QueryExecutionResult {
  executionTime: number;
  affectedRows: number;
  total?: number;
  data: any[];
}

// 查询历史记录
export interface QueryHistory {
  id: string;
  queryId: string;
  queryName: string;
  executor: string;
  status: 'running' | 'completed' | 'failed' | 'cancelled';
  startTime: string;
  endTime?: string;
  duration?: number;
  affectedRows?: number;
  error?: string;
  conditions?: Record<string, any>;
}

// 查询历史列表参数
export interface QueryHistoryParams extends Pagination {
  queryId?: string;
  status?: 'running' | 'completed' | 'failed' | 'cancelled';
  startTime?: string;
  endTime?: string;
}

// 查询历史列表响应
export interface QueryHistoryResponse extends PaginationResult {
  list: QueryHistory[];
}