<template>
  <div class="query-history">
    <a-card :loading="loading">
      <template #title>
        <div class="history-header">
          <span>查询历史</span>
          <a-tag v-if="queryDetail">{{ queryDetail.name }}</a-tag>
        </div>
      </template>

      <!-- 搜索表单 -->
      <div class="search-form">
        <a-form layout="inline">
          <a-form-item label="执行状态">
            <a-select
              v-model:value="searchParams.status"
              style="width: 150px"
              allowClear
              placeholder="请选择状态"
            >
              <a-select-option value="running">执行中</a-select-option>
              <a-select-option value="completed">已完成</a-select-option>
              <a-select-option value="failed">执行失败</a-select-option>
              <a-select-option value="cancelled">已取消</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="执行时间">
            <a-range-picker
              v-model:value="dateRange"
              :show-time="{ format: 'HH:mm' }"
              format="YYYY-MM-DD HH:mm"
              value-format="YYYY-MM-DD HH:mm:ss"
              @change="handleDateRangeChange"
            />
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              搜索
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 历史记录表格 -->
      <a-table
        :columns="columns"
        :data-source="queryStore.history"
        :pagination="{
          total: queryStore.historyTotal,
          current: searchParams.page,
          pageSize: searchParams.pageSize,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: total => `共 ${total} 条记录`,
          onChange: handlePageChange,
          onShowSizeChange: handlePageSizeChange,
        }"
        :scroll="{ x: 1200 }"
      >
        <!-- 查询名称 -->
        <template #queryName="{ record }">
          <router-link
            :to="{ name: 'query-execute', params: { id: record.queryId } }"
          >
            {{ record.queryName }}
          </router-link>
        </template>

        <!-- 执行状态 -->
        <template #status="{ record }">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>

        <!-- 执行时间 -->
        <template #startTime="{ text }">
          {{ formatDateTime(text) }}
        </template>

        <!-- 结束时间 -->
        <template #endTime="{ text }">
          {{ text ? formatDateTime(text) : '-' }}
        </template>

        <!-- 执行时长 -->
        <template #duration="{ record }">
          <span v-if="record.duration !== undefined">
            {{ formatDuration(record.duration) }}
          </span>
          <span v-else>-</span>
        </template>

        <!-- 影响行数 -->
        <template #affectedRows="{ record }">
          <span v-if="record.affectedRows !== undefined">
            {{ record.affectedRows.toLocaleString() }}
          </span>
          <span v-else>-</span>
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <a-space>
            <!-- 查看详情 -->
            <a-button
              type="link"
              size="small"
              @click="handleViewDetail(record)"
            >
              查看详情
            </a-button>

            <!-- 取消执行 -->
            <a-button
              v-if="record.status === 'running'"
              type="link"
              danger
              size="small"
              :loading="record.cancelling"
              @click="handleCancel(record)"
            >
              取消执行
            </a-button>

            <!-- 重新执行 -->
            <a-button
              v-if="['failed', 'cancelled'].includes(record.status)"
              type="link"
              size="small"
              @click="handleRerun(record)"
            >
              重新执行
            </a-button>
          </a-space>
        </template>
      </a-table>

      <!-- 详情抽屉 -->
      <a-modal
        v-model:open="detailVisible"
        title="查询详情"
        width="800px"
        :footer="null"
      >
        <template v-if="selectedRecord">
          <a-descriptions bordered>
            <a-descriptions-item label="查询名称" :span="3">
              {{ selectedRecord.queryName }}
            </a-descriptions-item>
            
            <a-descriptions-item label="执行人" :span="3">
              {{ selectedRecord.executor }}
            </a-descriptions-item>

            <a-descriptions-item label="执行状态" :span="3">
              <a-tag :color="getStatusColor(selectedRecord.status)">
                {{ getStatusText(selectedRecord.status) }}
              </a-tag>
            </a-descriptions-item>

            <a-descriptions-item label="开始时间" :span="3">
              {{ formatDateTime(selectedRecord.startTime) }}
            </a-descriptions-item>

            <a-descriptions-item label="结束时间" :span="3">
              {{ selectedRecord.endTime ? formatDateTime(selectedRecord.endTime) : '-' }}
            </a-descriptions-item>

            <a-descriptions-item label="执行时长" :span="3">
              {{ selectedRecord.duration ? formatDuration(selectedRecord.duration) : '-' }}
            </a-descriptions-item>

            <a-descriptions-item label="影响行数" :span="3">
              {{ selectedRecord.affectedRows?.toLocaleString() ?? '-' }}
            </a-descriptions-item>

            <a-descriptions-item
              v-if="selectedRecord.error"
              label="错误信息"
              :span="3"
            >
              <pre class="error-message">{{ selectedRecord.error }}</pre>
            </a-descriptions-item>

            <a-descriptions-item
              v-if="selectedRecord.conditions"
              label="查询条件"
              :span="3"
            >
              <pre class="conditions">{{ JSON.stringify(selectedRecord.conditions, null, 2) }}</pre>
            </a-descriptions-item>
          </a-descriptions>
        </template>
      </a-modal>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { SearchOutlined } from '@ant-design/icons-vue';
import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';
import { useQueryStore } from '@/stores';
import type { QueryDetail, QueryHistory } from '@/types/query';

dayjs.extend(duration);

const route = useRoute();
const router = useRouter();
const queryStore = useQueryStore();

// 加载状态
const loading = ref(false);

