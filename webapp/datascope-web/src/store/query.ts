import { defineStore } from 'pinia';
import { queryApi } from '../api/query';
import type { QueryHistory, QueryHistoryStats } from '../types/query';

export const useQueryStore = defineStore('query', {
  state: () => ({
    queryHistories: [] as QueryHistory[],
    slowQueries: [] as QueryHistory[],
    queryStats: null as QueryHistoryStats | null,
    total: 0,
    loading: false,
    error: null as string | null,
  }),

  actions: {
    async fetchQueryHistories(pageNum: number, pageSize: number) {
      try {
        this.loading = true;
        const response = await queryApi.getUserHistory('test-user', pageNum, pageSize);
        this.queryHistories = response.data.records;
        this.total = response.data.total;
      } catch (error: any) {
        this.error = error.message;
        console.error('Failed to fetch query histories:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchSlowQueries(threshold: number, startTime: string, endTime: string) {
      try {
        this.loading = true;
        const response = await queryApi.getSlowQueries(threshold, startTime, endTime);
        this.slowQueries = response.data;
      } catch (error: any) {
        this.error = error.message;
        console.error('Failed to fetch slow queries:', error);
      } finally {
        this.loading = false;
      }
    },

    async fetchQueryStats(startTime: string, endTime: string) {
      try {
        this.loading = true;
        const response = await queryApi.getExecutionStats(startTime, endTime);
        this.queryStats = response.data;
      } catch (error: any) {
        this.error = error.message;
        console.error('Failed to fetch query stats:', error);
      } finally {
        this.loading = false;
      }
    },
  },
});