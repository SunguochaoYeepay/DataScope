<template>
  <div class="query-stats">
    <error-message
      :error="queryStore.error"
      @close="queryStore.error = null"
    />
    
    <a-card title="查询统计" :bordered="false">
      <template #extra>
        <a-space>
          <a-range-picker
            v-model:value="dateRange"
            :show-time="{ format: 'HH:mm:ss' }"
            format="YYYY-MM-DD HH:mm:ss"
            :placeholder="['开始时间', '结束时间']"
          />
          <a-button type="primary" @click="fetchData">
            <template #icon><bar-chart-outlined /></template>
            统计
          </a-button>
        </a-space>
      </template>

      <loading-spinner :loading="queryStore.loading" />

      <div v-show="!queryStore.loading" class="stats-content">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-card class="stat-card">
              <a-statistic
                title="总执行次数"
                :value="queryStore.queryStats?.total_executions || 0"
                :precision="0"
              >
                <template #suffix>次</template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <a-statistic
                title="平均执行时间"
                :value="queryStore.queryStats?.avg_execution_time || 0"
                :precision="2"
              >
                <template #suffix>ms</template>
              </a-statistic>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <a-statistic
                title="成功率"
                :value="successRate"
                :precision="2"
                suffix="%"
                :value-style="{ color: successRate >= 90 ? '#3f8600' : '#cf1322' }"
              />
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card class="stat-card">
              <a-statistic
                title="总影响行数"
                :value="queryStore.queryStats?.total_affected_rows || 0"
                :precision="0"
              >
                <template #suffix>行</template>
              </a-statistic>
            </a-card>
          </a-col>
        </a-row>

        <a-divider />

        <a-row :gutter="16">
          <a-col :span="12">
            <a-card title="执行时间分布" :bordered="false">
              <a-descriptions bordered>
                <a-descriptions-item label="最大执行时间" span="3">
                  {{ queryStore.queryStats?.max_execution_time || 0 }}ms
                </a-descriptions-item>
                <a-descriptions-item label="最小执行时间" span="3">
                  {{ queryStore.queryStats?.min_execution_time || 0 }}ms
                </a-descriptions-item>
                <a-descriptions-item label="平均执行时间" span="3">
                  {{ queryStore.queryStats?.avg_execution_time || 0 }}ms
                </a-descriptions-item>
              </a-descriptions>
            </a-card>
          </a-col>
          <a-col :span="12">
            <a-card title="执行状态分布" :bordered="false">
              <a-descriptions bordered>
                <a-descriptions-item label="成功次数" span="3">
                  {{ queryStore.queryStats?.success_count || 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="失败次数" span="3">
                  {{ queryStore.queryStats?.failed_count || 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="成功率" span="3">
                  {{ successRate }}%
                </a-descriptions-item>
              </a-descriptions>
            </a-card>
          </a-col>
        </a-row>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useQueryStore } from '../store/query';
import { BarChartOutlined } from '@ant-design/icons-vue';
import ErrorMessage from '../components/ErrorMessage.vue';
import LoadingSpinner from '../components/LoadingSpinner.vue';
import dayjs from 'dayjs';
import type { Dayjs } from 'dayjs';

const queryStore = useQueryStore();
const dateRange = ref<[Dayjs, Dayjs]>([
  dayjs().startOf('day'),
  dayjs().endOf('day')
]);

const successRate = computed(() => {
  if (!queryStore.queryStats) return 0;
  const total = queryStore.queryStats.success_count + queryStore.queryStats.failed_count;
  if (total === 0) return 0;
  const rate = (queryStore.queryStats.success_count / total) * 100;
  return Number(rate.toFixed(2));
});

const fetchData = () => {
  if (!dateRange.value) return;
  const [start, end] = dateRange.value;
  queryStore.fetchQueryStats(
    start.format('YYYY-MM-DD HH:mm:ss'),
    end.format('YYYY-MM-DD HH:mm:ss')
  );
};

// 初始化数据
fetchData();
</script>

<style scoped>
.query-stats {
  padding: 24px;
}

.stat-card {
  margin-bottom: 16px;
}

:deep(.ant-statistic-title) {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
}

:deep(.ant-statistic-content) {
  font-size: 24px;
}

.stats-content {
  margin-top: 24px;
}

.ant-card {
  margin-bottom: 24px;
}

.ant-descriptions {
  margin-top: 16px;
}
</style>