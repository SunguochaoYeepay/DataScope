<template>
  <page-container title="查询历史" subtitle="查看和分析SQL查询历史">
    <a-row :gutter="[16, 16]">
      <a-col :span="24">
        <a-card>
          <a-form layout="inline" :model="formState">
            <a-form-item label="数据源">
              <a-select
                v-model:value="formState.dataSourceId"
                style="width: 200px"
                placeholder="全部"
                allowClear
              >
                <a-select-option
                  v-for="ds in datasourceStore.datasources"
                  :key="ds.id"
                  :value="ds.id"
                >
                  {{ ds.name }}
                </a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="时间范围">
              <a-range-picker
                v-model:value="formState.timeRange"
                :show-time="{ format: 'HH:mm' }"
                format="YYYY-MM-DD HH:mm"
                :placeholder="['开始时间', '结束时间']"
              />
            </a-form-item>

            <a-form-item>
              <a-button type="primary" @click="handleSearch">查询</a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>

      <a-col :span="16">
        <pro-table
          title="查询历史"
          :columns="columns"
          :data-source="queryHistoryStore.histories"
          :loading="queryHistoryStore.loading"
          :pagination="{
            total: queryHistoryStore.total,
            current: currentPage,
            pageSize: pageSize,
            showSizeChanger: true,
            showQuickJumper: true,
          }"
          @change="handleTableChange"
          @refresh="refresh"
        >
          <template #column-status="{ record }">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>

          <template #column-executionTime="{ record }">
            {{ record.executionTime }}ms
          </template>

          <template #column-action="{ record }">
            <a-button type="link" @click="showDetail(record)">详情</a-button>
          </template>
        </pro-table>
      </a-col>

      <a-col :span="8">
        <a-card title="查询统计">
          <a-statistic
            title="总查询次数"
            :value="queryHistoryStore.stats?.totalQueries || 0"
            style="margin-bottom: 16px"
          />

          <a-statistic
            title="平均执行时间"
            :value="queryHistoryStore.stats?.avgExecutionTime || 0"
            :precision="2"
            suffix="ms"
            style="margin-bottom: 16px"
          />

          <a-divider />

          <div class="slow-queries">
            <h4>慢查询 TOP 5</h4>
            <a-list
              :data-source="queryHistoryStore.slowQueries.slice(0, 5)"
              size="small"
            >
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #title>
                      {{ item.sql.substring(0, 50) }}...
                    </template>
                    <template #description>
                      执行时间: {{ item.executionTime }}ms
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <a-modal
      v-model:visible="detailVisible"
      title="查询详情"
      width="800px"
      :footer="null"
    >
      <template v-if="selectedQuery">
        <a-descriptions bordered>
          <a-descriptions-item label="数据源" :span="3">
            {{ getDatasourceName(selectedQuery.dataSourceId) }}
          </a-descriptions-item>

          <a-descriptions-item label="执行时间" :span="1">
            {{ selectedQuery.executionTime }}ms
          </a-descriptions-item>

          <a-descriptions-item label="状态" :span="1">
            <a-tag :color="getStatusColor(selectedQuery.status)">
              {{ getStatusText(selectedQuery.status) }}
            </a-tag>
          </a-descriptions-item>

          <a-descriptions-item label="创建时间" :span="1">
            {{ selectedQuery.createdAt }}
          </a-descriptions-item>

          <a-descriptions-item label="SQL语句" :span="3">
            <a-typography-paragraph copyable>
              <pre>{{ selectedQuery.sql }}</pre>
            </a-typography-paragraph>
          </a-descriptions-item>

          <a-descriptions-item
            v-if="selectedQuery.errorMessage"
            label="错误信息"
            :span="3"
          >
            <a-typography-paragraph type="danger">
              {{ selectedQuery.errorMessage }}
            </a-typography-paragraph>
          </a-descriptions-item>
        </a-descriptions>
      </template>
    </a-modal>
  </page-container>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { Dayjs } from 'dayjs';
import { useDatasourceStore } from '@/stores/datasource';
import { useQueryHistoryStore } from '@/stores/query-history';
import type { QueryHistory } from '@/types/query-history';
import PageContainer from '@/components/PageContainer.vue';
import ProTable from '@/components/ProTable.vue';

const datasourceStore = useDatasourceStore();
const queryHistoryStore = useQueryHistoryStore();

const currentPage = ref(1);
const pageSize = ref(10);
const detailVisible = ref(false);
const selectedQuery = ref<QueryHistory>();

const formState = reactive({
  dataSourceId: undefined as string | undefined,
  timeRange: [] as [Dayjs, Dayjs] | [],
});

const columns = [
  {
    title: '数据源',
    key: 'dataSourceId',
    width: 200,
    customRender: ({ record }: { record: QueryHistory }) => {
      return getDatasourceName(record.dataSourceId);
    },
  },
  {
    title: 'SQL语句',
    dataIndex: 'sql',
    key: 'sql',
    width: 300,
    ellipsis: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
  },
  {
    title: '执行时间',
    dataIndex: 'executionTime',
    key: 'executionTime',
    width: 120,
    sorter: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    width: 180,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right',
  },
];

const getStatusColor = (status: string) => {
  switch (status) {
    case 'SUCCESS':
      return 'success';
    case 'FAILED':
      return 'error';
    case 'RUNNING':
      return 'processing';
    default:
      return 'default';
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case 'SUCCESS':
      return '成功';
    case 'FAILED':
      return '失败';
    case 'RUNNING':
      return '执行中';
    default:
      return '未知';
  }
};

const getDatasourceName = (id: string) => {
  return (
    datasourceStore.datasources.find((ds) => ds.id === id)?.name || '未知数据源'
  );
};

const handleTableChange = (pagination: any, filters: any, sorter: any) => {
  currentPage.value = pagination.current;
  pageSize.value = pagination.pageSize;
  fetchData();
};

const handleSearch = () => {
  currentPage.value = 1;
  fetchData();
};

const fetchData = () => {
  const [startTime, endTime] = formState.timeRange;
  queryHistoryStore.fetchHistories({
    page: currentPage.value - 1,
    size: pageSize.value,
    dataSourceId: formState.dataSourceId,
    startTime: startTime?.format('YYYY-MM-DD HH:mm:ss'),
    endTime: endTime?.format('YYYY-MM-DD HH:mm:ss'),
  });

  queryHistoryStore.fetchStats(
    formState.dataSourceId,
    startTime?.format('YYYY-MM-DD HH:mm:ss'),
    endTime?.format('YYYY-MM-DD HH:mm:ss')
  );

  queryHistoryStore.fetchSlowQueries(1000, 5);
};

const refresh = () => {
  currentPage.value = 1;
  fetchData();
};

const showDetail = (record: QueryHistory) => {
  selectedQuery.value = record;
  detailVisible.value = true;
};

onMounted(async () => {
  await datasourceStore.fetchDatasources({
    page: 0,
    size: 100,
  });
  fetchData();
});
</script>

<style scoped>
.slow-queries {
  margin-top: 16px;
}

.slow-queries h4 {
  margin-bottom: 16px;
}
</style>