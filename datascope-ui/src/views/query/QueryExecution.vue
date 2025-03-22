<template>
  <div class="query-execution">
    <a-card :loading="loading">
      <template #title>
        <div class="execution-header">
          <span class="query-name">{{ queryDetail?.name }}</span>
          <a-tag>{{ queryDetail?.dataSourceName }}</a-tag>
        </div>
      </template>
      
      <template #extra>
        <a-space>
          <a-button @click="handleBack">返回</a-button>
          <a-button
            @click="handleViewHistory"
          >
            历史记录
          </a-button>
          <a-button
            type="primary"
            :loading="executing"
            @click="handleExecute"
          >
            执行查询
          </a-button>
        </a-space>
      </template>

      <!-- 查询条件区域 -->
      <div class="conditions-section" v-if="queryDetail?.config.conditions.length">
        <a-form
          ref="conditionFormRef"
          :model="conditionValues"
          layout="inline"
        >
          <a-form-item
            v-for="condition in queryDetail.config.conditions"
            :key="condition.field"
            :name="condition.field"
            :label="condition.label || condition.field"
            :rules="condition.required ? [{ required: true, message: `请输入${condition.label || condition.field}` }] : undefined"
          >
            <!-- 输入框 -->
            <a-input
              v-if="condition.type === 'input'"
              v-model:value="conditionValues[condition.field]"
              :placeholder="`请输入${condition.label || condition.field}`"
              :style="{ width: '200px' }"
            />

            <!-- 下拉框 -->
            <a-select
              v-else-if="condition.type === 'select'"
              v-model:value="conditionValues[condition.field]"
              :placeholder="`请选择${condition.label || condition.field}`"
              :style="{ width: '200px' }"
            >
              <a-select-option
                v-for="option in condition.options"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </a-select-option>
            </a-select>

            <!-- 日期选择器 -->
            <a-date-picker
              v-else-if="condition.type === 'date'"
              v-model:value="conditionValues[condition.field]"
              :placeholder="`请选择${condition.label || condition.field}`"
              :style="{ width: '200px' }"
            />

            <!-- 日期时间选择器 -->
            <a-date-picker
              v-else-if="condition.type === 'datetime'"
              v-model:value="conditionValues[condition.field]"
              :placeholder="`请选择${condition.label || condition.field}`"
              :style="{ width: '200px' }"
              showTime
            />

            <!-- 数字输入框 -->
            <a-input-number
              v-else-if="condition.type === 'number'"
              v-model:value="conditionValues[condition.field]"
              :placeholder="`请输入${condition.label || condition.field}`"
              :style="{ width: '200px' }"
            />
          </a-form-item>
        </a-form>
      </div>

      <!-- SQL预览区域 -->
      <div class="sql-preview">
        <div class="section-title">SQL预览</div>
        <MonacoEditor
          v-model:value="finalSql"
          language="sql"
          :options="{
            readOnly: true,
            minimap: { enabled: false },
            lineNumbers: 'on',
            scrollBeyondLastLine: false,
            automaticLayout: true,
          }"
          height="150"
        />
      </div>

      <!-- 执行结果区域 -->
      <div class="result-section" v-if="executionResult">
        <div class="section-header">
          <div class="section-title">查询结果</div>
          <div class="result-stats">
            <a-space>
              <span>执行时间：{{ executionResult.executionTime }}ms</span>
              <span>影响行数：{{ executionResult.affectedRows }}</span>
              <a-button
                type="link"
                icon="download"
                @click="handleExport"
              >
                导出结果
              </a-button>
            </a-space>
          </div>
        </div>

        <!-- 结果表格 -->
        <a-table
          :columns="resultColumns"
          :data-source="executionResult.data"
          :scroll="{ x: 'max-content' }"
          :pagination="queryDetail?.config.pagination.enabled ? {
            total: executionResult.total,
            pageSize: queryDetail.config.pagination.pageSize,
            current: currentPage,
            onChange: handlePageChange,
          } : false"
          size="middle"
          :loading="executing"
        >
          <!-- 自定义列渲染 -->
          <template
            v-for="col in queryDetail?.config.columns"
            :key="col.field"
            #[col.field]="{ text }"
          >
            <!-- 日期格式化 -->
            <template v-if="col.type === 'date'">
              {{ formatDate(text, col.format || 'YYYY-MM-DD') }}
            </template>
            
            <!-- 日期时间格式化 -->
            <template v-else-if="col.type === 'datetime'">
              {{ formatDate(text, col.format || 'YYYY-MM-DD HH:mm:ss') }}
            </template>
            
            <!-- 数字格式化 -->
            <template v-else-if="col.type === 'number'">
              {{ formatNumber(text, col.format) }}
            </template>
            
            <!-- 布尔值展示 -->
            <template v-else-if="col.type === 'boolean'">
              <a-tag :color="text ? 'success' : 'default'">
                {{ text ? '是' : '否' }}
              </a-tag>
            </template>
            
            <!-- 掩码处理 -->
            <template v-else-if="col.mask">
              {{ maskText(text) }}
            </template>
            
            <!-- 默认文本展示 -->
            <template v-else>
              {{ text }}
            </template>
          </template>
        </a-table>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { message } from 'ant-design-vue';
