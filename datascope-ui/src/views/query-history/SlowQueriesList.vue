<template>
  <page-container
    title="慢查询分析"
    sub-title="分析和优化慢SQL查询"
  >
    <a-card :bordered="false">
      <a-form
        :model="formState"
        layout="inline"
        @finish="handleSearch"
      >
        <a-form-item
          name="datasourceId"
          label="数据源"
        >
          <a-select
            v-model:value="formState.datasourceId"
            placeholder="请选择数据源"
            style="width: 200px"
            allowClear
          >
            <a-select-option
              v-for="ds in datasources"
              :key="ds.id"
              :value="ds.id"
            >
              {{ ds.name }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          name="timeRange"
          label="时间范围"
        >
          <a-range-picker
            v-model:value="formState.timeRange"
            :show-time="{ format: 'HH:mm' }"
            format="YYYY-MM-DD HH:mm"
            :placeholder="['开始时间', '结束时间']"
          />
        </a-form-item>

        <a-form-item
          name="threshold"
          label="执行时间阈值"
        >
          <a-input-number
            v-model:value="formState.threshold"
            placeholder="请输入阈值(秒)"
            :min="0"
            :precision="1"
            style="width: 120px"
          />
        </a-form-item>

        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit">
              查询
            </a-button>
            <a-button @click="handleReset">
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <a-divider />

      <a-row :gutter="16">
        <a-col :span="16">
          <pro-table
            :columns="columns"
            :data-source="tableData"
            :loading="loading"
            :pagination="pagination"
            :scroll="{ x: 1300 }"
            row-key="id"
            @change="handleTableChange"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'action'">
                <a-space>
                  <a @click="showQueryDetail(record)">详情</a>
                  <a @click="showExplain(record)">执行计划</a>
                  <a @click="optimizeQuery(record)">优化建议</a>
                </a-space>
              </template>
            </template>
          </pro-table>
        </a-col>

        <a-col :span="8">
          <a-card title="统计信息" :bordered="false">
            <a-statistic
              title="平均执行时间"
              :value="statistics.avgExecutionTime"
              :precision="2"
              suffix="秒"
              style="margin-bottom: 16px"
            />
            <a-statistic
              title="最长执行时间"
              :value="statistics.maxExecutionTime"
              :precision="2"
              suffix="秒"
              style="margin-bottom: 16px"
            />
            <a-statistic
              title="慢查询总数"
              :value="statistics.totalSlowQueries"
              style="margin-bottom: 16px"
            />
            <a-progress
              :percent="statistics.optimizationRate"
              status="active"
              style="margin-bottom: 8px"
            />
            <div class="stat-label">已优化比例</div>
          </a-card>

          <a-card
            title="Top 5 最慢查询"
            :bordered="false"
            style="margin-top: 16px"
          >
            <a-list
              :data-source="topSlowQueries"
              size="small"
            >
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #title>
                      <a @click="showQueryDetail(item)">
                        {{ item.sql.substring(0, 50) }}...
                      </a>
                    </template>
                    <template #description>
                      执行时间: {{ item.executionTime }}秒
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>
      </a-row>
    </a-card>

    <!-- 查询详情弹窗 -->
    <a-modal
      v-model:visible="detailModalVisible"
      title="查询详情"
      width="800px"
      :footer="null"
    >
      <template v-if="selectedQuery">
        <a-descriptions bordered>
          <a-descriptions-item label="数据源" :span="3">
            {{ selectedQuery.datasourceName }}
          </a-descriptions-item>
          <a-descriptions-item label="执行时间" :span="3">
            {{ selectedQuery.executionTime }}秒
          </a-descriptions-item>
          <a-descriptions-item label="执行时间" :span="3">
            {{ selectedQuery.executedAt }}
          </a-descriptions-item>
          <a-descriptions-item label="SQL语句" :span="3">
            <a-typography-paragraph copyable>
              <pre>{{ selectedQuery.sql }}</pre>
            </a-typography-paragraph>
          </a-descriptions-item>
        </a-descriptions>
      </template>
    </a-modal>

    <!-- 执行计划弹窗 -->
    <a-modal
      v-model:visible="explainModalVisible"
      title="执行计划"
      width="1000px"
      :footer="null"
    >
      <template v-if="explainResult">
        <a-table
          :columns="explainColumns"
          :data-source="explainResult"
          :pagination="false"
          size="small"
        />
      </template>
    </a-modal>

    <!-- 优化建议弹窗 -->
    <a-modal
      v-model:visible="optimizeModalVisible"
      title="优化建议"
      width="800px"
      :footer="null"
    >
      <template v-if="optimizationSuggestions">
        <a-timeline>
          <a-timeline-item
            v-for="(suggestion, index) in optimizationSuggestions"
            :key="index"
            :color="suggestion.severity === 'high' ? 'red' : suggestion.severity === 'medium' ? 'orange' : 'blue'"
          >
            <template #dot>
              <template v-if="suggestion.severity === 'high'">
                <warning-outlined />
              </template>
              <template v-else-if="suggestion.severity === 'medium'">
                <exclamation-outlined />
              </template>
              <template v-else>
                <info-outlined />
              </template>
            </template>
            <h4>{{ suggestion.title }}</h4>
            <p>{{ suggestion.description }}</p>
            <a-typography-paragraph v-if="suggestion.example" type="secondary">
              示例: {{ suggestion.example }}
            </a-typography-paragraph>
          </a-timeline-item>
        </a-timeline>
      </template>
    </a-modal>
  </page-container>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import type { TablePaginationConfig } from 'ant-design-vue';
import type { Dayjs } from 'dayjs';
import {
  WarningOutlined,
  ExclamationOutlined,
  InfoOutlined,
} from '@ant-design/icons-vue';

// 类型定义
interface DataSource {
  id: number;
  name: string;
}

interface QueryRecord {
  id: number;
  datasourceId: number;
  datasourceName: string;
  sql: string;
  executionTime: number;
  executedAt: string;
  status: string;
  affectedRows: number;
}

interface FormState {
  datasourceId?: number;
  timeRange?: [Dayjs, Dayjs];
  threshold: number;
}

interface Statistics {
  avgExecutionTime: number;
  maxExecutionTime: number;
  totalSlowQueries: number;
  optimizationRate: number;
}

interface ExplainRecord {
  id: string;
  selectType: string;
  table: string;
  partitions: string | null;
  type: string;
  possibleKeys: string | null;
  key: string | null;
  keyLen: string | null;
  ref: string | null;
  rows: number;
  filtered: number;
  extra: string | null;
}

interface OptimizationSuggestion {
  severity: 'high' | 'medium' | 'low';
  title: string;
  description: string;
  example?: string;
}

// 表格列定义
const columns = [
  {
    title: '数据源',
    dataIndex: 'datasourceName',
    width: 150,
  },
  {
    title: 'SQL语句',
    dataIndex: 'sql',
    width: 300,
    ellipsis: true,
  },
  {
    title: '执行时间(秒)',
    dataIndex: 'executionTime',
    width: 120,
    sorter: true,
  },
  {
    title: '执行时间',
    dataIndex: 'executedAt',
    width: 180,
    sorter: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    width: 100,
  },
  {
    title: '影响行数',
    dataIndex: 'affectedRows',
    width: 100,
  },
  {
    title: '操作',
    key: 'action',
    width: 180,
    fixed: 'right',
  },
];

const explainColumns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 60,
  },
  {
    title: 'select_type',
    dataIndex: 'selectType',
    width: 120,
  },
  {
    title: 'table',
    dataIndex: 'table',
    width: 120,
  },
  {
    title: 'type',
    dataIndex: 'type',
    width: 80,
  },
  {
    title: 'possible_keys',
    dataIndex: 'possibleKeys',
    width: 150,
    ellipsis: true,
  },
  {
    title: 'key',
    dataIndex: 'key',
    width: 120,
  },
  {
    title: 'rows',
    dataIndex: 'rows',
    width: 80,
  },
  {
    title: 'filtered',
    dataIndex: 'filtered',
    width: 80,
  },
  {
    title: 'Extra',
    dataIndex: 'extra',
    width: 200,
    ellipsis: true,
  },
];

