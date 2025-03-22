import { defineStore } from 'pinia';
import {
  getQueryList,
  getQueryDetail,
  createQuery,
  updateQuery,
  deleteQuery,
  publishQuery,
  archiveQuery,
  executeQuery,
  cancelExecution,
  getQueryHistory,
  exportQueryResult,
} from '@/api/query';
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

export const useQueryStore = defineStore('query', {
  state: () => ({
    // 查询列表数据
    list: [] as QueryListResponse['list'],
    // 总数
    total: 0,
    // 查询详情
    detail: null as QueryDetail | null,
    // 查询历史记录
    history: [] as QueryHistoryResponse['list'],
    // 历史记录总数
    historyTotal: 0,
  }),

  actions: {
    /**
     * 获取查询列表
     */
    async fetchList(params: QueryListParams) {
      const { list, total } = await getQueryList(params);
      this.list = list;
      this.total = total;
      return { list, total };
    },

    /**
     * 获取查询详情
     */
    async fetchDetail(id: string) {
      const detail = await getQueryDetail(id);
      this.detail = detail;
      return detail;
    },

    /**
     * 创建查询
     */
    async create(params: CreateQueryParams) {
      const detail = await createQuery(params);
      return detail;
    },

    /**
     * 更新查询
     */
    async update(params: UpdateQueryParams) {
      const detail = await updateQuery(params.id, params);
      return detail;
    },

    /**
     * 删除查询
     */
    async delete(id: string) {
      await deleteQuery(id);
      // 从列表中移除
      this.list = this.list.filter(item => item.id !== id);
      this.total--;
    },

    /**
     * 发布查询
     */
    async publish(id: string) {
      await publishQuery(id);
      // 更新列表项状态
      const item = this.list.find(item => item.id === id);
      if (item) {
        item.status = 'published';
      }
      // 更新详情状态
      if (this.detail?.id === id) {
        this.detail.status = 'published';
      }
    },

    /**
     * 归档查询
     */
    async archive(id: string) {
      await archiveQuery(id);
      // 更新列表项状态
      const item = this.list.find(item => item.id === id);
      if (item) {
        item.status = 'archived';
      }
      // 更新详情状态
      if (this.detail?.id === id) {
        this.detail.status = 'archived';
      }
    },

    /**
     * 执行查询
     */
    async execute(params: QueryExecuteParams) {
      const result = await executeQuery(params);
      return result;
    },

    /**
     * 取消查询执行
     */
    async cancel(id: string, executionId: string) {
      await cancelExecution(id, executionId);
    },

    /**
     * 获取查询历史记录
     */
    async fetchHistory(params: QueryHistoryParams) {
      const { list, total } = await getQueryHistory(params);
      this.history = list;
      this.historyTotal = total;
      return { list, total };
    },

    /**
     * 导出查询结果
     */
    async export(id: string, params: QueryExecuteParams) {
      const blob = await exportQueryResult(id, params);
      // 创建下载链接
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `query-result-${id}.xlsx`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    },
  },
});