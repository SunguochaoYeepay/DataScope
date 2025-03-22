<template>
  <div class="query-list">
    <a-card :loading="loading">
      <!-- 操作栏 -->
      <div class="operation-bar">
        <a-space>
          <a-input-search
            v-model:value="searchParams.keyword"
            placeholder="请输入查询名称"
            style="width: 200px"
            @search="handleSearch"
          />
          <a-select
            v-model:value="searchParams.status"
            style="width: 150px"
            placeholder="请选择状态"
            allowClear
            @change="handleSearch"
          >
            <a-select-option value="draft">草稿</a-select-option>
            <a-select-option value="published">已发布</a-select-option>
            <a-select-option value="archived">已归档</a-select-option>
          </a-select>
          <a-select
            v-model:value="searchParams.dataSourceId"
            style="width: 200px"
            placeholder="请选择数据源"
            allowClear
            @change="handleSearch"
          >
            <a-select-option
              v-for="ds in datasourceStore.list"
              :key="ds.id"
              :value="ds.id"
            >
              {{ ds.name }}
            </a-select-option>
          </a-select>
        </a-space>

        <a-button type="primary" @click="handleCreate">
          <template #icon><PlusOutlined /></template>
          新建查询
        </a-button>
      </div>

      <!-- 查询列表 -->
      <a-table
        :columns="columns"
        :data-source="queryStore.list"
        :loading="loading"
        :pagination="{
          total: queryStore.total,
          current: searchParams.page,
          pageSize: searchParams.pageSize,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: total => `共 ${total} 条记录`,
        }"
        :scroll="{ x: 1200 }"
        @change="handleTableChange"
      >
        <!-- 查询名称 -->
        <template #name="{ text, record }">
          <a @click="handleExecute(record)">{{ text }}</a>
          <a-typography-paragraph
            v-if="record.description"
            type="secondary"
            :ellipsis="{ rows: 1 }"
          >
            {{ record.description }}
          </a-typography-paragraph>
        </template>

        <!-- 数据源 -->
        <template #dataSourceName="{ text }">
          <a-tag>{{ text }}</a-tag>
        </template>

        <!-- 状态 -->
        <template #status="{ text }">
          <a-tag :color="getStatusColor(text)">
            {{ getStatusText(text) }}
          </a-tag>
        </template>

        <!-- 创建时间 -->
        <template #createdAt="{ text }">
          {{ formatDateTime(text) }}
        </template>

        <!-- 更新时间 -->
        <template #updatedAt="{ text }">
          {{ formatDateTime(text) }}
        </template>

        <!-- 操作 -->
        <template #action="{ record }">
          <a-space>
            <!-- 执行按钮 -->
            <a-button
              type="link"
              size="small"
              @click="handleExecute(record)"
            >
              执行
            </a-button>

            <!-- 历史记录按钮 -->
            <a-button
              type="link"
              size="small"
              @click="handleViewHistory(record)"
            >
              历史记录
            </a-button>

            <!-- 编辑按钮 -->
            <a-button
              type="link"
              size="small"
              @click="handleEdit(record)"
              :disabled="record.status === 'archived'"
            >
              编辑
            </a-button>

            <!-- 更多操作 -->
            <a-dropdown>
              <a-button type="link" size="small">
                更多
                <DownOutlined />
              </a-button>
              <template #overlay>
                <a-menu>
                  <a-menu-item
                    v-if="record.status === 'draft'"
                    key="publish"
                    @click="handlePublish(record)"
                  >
                    发布
                  </a-menu-item>
                  <a-menu-item
                    v-if="record.status === 'published'"
                    key="archive"
                    @click="handleArchive(record)"
                  >
                    归档
                  </a-menu-item>
                  <a-menu-item
                    key="delete"
                    danger
                    @click="handleDelete(record)"
                  >
                    删除
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </template>
      </a-table>
    </a-card>

    <!-- 删除确认对话框 -->
    <a-modal
      v-model:open="deleteModalVisible"
      title="删除确认"
      :footer="null"
    >
      <p>确定要删除该查询吗？删除后将无法恢复。</p>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import type { TablePaginationConfig } from 'ant-design-vue';
import {
  SearchOutlined,
  PlusOutlined,
  DownOutlined,
} from '@ant-design/icons-vue';
import dayjs from 'dayjs';
import { useQueryStore, useDataSourceStore } from '@/stores';

// 查询状态类型
type QueryStatus = 'draft' | 'published' | 'archived';

// 查询列表项类型
interface QueryListItem {
  id: string;
  name: string;
  description?: string;
  dataSourceId: string;
  dataSourceName: string;
  status: QueryStatus;
  creator: string;
  createdAt: string;
  updatedAt: string;
}

// 搜索参数类型
interface SearchParams {
  keyword: string;
  status?: QueryStatus;
  dataSourceId?: string;
  page: number;
  pageSize: number;
}

