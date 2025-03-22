import { defineStore } from 'pinia';
import type {
  DataSource,
  CreateDataSourceParams,
  UpdateDataSourceParams,
  DataSourceQueryParams,
  TestConnectionResult,
} from '@/types/datasource';
import request from '@/utils/request';
import type { PaginationResult } from '@/types/api';

interface DataSourceState {
  list: DataSource[];
  total: number;
  current: number;
  pageSize: number;
  loading: boolean;
  currentDataSource: DataSource | null;
}

export const useDataSourceStore = defineStore('datasource', {
  state: (): DataSourceState => ({
    list: [],
    total: 0,
    current: 1,
    pageSize: 10,
    loading: false,
    currentDataSource: null,
  }),

  getters: {
    /**
     * 获取数据源Map
     */
    dataSourceMap(): Map<string, DataSource> {
      return new Map(this.list.map(item => [item.id, item]));
    },
  },

  actions: {
    /**
     * 设置当前数据源
     */
    setCurrentDataSource(datasource: DataSource | null) {
      this.currentDataSource = datasource;
    },

    /**
     * 获取数据源列表
     */
    async fetchList(params: DataSourceQueryParams = {}) {
      this.loading = true;
      try {
        const { pageSize = this.pageSize, current = this.current } = params;
        const res = await request.get<PaginationResult<DataSource>>('/datasource/list', {
          params: { ...params, pageSize, current },
        });
        this.list = res.list;
        this.total = res.total;
        this.current = res.current;
        this.pageSize = res.pageSize;
      } finally {
        this.loading = false;
      }
    },

    /**
     * 创建数据源
     */
    async create(params: CreateDataSourceParams) {
      const res = await request.post<DataSource>('/datasource/create', params);
      await this.fetchList({ current: 1 });
      return res;
    },

    /**
     * 更新数据源
     */
    async update(params: UpdateDataSourceParams) {
      const res = await request.put<DataSource>('/datasource/update', params);
      const index = this.list.findIndex(item => item.id === params.id);
      if (index > -1) {
        this.list[index] = { ...this.list[index], ...res };
      }
      return res;
    },

    /**
     * 删除数据源
     */
    async delete(id: string) {
      await request.delete(`/datasource/${id}`);
      await this.fetchList({ current: 1 });
    },

    /**
     * 测试连接
     */
    async testConnection(params: CreateDataSourceParams): Promise<TestConnectionResult> {
      return await request.post('/datasource/test', params);
    },

    /**
     * 同步元数据
     */
    async syncMetadata(id: string) {
      return await request.post(`/datasource/${id}/sync`);
    },

    /**
     * 获取数据源详情
     */
    async fetchDetail(id: string) {
      const res = await request.get<DataSource>(`/datasource/${id}`);
      this.setCurrentDataSource(res);
      return res;
    },

    /**
     * 重置状态
     */
    resetState() {
      this.list = [];
      this.total = 0;
      this.current = 1;
      this.pageSize = 10;
      this.loading = false;
      this.currentDataSource = null;
    },
  },
});