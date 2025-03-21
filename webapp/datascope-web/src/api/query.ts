import axios from 'axios';
import type { ApiResponse, QueryHistory, PageResponse, QueryHistoryStats } from '../types/query';

const BASE_URL = 'http://localhost:8080';

export const queryApi = {
  // 获取用户查询历史
  getUserHistory: async (userId: string, pageNum: number, pageSize: number) => {
    const response = await axios.get<ApiResponse<PageResponse<QueryHistory>>>(
      `${BASE_URL}/api/v1/query-histories/user/${userId}`,
      {
        params: { pageNum, pageSize }
      }
    );
    return response.data;
  },

  // 获取查询历史详情
  getHistoryById: async (id: string) => {
    const response = await axios.get<ApiResponse<QueryHistory>>(
      `${BASE_URL}/api/v1/query-histories/${id}`
    );
    return response.data;
  },

  // 获取执行统计信息
  getExecutionStats: async (startTime: string, endTime: string) => {
    const response = await axios.get<ApiResponse<QueryHistoryStats>>(
      `${BASE_URL}/api/v1/query-histories/stats`,
      {
        params: { startTime, endTime }
      }
    );
    return response.data;
  },

  // 获取最近失败的查询
  getRecentFailures: async (limit: number = 10) => {
    const response = await axios.get<ApiResponse<QueryHistory[]>>(
      `${BASE_URL}/api/v1/query-histories/failures`,
      {
        params: { limit }
      }
    );
    return response.data;
  },

  // 获取慢查询记录
  getSlowQueries: async (thresholdMillis: number, startTime: string, endTime: string, limit: number = 10) => {
    const response = await axios.get<ApiResponse<QueryHistory[]>>(
      `${BASE_URL}/api/v1/query-histories/slow-queries`,
      {
        params: { thresholdMillis, startTime, endTime, limit }
      }
    );
    return response.data;
  }
};