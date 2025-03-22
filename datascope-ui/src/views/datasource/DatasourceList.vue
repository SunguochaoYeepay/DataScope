<template>
  <div class="datasource-list">
    <!-- 操作栏 -->
    <div class="operation-bar">
      <a-space>
        <a-input-search
          v-model:value="queryParams.keyword"
          placeholder="搜索数据源"
          style="width: 250px"
          @search="handleSearch"
        />
        <a-select
          v-model:value="queryParams.type"
          placeholder="数据源类型"
          style="width: 150px"
          allowClear
          @change="handleSearch"
        >
          <a-select-option value="MySQL">MySQL</a-select-option>
          <a-select-option value="DB2">DB2</a-select-option>
        </a-select>
        <a-select
          v-model:value="queryParams.status"
          placeholder="状态"
          style="width: 150px"
          allowClear
          @change="handleSearch"
        >
          <a-select-option value="active">正常</a-select-option>
          <a-select-option value="inactive">未启用</a-select-option>
          <a-select-option value="error">异常</a-select-option>
        </a-select>
      </a-space>
      <a-button type="primary" @click="handleCreate">
        <template #icon><PlusOutlined /></template>
        新建数据源
      </a-button>
    </div>

    <!-- 数据表格 -->
    <a-table
      :columns="columns"
      :data-source="datasourceStore.list"
      :loading="datasourceStore.loading"
      :pagination="{
        total: datasourceStore.total,
        current: datasourceStore.current,
        pageSize: datasourceStore.pageSize,
        showSizeChanger: true,
        showQuickJumper: true,
      }"
      @change="handleTableChange"
      row-key="id"
    >
      <!-- 数据源类型 -->
      <template #type="{ text }">
        <a-tag :color="text === 'MySQL' ? 'blue' : 'purple'">{{ text }}</a-tag>
      </template>

      <!-- 状态 -->
      <template #status="{ text }">
        <a-tag :color="getStatusColor(text)">{{ getStatusText(text) }}</a-tag>
      </template>

      <!-- 操作 -->
      <template #action="{ record }">
        <a-space>
          <a-button type="link" size="small" @click="handleEdit(record)">
            编辑
          </a-button>
          <a-button type="link" size="small" @click="handleTest(record)">
            测试连接
          </a-button>
          <a-button type="link" size="small" @click="handleSync(record)">
            同步元数据
          </a-button>
          <a-popconfirm
            title="确定要删除该数据源吗？"
            @confirm="handleDelete(record)"
          >
            <a-button type="link" size="small" danger>删除</a-button>
          </a-popconfirm>
        </a-space>
      </template>
    </a-table>

    <!-- 测试连接结果弹窗 -->
    <a-modal
      v-model:open="testModalVisible"
      title="测试连接结果"
      :footer="null"
      width="500px"
    >
      <a-result
        :status="testResult.success ? 'success' : 'error'"
        :title="testResult.success ? '连接成功' : '连接失败'"
        :sub-title="testResult.message"
      >
        <template #extra>
          <a-button type="primary" @click="testModalVisible = false">
            确定
          </a-button>
        </template>
      </a-result>
      <div v-if="testResult.details" class="test-details">
        <pre>{{ testResult.details }}</pre>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { PlusOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { TableProps } from 'ant-design-vue';
import { useDataSourceStore } from '@/stores';

// 数据源类型
type DataSourceType = 'MySQL' | 'DB2';

// 数据源状态
type DataSourceStatus = 'active' | 'inactive' | 'error';

// 数据源信息
interface DataSource {
  id: string;
  name: string;
  type: DataSourceType;
  host: string;
  port: number;
  database: string;
  username: string;
  status: DataSourceStatus;
  updatedAt: string;
}

// 查询参数
interface DataSourceQueryParams {
  keyword?: string;
  type?: DataSourceType;
  status?: DataSourceStatus;
  pageSize: number;
  current: number;
}

// 测试连接结果
interface TestConnectionResult {
  success: boolean;
  message: string;
  details?: string;
}

const router = useRouter();
const datasourceStore = useDataSourceStore();

// 查询参数
const queryParams = reactive<DataSourceQueryParams>({
  keyword: '',
  type: undefined,
  status: undefined,
  pageSize: 10,
  current: 1,
});

// 表格列定义
const columns = [
  {
    title: '数据源名称',
    dataIndex: 'name',
    key: 'name',
    ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    slots: { customRender: 'type' },
    width: 100,
  },
  {
    title: '主机',
    dataIndex: 'host',
    key: 'host',
    ellipsis: true,
  },
  {
    title: '数据库',
    dataIndex: 'database',
    key: 'database',
    ellipsis: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    slots: { customRender: 'status' },
    width: 100,
  },
  {
    title: '更新时间',
    dataIndex: 'updatedAt',
    key: 'updatedAt',
    width: 170,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 280,
    fixed: 'right',
  },
];

// 测试连接相关
const testModalVisible = ref(false);
const testResult = ref<TestConnectionResult>({
  success: false,
  message: '',
});

// 获取状态颜色
const getStatusColor = (status: DataSourceStatus) => {
  const colors: Record<DataSourceStatus, string> = {
    active: 'success',
    inactive: 'default',
    error: 'error',
  };
  return colors[status];
};

// 获取状态文本
const getStatusText = (status: DataSourceStatus) => {
  const texts: Record<DataSourceStatus, string> = {
    active: '正常',
    inactive: '未启用',
    error: '异常',
  };
  return texts[status];
};

// 搜索
const handleSearch = () => {
  queryParams.current = 1;
  fetchList();
};

// 表格变化
const handleTableChange: TableProps['onChange'] = (pagination) => {
  if (pagination.current) {
    queryParams.current = pagination.current;
  }
  if (pagination.pageSize) {
    queryParams.pageSize = pagination.pageSize;
  }
  fetchList();
};

// 获取列表数据
const fetchList = () => {
  datasourceStore.fetchList(queryParams);
};

// 新建数据源
const handleCreate = () => {
  router.push({ name: 'datasource-create' });
};

// 编辑数据源
const handleEdit = (record: DataSource) => {
  router.push({
    name: 'datasource-edit',
    params: { id: record.id },
  });
};

// 测试连接
const handleTest = async (record: DataSource) => {
  try {
    testResult.value = await datasourceStore.testConnection({
      type: record.type,
      host: record.host,
      port: record.port,
      database: record.database,
      username: record.username,
      password: '', // 需要用户重新输入密码
    });
    testModalVisible.value = true;
  } catch (error: any) {
    message.error(error.message || '测试连接失败');
  }
};

// 同步元数据
const handleSync = async (record: DataSource) => {
  try {
    await datasourceStore.syncMetadata(record.id);
    message.success('同步成功');
    fetchList(); // 刷新列表
  } catch (error: any) {
    message.error(error.message || '同步失败');
  }
};

// 删除数据源
const handleDelete = async (record: DataSource) => {
  try {
    await datasourceStore.delete(record.id);
    message.success('删除成功');
    fetchList(); // 刷新列表
  } catch (error: any) {
    message.error(error.message || '删除失败');
  }
};

onMounted(() => {
  fetchList();
});
</script>

<style lang="scss" scoped>
.datasource-list {
  .operation-bar {
    margin-bottom: 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .test-details {
    margin-top: 16px;
    padding: 16px;
    background: #f5f5f5;
    border-radius: 4px;

    pre {
      margin: 0;
      white-space: pre-wrap;
      word-wrap: break-word;
    }
  }
}
</style>