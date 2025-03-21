import { defineStore } from 'pinia';
import axios from 'axios';

export interface DataSource {
  id: string;
  name: string;
  type: string;
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  createdAt: string;
  updatedAt: string;
}

export interface DataSourceResponse {
  items: DataSource[];
  total: number;
}

export const useDatasourceStore = defineStore('datasource', {
  state: () => ({
    datasources: [] as DataSource[],
    total: 0,
  }),

  actions: {
    async fetchDatasources(page: number, size: number): Promise<DataSourceResponse> {
      try {
        const response = await axios.get<DataSourceResponse>(`/api/datasources?page=${page}&size=${size}`);
        this.datasources = response.data.items;
        this.total = response.data.total;
        return response.data;
      } catch (error) {
        console.error('Failed to fetch datasources:', error);
        throw error;
      }
    },

    async createDatasource(datasource: Omit<DataSource, 'id' | 'createdAt' | 'updatedAt'>): Promise<DataSource> {
      try {
        const response = await axios.post<DataSource>('/api/datasources', datasource);
        return response.data;
      } catch (error) {
        console.error('Failed to create datasource:', error);
        throw error;
      }
    },

    async updateDatasource(datasource: Partial<DataSource> & { id: string }): Promise<DataSource> {
      try {
        const response = await axios.put<DataSource>(`/api/datasources/${datasource.id}`, datasource);
        return response.data;
      } catch (error) {
        console.error('Failed to update datasource:', error);
        throw error;
      }
    },

    async deleteDatasource(id: string): Promise<void> {
      try {
        await axios.delete(`/api/datasources/${id}`);
      } catch (error) {
        console.error('Failed to delete datasource:', error);
        throw error;
      }
    },

    async testConnection(id: string): Promise<void> {
      try {
        await axios.post(`/api/datasources/${id}/test`);
      } catch (error) {
        console.error('Failed to test connection:', error);
        throw error;
      }
    },
  },
});