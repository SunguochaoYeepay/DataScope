<template>
  <div class="query-history">
    <error-message
      :error="queryStore.error"
      @close="queryStore.error = null"
    />
    
    <a-card title="查询历史" :bordered="false">
      <template #extra>
        <a-button type="primary" @click="refresh">
          <template #icon><reload-outlined /></template>
          刷新
        </a-button>
      </template>

      <loading-spinner :loading="queryStore.loading" />
      
      <a-table
        v-show="!queryStore.loading"
        :columns="columns"
        :data-source="queryStore.queryHistories"
        :pagination="{
          total: queryStore.total,
          current: currentPage,
          pageSize: pageSize,
          onChange: handlePageChange,
          showSizeChanger: true,
          showTotal: total => `共 ${total} 条记录`
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'SUCCESS' ? 'success' : 'error'">
              {{ record.status }}
            </a-tag>
          </template>
          
          <template v-if="column.key === 'executionTime'">
            {{ record.executionTime }}ms
          </template>

          <template v-if="column.key === 'parameters'">
            <a-tooltip>
              <template #title>
                <pre>{{ formatJson(record.parameters) }}</pre>
              </template>
              <a-button type="link">查看参数</a-button>
            </a-tooltip>
          </template>

          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" @click="showDetails(record)">详情</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:visible="detailsVisible"
      title="查询详情"
      width="800px"
      :footer="null"
    >
      <a-descriptions bordered>
        <a-descriptions-item label="查询ID" span="3">
          {{ selectedRecord?.queryId }}
        </a-descriptions-item>
        <a-descriptions-item label="执行时间" span="2">
          {{ selectedRecord?.executionTime }}ms
        </a-descriptions-item>
        <a-descriptions-item label="影响行数">
          {{ selectedRecord?.affectedRows }}
        </a-descriptions-item>
        <a-descriptions-item label="执行状态" span="2">
          <a-tag :color="selectedRecord?.status === 'SUCCESS' ? 'success' : 'error'">
            {{ selectedRecord?.status }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="执行IP">
          {{ selectedRecord?.executionIp }}
        </a-descriptions-item>
        <a-descriptions-item label="创建时间" span="2">
          {{ selectedRecord?.createdAt }}
        </a-descriptions-item>
        <a-descriptions-item label="创建者">
          {{ selectedRecord?.createdBy }}
        </a-descriptions-item>
        <a-descriptions-item label="查询参数" span="3">
          <pre>{{ formatJson(selectedRecord?.parameters) }}</pre>
        </a-descriptions-item>
        <template v-if="selectedRecord?.errorMessage">
          <a-descriptions-item label="错误信息" span="3">
            {{ selectedRecord.errorMessage }}
          </a-descriptions-item>
        </template>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQueryStore } from '../store/query';
import { ReloadOutlined } from '@ant-design/icons-vue';
import ErrorMessage from '../components/ErrorMessage.vue';
import LoadingSpinner from '../components/LoadingSpinner.vue';
import type { QueryHistory } from '../types/query';

const queryStore = useQueryStore();
const currentPage = ref(1);
const pageSize = ref(10);
const detailsVisible = ref(false);
const selectedRecord = ref<QueryHistory | null>(null);

const columns = [
  {
    title: '查询ID',
    dataIndex: 'queryId',
    key: 'queryId',
    width: 200,
  },
  {
    title: '执行时间',
    dataIndex: 'executionTime',
    key: 'executionTime',
    width: 120,
    sorter: (a: QueryHistory, b: QueryHistory) => a.executionTime - b.executionTime,
  },
  {
    title: '影响行数',
    dataIndex: 'affectedRows',
    key: 'affectedRows',
    width: 100,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
  },
  {
    title: '参数',
    dataIndex: 'parameters',
    key: 'parameters',
    width: 100,
  },
  {
    title: '执行IP',
    dataIndex: 'executionIp',
    key: 'executionIp',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
    width: 100,
  }
];

const handlePageChange = (page: number, size: number) => {
  currentPage.value = page;
  pageSize.value = size;
  fetchData();
};

const fetchData = () => {
  queryStore.fetchQueryHistories(currentPage.value, pageSize.value);
};

const refresh = () => {
  currentPage.value = 1;
  fetchData();
};

const showDetails = (record: QueryHistory) => {
  selectedRecord.value = record;
  detailsVisible.value = true;
};

const formatJson = (jsonString: string | undefined) => {
  if (!jsonString) return '';
  try {
    const obj = JSON.parse(jsonString);
    return JSON.stringify(obj, null, 2);
  } catch (e) {
    return jsonString;
  }
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.query-history {
  padding: 24px;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>