// 响应式状态
const loading = ref(false);
const datasources = ref<DataSource[]>([]);
const tableData = ref<QueryRecord[]>([]);
const topSlowQueries = ref<QueryRecord[]>([]);
const pagination = reactive<TablePaginationConfig>({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
});

const formState = reactive<FormState>({
  datasourceId: undefined,
  timeRange: undefined,
  threshold: 1.0,
});

const statistics = reactive<Statistics>({
  avgExecutionTime: 0,
  maxExecutionTime: 0,
  totalSlowQueries: 0,
  optimizationRate: 0,
});

// 弹窗状态
const detailModalVisible = ref(false);
const explainModalVisible = ref(false);
const optimizeModalVisible = ref(false);
const selectedQuery = ref<QueryRecord | null>(null);
const explainResult = ref<ExplainRecord[]>([]);
const optimizationSuggestions = ref<OptimizationSuggestion[]>([]);

// 生命周期钩子
onMounted(async () => {
  await Promise.all([
    fetchDatasources(),
    fetchData(),
  ]);
});

// 方法定义
const fetchDatasources = async () => {
  try {
    // TODO: 实现获取数据源列表的API调用
    datasources.value = [];
  } catch (error) {
    console.error('获取数据源列表失败:', error);
  }
};

const fetchData = async () => {
  loading.value = true;
  try {
    // TODO: 实现获取慢查询数据的API调用
    tableData.value = [];
    topSlowQueries.value = [];
    updateStatistics();
  } catch (error) {
    console.error('获取数据失败:', error);
  } finally {
    loading.value = false;
  }
};