const router = useRouter();
const queryStore = useQueryStore();
const datasourceStore = useDataSourceStore();

// 加载状态
const loading = ref(false);

// 搜索参数
const searchParams = reactive<SearchParams>({
  keyword: '',
  status: undefined,
  dataSourceId: undefined,
  page: 1,
  pageSize: 10,
});

// 表格列定义
const columns = [
  {
    title: '查询名称',
    dataIndex: 'name',
    key: 'name',
    slots: { customRender: 'name' },
    width: 300,
    fixed: 'left',
  },
  {
    title: '数据源',
    dataIndex: 'dataSourceName',
    key: 'dataSourceName',
    slots: { customRender: 'dataSourceName' },
    width: 150,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    slots: { customRender: 'status' },
    width: 100,
  },
  {
    title: '创建人',
    dataIndex: 'creator',
    key: 'creator',
    width: 120,
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt',
    key: 'createdAt',
    slots: { customRender: 'createdAt' },
    width: 180,
  },
  {
    title: '更新时间',
    dataIndex: 'updatedAt',
    key: 'updatedAt',
    slots: { customRender: 'updatedAt' },
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 250,
    fixed: 'right',
  },
];

// 删除相关
const deleteModalVisible = ref(false);
const deleting = ref(false);
const queryToDelete = ref<QueryListItem>();

// 获取数据源列表
const fetchDataSources = async () => {
  try {
    await datasourceStore.fetchList();
  } catch (error: any) {
    message.error(error.message || '获取数据源列表失败');
  }
};

// 获取查询列表
const fetchQueries = async () => {
  loading.value = true;
  try {
    await queryStore.fetchList(searchParams);
  } catch (error: any) {
    message.error(error.message || '获取查询列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  searchParams.page = 1;
  fetchQueries();
};

// 表格变化
const handleTableChange = (pagination: TablePaginationConfig) => {
  if (pagination.current) {
    searchParams.page = pagination.current;
  }
  if (pagination.pageSize) {
    searchParams.pageSize = pagination.pageSize;
  }
  fetchQueries();
};

// 新建查询
const handleCreate = () => {
  router.push({ name: 'query-create' });
};

// 执行查询
const handleExecute = (record: QueryListItem) => {
  router.push({
    name: 'query-execute',
    params: { id: record.id },
  });
};

// 查看历史记录
const handleViewHistory = (record: QueryListItem) => {
  router.push({
    name: 'query-history',
    query: { queryId: record.id },
  });
};

// 编辑查询
const handleEdit = (record: QueryListItem) => {
  if (record.status === 'archived') {
    message.warning('已归档的查询不能编辑');
    return;
  }
  router.push({
    name: 'query-edit',
    params: { id: record.id },
  });
};

// 发布查询
const handlePublish = async (record: QueryListItem) => {
  try {
    await queryStore.publish(record.id);
    message.success('发布成功');
    fetchQueries();
  } catch (error: any) {
    message.error(error.message || '发布失败');
  }
};

// 归档查询
const handleArchive = async (record: QueryListItem) => {
  try {
    await queryStore.archive(record.id);
    message.success('归档成功');
    fetchQueries();
  } catch (error: any) {
    message.error(error.message || '归档失败');
  }
};

// 删除查询
const handleDelete = (record: QueryListItem) => {
  queryToDelete.value = record;
  deleteModalVisible.value = true;
};

// 确认删除
const confirmDelete = async () => {
  if (!queryToDelete.value) return;
  
  deleting.value = true;
  try {
    await queryStore.delete(queryToDelete.value.id);
    message.success('删除成功');
    deleteModalVisible.value = false;
    fetchQueries();
  } catch (error: any) {
    message.error(error.message || '删除失败');
  } finally {
    deleting.value = false;
  }
};

// 获取状态颜色
const getStatusColor = (status: QueryStatus) => {
  const colors: Record<QueryStatus, string> = {
    draft: 'default',
    published: 'success',
    archived: 'warning',
  };
  return colors[status];
};

// 获取状态文本
const getStatusText = (status: QueryStatus) => {
  const texts: Record<QueryStatus, string> = {
    draft: '草稿',
    published: '已发布',
    archived: '已归档',
  };
  return texts[status];
};

// 格式化日期时间
const formatDateTime = (date: string | Date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss');
};

onMounted(() => {
  fetchDataSources();
  fetchQueries();
});
</script>

<style lang="scss" scoped>
.query-list {
  .operation-bar {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  :deep(.ant-table) {
    .ant-typography {
      margin-bottom: 0;
      margin-top: 4px;
      color: rgba(0, 0, 0, 0.45);
    }
  }

  :deep(.ant-table-cell) {
    .ant-space {
      flex-wrap: nowrap;
    }
  }
}
</style>