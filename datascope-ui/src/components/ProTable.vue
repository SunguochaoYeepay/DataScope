<template>
  <div class="pro-table">
    <div class="pro-table-toolbar">
      <div class="pro-table-toolbar-left">
        <slot name="toolbar-left" />
      </div>
      <div class="pro-table-toolbar-right">
        <slot name="toolbar-right" />
      </div>
    </div>
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :pagination="pagination"
      :loading="loading"
      :row-key="rowKey"
      :scroll="scroll"
      @change="handleTableChange"
    >
      <template #headerCell="{ column }">
        <slot name="headerCell" :column="column">
          {{ column.title }}
        </slot>
      </template>
      <template #bodyCell="{ column, record }">
        <slot name="bodyCell" :column="column" :record="record">
          {{ record[column.dataIndex] }}
        </slot>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import type { TableProps, TablePaginationConfig, TableColumnType } from 'ant-design-vue'

interface Props {
  columns: TableColumnType[]
  dataSource: TableProps['dataSource']
  loading?: boolean
  pagination?: false | TablePaginationConfig
  rowKey?: string | ((record: any) => string)
  scroll?: {
    x?: number | true | string
    y?: number | string
  }
}

interface Emits {
  (e: 'change', pagination: TablePaginationConfig): void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  rowKey: 'id',
})

const emit = defineEmits<Emits>()

const handleTableChange = (pagination: TablePaginationConfig) => {
  emit('change', pagination)
}
</script>

<style lang="scss" scoped>
.pro-table {
  background: #fff;
  padding: 24px;
  border-radius: 2px;

  .pro-table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  :deep(.ant-table-wrapper) {
    .ant-table-title {
      padding: 0;
      margin-bottom: 16px;
    }

    .ant-table-container {
      border-radius: 2px;
    }

    .ant-table-content {
      overflow-x: auto;
    }

    .ant-table-thead > tr > th {
      background: #fafafa;
    }

    .ant-table-tbody > tr > td {
      &.ant-table-cell-fix-left,
      &.ant-table-cell-fix-right {
        z-index: 1;
      }
    }

    .ant-table-pagination {
      margin: 16px 0 0;
    }
  }
}
</style>