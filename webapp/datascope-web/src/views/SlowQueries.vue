<template>
  <div class="slow-queries">
    <a-card title="慢查询分析" :bordered="false">
      <template #extra>
        <a-space>
          <a-input-number
            v-model:value="threshold"
            :min="1000"
            :step="1000"
            addon-after="ms"
            style="width: 200px"
            placeholder="执行时间阈值"
          />
          <a-range-picker
            v-model:value="dateRange"
            :show-time="{ format: 'HH:mm:ss' }"
            format="YYYY-MM-DD HH:mm:ss"
            :placeholder="['开始时间', '结束时间']"
          />
          <a-button type="primary" @click="fetchData">查询</a-button>
        </a-space>
      </template>

      <a-table
        :columns="columns"
        :data-source="queryStore.slowQueries"
        :pagination="false"
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
import { ref } from 'vue';
import { useQueryStore } from '../store/query';
import { ReloadOutlined } from '@ant-design/icons-vue';
import ErrorMessage from '../components/ErrorMessage.vue';
import LoadingSpinner from '../components/LoadingSpinner.vue';
import dayjs from 'dayjs';
import type { Dayjs } from 'dayjs';
import type { QueryHistory } from '../types/query';

const queryStore = useQueryStore();
const threshold = ref(3000);
const dateRange = ref<[Dayjs, Dayjs]>([
  dayjs().startOf('day'),
  dayjs().endOf('day')
]);
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

const fetchData = () => {
  if (!dateRange.value) return;
  const [start, end] = dateRange.value;
  queryStore.fetchSlowQueries(
    threshold.value,
    start.format('YYYY-MM-DD HH:mm:ss'),
    end.format('YYYY-MM-DD HH:mm:ss')
  );
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

// 初始化数据
fetchData();
</script>

<style scoped>
.slow-queries {
  padding: 24px;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}
</style>