// 查询详情
const queryDetail = ref<QueryDetail>();

// 日期范围
const dateRange = ref<[dayjs.Dayjs, dayjs.Dayjs]>();

// 搜索参数
const searchParams = reactive({
  queryId: route.query.queryId as string,
  status: undefined as string | undefined,
  startTime: undefined as string | undefined,
  endTime: undefined as string | undefined,
  page: 1,
  pageSize: 10,
});

// 表格列定义
const columns = [
  {
    title: '查询名称',
    dataIndex: 'queryName',
    key: 'queryName',
    slots: { customRender: 'queryName' },
    width: 200,
    fixed: 'left',
  },
  {
    title: '执行人',
    dataIndex: 'executor',
    key: 'executor',
    width: 120,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    slots: { customRender: 'status' },
    width: 100,
  },
  {
    title: '开始时间',
    dataIndex: 'startTime',
    key: 'startTime',
    slots: { customRender: 'startTime' },
    width: 180,
    sorter: true,
  },
  {
    title: '结束时间',
    dataIndex: 'endTime',
    key: 'endTime',
    slots: { customRender: 'endTime' },
    width: 180,
  },
  {
    title: '执行时长',
    dataIndex: 'duration',
    key: 'duration',
    slots: { customRender: 'duration' },
    width: 120,
    sorter: true,
  },
  {
    title: '影响行数',
    dataIndex: 'affectedRows',
    key: 'affectedRows',
    slots: { customRender: 'affectedRows' },
    width: 120,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 200,
    fixed: 'right',
  },
];

// 详情抽屉
const detailVisible = ref(false);
const selectedRecord = ref<QueryHistory>();

// 获取查询详情
const fetchQueryDetail = async () => {
  if (!searchParams.queryId) return;
  
  try {
    const detail = await queryStore.fetchDetail(searchParams.queryId);
    queryDetail.value = detail;
  } catch (error) {
    console.error('Failed to fetch query detail:', error);
  }
};

// 获取历史记录
const fetchHistory = async () => {
  loading.value = true;
  try {
    await queryStore.fetchHistory(searchParams);
  } finally {
    loading.value = false;
  }
};

// 日期范围变更
const handleDateRangeChange = (dates: [dayjs.Dayjs, dayjs.Dayjs] | null) => {
  if (dates) {
    searchParams.startTime = dates[0].format('YYYY-MM-DD HH:mm:ss');
    searchParams.endTime = dates[1].format('YYYY-MM-DD HH:mm:ss');
  } else {
    searchParams.startTime = undefined;
    searchParams.endTime = undefined;
  }
};

// 搜索
const handleSearch = () => {
  searchParams.page = 1;
  fetchHistory();
};

// 页码变更
const handlePageChange = (page: number, pageSize: number) => {
  searchParams.page = page;
  searchParams.pageSize = pageSize;
  fetchHistory();
};

// 每页条数变更
const handlePageSizeChange = (current: number, size: number) => {
  searchParams.page = 1;
  searchParams.pageSize = size;
  fetchHistory();
};

// 查看详情
const handleViewDetail = (record: QueryHistory) => {
  selectedRecord.value = record;
  detailVisible.value = true;
};

// 取消执行
const handleCancel = async (record: QueryHistory) => {
  try {
    record.cancelling = true;
    await queryStore.cancel(record.queryId, record.id);
    message.success('已取消执行');
    fetchHistory();
  } catch (error: any) {
    message.error(error.message || '取消失败');
  } finally {
    record.cancelling = false;
  }
};

// 重新执行
const handleRerun = (record: QueryHistory) => {
  router.push({
    name: 'query-execute',
    params: { id: record.queryId },
    query: {
      conditions: record.conditions ? JSON.stringify(record.conditions) : undefined,
    },
  });
};

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    running: 'processing',
    completed: 'success',
    failed: 'error',
    cancelled: 'warning',
  };
  return colors[status] || 'default';
};

// 获取状态文本
const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    running: '执行中',
    completed: '已完成',
    failed: '执行失败',
    cancelled: '已取消',
  };
  return texts[status] || status;
};

// 格式化日期时间
const formatDateTime = (date: string | Date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
};

// 格式化时长
const formatDuration = (ms: number) => {
  const d = dayjs.duration(ms);
  if (d.asSeconds() < 1) {
    return `${Math.round(d.asMilliseconds())}ms`;
  }
  if (d.asMinutes() < 1) {
    return `${Math.round(d.asSeconds())}s`;
  }
  return `${Math.floor(d.asMinutes())}m ${Math.round(d.seconds())}s`;
};

// 监听路由参数变化
watch(
  () => route.query,
  () => {
    searchParams.queryId = route.query.queryId as string;
    fetchQueryDetail();
    fetchHistory();
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
.query-history {
  .history-header {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .search-form {
    margin-bottom: 16px;
  }

  .error-message {
    margin: 0;
    padding: 8px;
    background-color: #fff2f0;
    border: 1px solid #ffccc7;
    border-radius: 2px;
    color: #ff4d4f;
    font-family: monospace;
    white-space: pre-wrap;
    word-break: break-all;
  }

  .conditions {
    margin: 0;
    padding: 8px;
    background-color: #f5f5f5;
    border: 1px solid #d9d9d9;
    border-radius: 2px;
    font-family: monospace;
    white-space: pre-wrap;
    word-break: break-all;
  }
}
</style>