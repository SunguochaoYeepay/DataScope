import request from '@/utils/request';
import type {
  QueryListParams,
  QueryListResponse,
  QueryDetail,
  CreateQueryParams,
  UpdateQueryParams,
  QueryExecuteParams,
  QueryExecutionResult,
  QueryHistoryParams,
  QueryHistoryResponse,
} from '@/types/query';

const baseUrl = '/api/v1/queries';

/**
 * 获取查询列表
 */
export function getQueryList(params: QueryListParams) {
  return request.get<QueryListResponse>(baseUrl, { params });
}

/**
 * 获取查询详情
 */
export function getQueryDetail(id: string) {
  return request.get<QueryDetail>(`${baseUrl}/${id}`);
}

/**
 * 创建查询
 */
export function createQuery(data: CreateQueryParams) {
  return request.post<QueryDetail>(baseUrl, data);
}

/**
 * 更新查询
 */
export function updateQuery(id: string, data: UpdateQueryParams) {
  return request.put<QueryDetail>(`${baseUrl}/${id}`, data);
}

/**
 * 删除查询
 */
export function deleteQuery(id: string) {
  return request.delete(`${baseUrl}/${id}`);
}

/**
 * 发布查询
 */
export function publishQuery(id: string) {
  return request.post(`${baseUrl}/${id}/publish`);
}

/**
 * 归档查询
 */
export function archiveQuery(id: string) {
  return request.post(`${baseUrl}/${id}/archive`);
}

/**
 * 执行查询
 */
export function executeQuery(params: QueryExecuteParams) {
  return request.post<QueryExecutionResult>(`${baseUrl}/${params.id}/execute`, {
    conditions: params.conditions,
    page: params.page,
    pageSize: params.pageSize,
  });
}

/**
 * 取消查询执行
 */
export function cancelExecution(id: string, executionId: string) {
  return request.post(`${baseUrl}/${id}/executions/${executionId}/cancel`);
}

/**
 * 获取查询历史记录
 */
export function getQueryHistory(params: QueryHistoryParams) {
  return request.get<QueryHistoryResponse>(`${baseUrl}/history`, { params });
}

/**
 * 导出查询结果
 */
export function exportQueryResult(id: string, params: QueryExecuteParams) {
  return request.post(
    `${baseUrl}/${id}/export`,
    {
      conditions: params.conditions,
    },
    {
      responseType: 'blob',
    }
  );
}