import request from '@/utils/request';
import type { QueryHistory, QueryHistoryQuery, QueryHistoryResponse, QueryStats } from '@/types/query-history';

export function listQueryHistories(params: QueryHistoryQuery) {
  return request.get<QueryHistoryResponse>('/query-histories', { params });
}

export function getQueryHistory(id: string) {
  return request.get<QueryHistory>(`/query-histories/${id}`);
}

export function getQueryStats(dataSourceId?: string, startTime?: string, endTime?: string) {
  return request.get<QueryStats>('/query-histories/stats', {
    params: { dataSourceId, startTime, endTime }
  });
}

export function getSlowQueries(threshold: number, limit: number = 10) {
  return request.get<QueryHistory[]>('/query-histories/slow-queries', {
    params: { threshold, limit }
  });
}