<template>
  <div class="pro-table">
    <div v-if="$slots.toolbar" class="toolbar">
      <slot name="toolbar" />
    </div>
    
    <a-card :bordered="false" :loading="loading">
      <template v-if="$slots.extra" #extra>
        <slot name="extra" />
      </template>

      <a-table
        v-bind="$attrs"
        :columns="columns"
        :data-source="dataSource"
        :pagination="paginationConfig"
        :loading="loading"
        @change="handleTableChange"
      >
        <template
          v-for="(_, name) in $slots"
          #[name]="slotData"
          :key="name"
        >
          <slot
            v-if="name !== 'toolbar' && name !== 'extra'"
            :name="name"
            v-bind="slotData"
          />
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { TableProps, TablePaginationConfig, TableColumnType } from 'ant-design-vue';

interface Props {
  columns: TableColumnType[];
  dataSource: TableProps['dataSource'];
  loading?: boolean;
  pagination?: false | TablePaginationConfig;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  pagination: () => ({
    current: 1,
    pageSize: 10,
    total: 0,
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }),
});

const emit = defineEmits<{
  (e: 'change', pagination: TablePaginationConfig): void;
}>();

// 计算分页配置
const paginationConfig = computed(() => {
  if (props.pagination === false) {
    return false;
  }
  return {
    ...props.pagination,
  };
});

// 处理表格变化
const handleTableChange = (pagination: TablePaginationConfig) => {
  emit('change', pagination);
};
</script>

<style lang="scss" scoped>
.pro-table {
  .toolbar {
    margin-bottom: 16px;
  }

  :deep(.ant-card-body) {
    padding: 0;
  }

  :deep(.ant-table-wrapper) {
    .ant-table-title {
      padding: 16px 24px;
      border-bottom: 1px solid #f0f0f0;
    }

    .ant-table-container {
      border-bottom: 1px solid #f0f0f0;
    }

    .ant-table-content {
      overflow-x: auto;
    }

    .ant-table-thead > tr > th {
      white-space: nowrap;
    }

    .ant-table-tbody > tr > td {
      > .ant-typography {
        margin-bottom: 0;
      }
    }

    .ant-table-pagination {
      margin: 16px 24px;
    }
  }
}
</style>