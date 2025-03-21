import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { SQLParseRequest, SQLParseResult, TableInfo } from '@/types/sql-parser';
import * as sqlParserApi from '@/api/sql-parser';
import { message } from 'ant-design-vue';

export const useSqlParserStore = defineStore('sqlParser', () => {
  const loading = ref(false);
  const parseResult = ref<SQLParseResult | null>(null);
  const tables = ref<TableInfo[]>([]);
  const currentTable = ref<TableInfo | null>(null);

  async function parseSql(data: SQLParseRequest) {
    try {
      loading.value = true;
      const res = await sqlParserApi.parseSql(data);
      parseResult.value = res;
      return res;
    } catch (error) {
      console.error('Failed to parse SQL:', error);
      message.error('SQL解析失败');
      return null;
    } finally {
      loading.value = false;
    }
  }

  async function fetchTables(dataSourceId: string) {
    try {
      loading.value = true;
      const res = await sqlParserApi.getTables(dataSourceId);
      tables.value = res;
    } catch (error) {
      console.error('Failed to fetch tables:', error);
      message.error('获取表列表失败');
    } finally {
      loading.value = false;
    }
  }

  async function fetchTableInfo(dataSourceId: string, tableName: string) {
    try {
      loading.value = true;
      const res = await sqlParserApi.getTableInfo(dataSourceId, tableName);
      currentTable.value = res;
      return res;
    } catch (error) {
      console.error('Failed to fetch table info:', error);
      message.error('获取表信息失败');
      return null;
    } finally {
      loading.value = false;
    }
  }

  return {
    loading,
    parseResult,
    tables,
    currentTable,
    parseSql,
    fetchTables,
    fetchTableInfo,
  };
});