<template>
  <page-container title="SQL编辑器" subtitle="执行和分析SQL查询">
    <a-row :gutter="[16, 16]">
      <a-col :span="24">
        <a-card>
          <a-form layout="inline" :model="formState">
            <a-form-item label="数据源">
              <a-select
                v-model:value="formState.dataSourceId"
                style="width: 200px"
                placeholder="请选择数据源"
                @change="handleDataSourceChange"
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
          </a-form>
        </a-card>
      </a-col>

      <a-col :span="16">
        <a-card>
          <sql-editor
            v-model:value="sql"
            :loading="loading"
            :error="error"
            :execution-time="executionTime"
            @execute="handleExecute"
            @format="handleFormat"
          />
        </a-card>
      </a-col>

      <a-col :span="8">
        <a-card title="数据库表">
          <a-tree
            v-if="sqlParserStore.tables.length"
            :tree-data="treeData"
            :field-names="{
              children: 'children',
              title: 'title',
              key: 'key',
            }"
            @select="handleTreeSelect"
          />
          <a-empty v-else description="请选择数据源" />
        </a-card>
      </a-col>

      <a-col :span="24">
        <a-card title="查询结果">
          <template #extra>
            <a-space>
              <span v-if="executionTime">
                执行时间: {{ executionTime }}ms
              </span>
              <a-button
                type="primary"
                :disabled="!results.length"
                @click="handleExport"
              >
                导出
              </a-button>
            </a-space>
          </template>

          <a-table
            :columns="resultColumns"
            :data-source="results"
            :loading="loading"
            :scroll="{ x: true }"
            size="small"
          />
        </a-card>
      </a-col>
    </a-row>

    <a-modal
      v-model:open="tableInfoVisible"
      title="表信息"
      width="800px"
      :footer="null"
    >
      <a-table
        :columns="columnInfoColumns"
        :data-source="sqlParserStore.currentTable?.columns || []"
        size="small"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'tags'">
            <a-space>
              <a-tag v-if="record.primaryKey" color="blue">主键</a-tag>
              <a-tag v-if="!record.nullable" color="red">非空</a-tag>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-modal>
  </page-container>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { useDatasourceStore } from '@/stores/datasource';
import { useSqlParserStore } from '@/stores/sql-parser';
import type { DataNode } from 'ant-design-vue/es/tree';
import PageContainer from '@/components/PageContainer.vue';
import SqlEditor from '@/components/SqlEditor.vue';

const datasourceStore = useDatasourceStore();
const sqlParserStore = useSqlParserStore();

const formState = reactive({
  dataSourceId: undefined as string | undefined,
});

const sql = ref('');
const loading = ref(false);
const error = ref('');
const executionTime = ref<number>();
const results = ref<any[]>([]);
const resultColumns = ref<any[]>([]);
const tableInfoVisible = ref(false);
const selectedTable = ref<string>();

const columnInfoColumns = [
  {
    title: '列名',
    dataIndex: 'name',
    key: 'name',
    width: 200,
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    width: 150,
  },
  {
    title: '标签',
    key: 'tags',
    width: 150,
  },
  {
    title: '注释',
    dataIndex: 'comment',
    key: 'comment',
  },
];

const treeData = computed(() => {
  return sqlParserStore.tables.map((table) => ({
    title: table,
    key: table,
    isLeaf: true,
  }));
});

const handleDataSourceChange = async (value: string) => {
  if (value) {
    await sqlParserStore.fetchTables(value);
  }
};

const handleTreeSelect = async (selectedKeys: string[]) => {
  if (selectedKeys.length && formState.dataSourceId) {
    selectedTable.value = selectedKeys[0];
    await sqlParserStore.fetchTableInfo(formState.dataSourceId, selectedKeys[0]);
    tableInfoVisible.value = true;
  }
};

const handleExecute = async () => {
  if (!formState.dataSourceId) {
    message.error('请先选择数据源');
    return;
  }

  if (!sql.value.trim()) {
    message.error('请输入SQL语句');
    return;
  }

  loading.value = true;
  error.value = '';
  executionTime.value = undefined;

  try {
    const startTime = Date.now();
    const parseResult = await sqlParserStore.parseSql({
      sql: sql.value,
      dataSourceId: formState.dataSourceId,
    });

    if (parseResult?.isValid) {
      // TODO: 执行SQL查询
      // const response = await executeQuery({
      //   sql: sql.value,
      //   dataSourceId: formState.dataSourceId,
      // });
      
      // results.value = response.data;
      // resultColumns.value = Object.keys(response.data[0] || {}).map((key) => ({
      //   title: key,
      //   dataIndex: key,
      //   key,
      // }));
      
      executionTime.value = Date.now() - startTime;
    } else {
      error.value = parseResult?.errorMessage || '解析SQL失败';
    }
  } catch (err: any) {
    error.value = err.message || '执行失败';
  } finally {
    loading.value = false;
  }
};

const handleFormat = () => {
  // TODO: 实现SQL格式化
  message.info('SQL格式化功能开发中');
};

const handleExport = () => {
  // TODO: 实现导出功能
  message.info('导出功能开发中');
};

onMounted(async () => {
  await datasourceStore.fetchDatasources({
    page: 0,
    size: 100,
  });
});
</script>