const updateStatistics = () => {
  if (tableData.value.length === 0) {
    return;
  }

  const executionTimes = tableData.value.map(q => q.executionTime);
  statistics.avgExecutionTime = executionTimes.reduce((a, b) => a + b, 0) / executionTimes.length;
  statistics.maxExecutionTime = Math.max(...executionTimes);
  statistics.totalSlowQueries = tableData.value.length;
  statistics.optimizationRate = 30; // TODO: 计算实际的优化率
};

const handleSearch = () => {
  pagination.current = 1;
  fetchData();
};

const handleReset = () => {
  formState.datasourceId = undefined;
  formState.timeRange = undefined;
  formState.threshold = 1.0;
  handleSearch();
};

const handleTableChange = (pag: TablePaginationConfig) => {
  pagination.current = pag.current || 1;
  pagination.pageSize = pag.pageSize || 10;
  fetchData();
};

const showQueryDetail = (record: QueryRecord) => {
  selectedQuery.value = record;
  detailModalVisible.value = true;
};

const showExplain = async (record: QueryRecord) => {
  try {
    // TODO: 实现获取执行计划的API调用
    explainResult.value = [];
    explainModalVisible.value = true;
  } catch (error) {
    console.error('获取执行计划失败:', error);
  }
};

const optimizeQuery = async (record: QueryRecord) => {
  try {
    // TODO: 实现获取优化建议的API调用
    optimizationSuggestions.value = [
      {
        severity: 'high',
        title: '添加索引',
        description: '在查询条件字段上添加索引可以提高查询性能',
        example: 'CREATE INDEX idx_name ON table_name (column_name)',
      },
      {
        severity: 'medium',
        title: '优化JOIN条件',
        description: '确保JOIN条件字段类型匹配且都有索引',
      },
      {
        severity: 'low',
        title: '使用LIMIT限制结果集',
        description: '添加LIMIT子句限制返回的数据量',
        example: 'SELECT * FROM table_name LIMIT 1000',
      },
    ];
    optimizeModalVisible.value = true;
  } catch (error) {
    console.error('获取优化建议失败:', error);
  }
};
</script>

<style scoped>
.stat-label {
  color: rgba(0, 0, 0, 0.45);
  font-size: 14px;
}

:deep(.ant-timeline-item-head-custom) {
  background: none;
  padding: 0;
  margin-top: 6px;
}
</style>