import type { FormInstance } from 'ant-design-vue';
import dayjs from 'dayjs';
import MonacoEditor from '@/components/MonacoEditor.vue';
import { useQueryStore } from '@/stores';
import type { QueryDetail, QueryExecutionResult } from '@/types/query';

const router = useRouter();
const route = useRoute();
const queryStore = useQueryStore();

// 加载状态
const loading = ref(false);
const executing = ref(false);

// 查询详情
const queryDetail = ref<QueryDetail>();

// 条件表单实例
const conditionFormRef = ref<FormInstance>();

// 条件值
const conditionValues = reactive<Record<string, any>>({});

// 当前页码
const currentPage = ref(1);

// 执行结果
const executionResult = ref<QueryExecutionResult>();

// 计算最终SQL
const finalSql = computed(() => {
  if (!queryDetail.value?.sql) return '';
  
  let sql = queryDetail.value.sql;
  
  // 替换条件占位符
  Object.entries(conditionValues).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      const placeholder = `:${key}`;
      sql = sql.replace(
        new RegExp(placeholder, 'g'),
        typeof value === 'string' ? `'${value}'` : value
      );
    }
  });
  
  return sql;
});

// 计算结果列定义
const resultColumns = computed(() => {
  if (!queryDetail.value?.config.columns) return [];
  
  return queryDetail.value.config.columns
    .filter(col => !col.hidden)
    .map(col => ({
      title: col.label || col.field,
      dataIndex: col.field,
      key: col.field,
      width: col.width,
      fixed: col.fixed,
      sorter: col.sortable,
      slots: {
        customRender: col.field,
      },
    }));
});

// 获取查询详情
const fetchDetail = async () => {
  if (!route.params.id) return;
  
  loading.value = true;
  try {
    const data = await queryStore.fetchDetail(route.params.id as string);
    queryDetail.value = data;
    
    // 设置默认条件值
    if (data.config.conditions) {
      data.config.conditions.forEach(condition => {
        if (condition.defaultValue !== undefined) {
          conditionValues[condition.field] = condition.defaultValue;
        }
      });
    }
  } finally {
    loading.value = false;
  }
};

// 执行查询
const handleExecute = async () => {
  // 验证必填条件
  try {
    await conditionFormRef.value?.validate();
  } catch {
    return;
  }

  executing.value = true;
  try {
    const result = await queryStore.execute({
      id: route.params.id as string,
      conditions: conditionValues,
      page: currentPage.value,
      pageSize: queryDetail.value?.config.pagination.pageSize,
    });
    executionResult.value = result;
    message.success('执行成功');
  } catch (error: any) {
    message.error(error.message || '执行失败');
  } finally {
    executing.value = false;
  }
};

// 页码变更
const handlePageChange = (page: number) => {
  currentPage.value = page;
  handleExecute();
};

// 导出结果
const handleExport = () => {
  if (!executionResult.value?.data.length) {
    message.warning('暂无数据可导出');
    return;
  }

  // TODO: 调用导出接口
  message.success('导出成功');
};

// 返回列表
const handleBack = () => {
  router.back();
};

// 查看历史记录
const handleViewHistory = () => {
  router.push({
    name: 'query-history',
    query: { queryId: route.params.id as string },
  });
};

// 格式化日期
const formatDate = (value: string | number | Date, format: string) => {
  if (!value) return '';
  return dayjs(value).format(format);
};

// 格式化数字
const formatNumber = (value: number, format?: string) => {
  if (value === undefined || value === null) return '';
  if (!format) return value;
  
  // TODO: 根据format格式化数字
  return value;
};

// 文本掩码
const maskText = (text: string) => {
  if (!text) return '';
  if (text.length <= 2) return '*'.repeat(text.length);
  return text.charAt(0) + '*'.repeat(text.length - 2) + text.charAt(text.length - 1);
};

onMounted(() => {
  fetchDetail();
});
</script>

<style lang="scss" scoped>
.query-execution {
  .execution-header {
    display: flex;
    align-items: center;
    gap: 8px;

    .query-name {
      font-weight: 500;
    }
  }

  .conditions-section {
    margin-bottom: 24px;
    padding: 16px;
    background: #fafafa;
    border-radius: 2px;
  }

  .sql-preview {
    margin-bottom: 24px;

    .section-title {
      margin-bottom: 8px;
      font-weight: 500;
    }
  }

  .result-section {
    .section-header {
      margin-bottom: 16px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .section-title {
        font-weight: 500;
      }
    }
  }
}
</style>