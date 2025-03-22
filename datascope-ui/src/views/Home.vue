<template>
  <div class="home-container">
    <a-row :gutter="[16, 16]">
      <!-- 数据源概览 -->
      <a-col :span="8">
        <a-card>
          <template #title>
            <DatabaseOutlined />
            <span>数据源</span>
          </template>
          <a-statistic
            title="总数"
            :value="statistics.datasourceCount"
            :loading="loading"
          >
            <template #suffix>
              <a-tag color="blue">个</a-tag>
            </template>
          </a-statistic>
          <a-button
            type="link"
            @click="router.push({ name: 'datasource' })"
          >
            查看详情
          </a-button>
        </a-card>
      </a-col>

      <!-- 查询概览 -->
      <a-col :span="8">
        <a-card>
          <template #title>
            <CodeOutlined />
            <span>查询</span>
          </template>
          <a-statistic
            title="总数"
            :value="statistics.queryCount"
            :loading="loading"
          >
            <template #suffix>
              <a-tag color="green">个</a-tag>
            </template>
          </a-statistic>
          <a-button
            type="link"
            @click="router.push({ name: 'query' })"
          >
            查看详情
          </a-button>
        </a-card>
      </a-col>

      <!-- 执行概览 -->
      <a-col :span="8">
        <a-card>
          <template #title>
            <ThunderboltOutlined />
            <span>执行</span>
          </template>
          <a-statistic
            title="今日执行"
            :value="statistics.todayExecutions"
            :loading="loading"
          >
            <template #suffix>
              <a-tag color="orange">次</a-tag>
            </template>
          </a-statistic>
          <a-button
            type="link"
            @click="router.push({ name: 'query' })"
          >
            查看详情
          </a-button>
        </a-card>
      </a-col>

      <!-- 最近执行的查询 -->
      <a-col :span="12">
        <a-card title="最近执行">
          <a-table
            :columns="recentColumns"
            :data-source="recentExecutions"
            :loading="loading"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">
                  {{ record.status }}
                </a-tag>
              </template>
              <template v-else-if="column.key === 'action'">
                <a @click="router.push({
                  name: 'query-execute',
                  params: { id: record.queryId }
                })">查看</a>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>

      <!-- 常用查询 -->
      <a-col :span="12">
        <a-card title="常用查询">
          <a-table
            :columns="frequentColumns"
            :data-source="frequentQueries"
            :loading="loading"
            :pagination="false"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'action'">
                <a @click="router.push({
                  name: 'query-execute',
                  params: { id: record.id }
                })">执行</a>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import {
  DatabaseOutlined,
  CodeOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons-vue';
import request from '@/utils/request';

// 查询执行状态类型
type QueryExecutionStatus = 'running' | 'completed' | 'failed' | 'cancelled';

// 统计数据类型
interface Statistics {
  datasourceCount: number;
  queryCount: number;
  todayExecutions: number;
}

// 查询执行记录类型
interface QueryExecution {
  queryId: string;
  queryName: string;
  executedBy: string;
  status: QueryExecutionStatus;
  duration: string;
}

// 常用查询类型
interface FrequentQuery {
  id: string;
  name: string;
  executeCount: number;
  updatedAt: string;
}

const router = useRouter();
const loading = ref(false);

// 统计数据
const statistics = ref<Statistics>({
  datasourceCount: 0,
  queryCount: 0,
  todayExecutions: 0,
});

// 最近执行列表
const recentColumns = [
  { title: '查询名称', dataIndex: 'queryName', key: 'queryName' },
  { title: '执行人', dataIndex: 'executedBy', key: 'executedBy' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '耗时', dataIndex: 'duration', key: 'duration' },
  { title: '操作', key: 'action' },
];

const recentExecutions = ref<QueryExecution[]>([]);

// 常用查询列表
const frequentColumns = [
  { title: '查询名称', dataIndex: 'name', key: 'name' },
  { title: '执行次数', dataIndex: 'executeCount', key: 'executeCount' },
  { title: '更新时间', dataIndex: 'updatedAt', key: 'updatedAt' },
  { title: '操作', key: 'action' },
];

const frequentQueries = ref<FrequentQuery[]>([]);

// 获取状态颜色
const getStatusColor = (status: QueryExecutionStatus) => {
  const colors: Record<QueryExecutionStatus, string> = {
    running: 'processing',
    completed: 'success',
    failed: 'error',
    cancelled: 'default',
  };
  return colors[status];
};

// 获取首页数据
const fetchHomeData = async () => {
  loading.value = true;
  try {
    const [
      statisticsData,
      recentData,
      frequentData,
    ] = await Promise.all([
      request.get<Statistics>('/dashboard/statistics'),
      request.get<QueryExecution[]>('/dashboard/recent-executions'),
      request.get<FrequentQuery[]>('/dashboard/frequent-queries'),
    ]);
    
    statistics.value = statisticsData;
    recentExecutions.value = recentData;
    frequentQueries.value = frequentData;
  } catch (error: any) {
    console.error('获取首页数据失败:', error.message);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchHomeData();
});
</script>

<style lang="scss" scoped>
.home-container {
  .ant-card {
    height: 100%;
  }

  :deep(.ant-card-head-title) {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .ant-statistic {
    margin-bottom: 16px;
  }
}
</style>