import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { QueryHistory, QueryHistoryQuery, QueryStats } from '@/types/query-history';
import * as queryHistoryApi from '@/api/query-history';
import { message } from 'ant-design-vue';

export const useQueryHistoryStore = defineStore('queryHistory', () => {
  const loading = ref(false);
  const histories = ref<QueryHistory[]>([]);
  const total = ref(0);
  const stats = ref<QueryStats | null>(null);
  const slowQueries = ref<QueryHistory[]>([]);

  async function fetchHistories(params: QueryHistoryQuery) {
    try {
      loading.value = true;
      const res = await queryHistoryApi.listQueryHistories(params);
      histories.value = res.content;
      total.value = res.totalElements;
    } catch (error) {
      console.error('Failed to fetch query histories:', error);
      message.error('获取查询历史失败');
    } finally {
      loading.value = false;
    }
  }

  async function fetchStats(dataSourceId?: string, startTime?: string, endTime?: string) {
    try {
      loading.value = true;
      const res = await queryHistoryApi.getQueryStats(dataSourceId, startTime, endTime);
      stats.value = res;
    } catch (error) {
      console.error('Failed to fetch query stats:', error);
      message.error('获取查询统计失败');
    } finally {
      loading.value = false;
    }
  }

  async function fetchSlowQueries(threshold: number, limit: number = 10) {
    try {
      loading.value = true;
      const res = await queryHistoryApi.getSlowQueries(threshold, limit);
      slowQueries.value = res;
    } catch (error) {
      console.error('Failed to fetch slow queries:', error);
      message.error('获取慢查询列表失败');
    } finally {
      loading.value = false;
    }
  }

  return {
    loading,
    histories,
    total,
    stats,
    slowQueries,
    fetchHistories,
    fetchStats,
    fetchSlowQueries,
  };
});