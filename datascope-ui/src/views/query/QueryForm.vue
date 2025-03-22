<template>
  <div class="query-form">
    <a-card
      :title="isEdit ? '编辑查询' : '新建查询'"
      :loading="loading"
    >
      <template #extra>
        <a-space>
          <a-button @click="handleCancel">取消</a-button>
          <a-button
            type="primary"
            :loading="submitting"
            @click="handleSubmit"
          >
            保存
          </a-button>
        </a-space>
      </template>

      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        layout="vertical"
      >
        <!-- 基本信息 -->
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="查询名称"
              name="name"
              required
            >
              <a-input
                v-model:value="formState.name"
                placeholder="请输入查询名称"
                :maxLength="50"
                showCount
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              label="数据源"
              name="dataSourceId"
              required
            >
              <a-select
                v-model:value="formState.dataSourceId"
                placeholder="请选择数据源"
                @change="handleDataSourceChange"
              >
                <a-select-option
                  v-for="ds in datasourceStore.list"
                  :key="ds.id"
                  :value="ds.id"
                >
                  {{ ds.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item
          label="描述"
          name="description"
        >
          <a-textarea
            v-model:value="formState.description"
            placeholder="请输入查询描述"
            :maxLength="200"
            :rows="2"
            showCount
          />
        </a-form-item>

        <!-- SQL编辑器 -->
        <a-form-item
          label="SQL语句"
          name="sql"
          required
        >
          <div class="sql-editor">
            <MonacoEditor
              v-model:value="formState.sql"
              language="sql"
              :options="{
                minimap: { enabled: false },
                lineNumbers: 'on',
                scrollBeyondLastLine: false,
                automaticLayout: true,
                tabSize: 2,
                theme: 'vs',
                fontSize: 14,
                wordWrap: 'on',
                suggestOnTriggerCharacters: true,
                formatOnPaste: true,
                formatOnType: true,
              }"
              @change="handleSqlChange"
              height="300"
            />
          </div>
        </a-form-item>

        <!-- 查询配置 -->
        <a-collapse v-model:activeKey="activeKeys">
          <!-- 查询条件配置 -->
          <a-collapse-panel key="conditions" header="查询条件配置">
            <div class="config-section">
              <div class="config-header">
                <span>条件列表</span>
                <a-button
                  type="link"
                  @click="handleAddCondition"
                >
                  <template #icon><PlusOutlined /></template>
                  添加条件
                </a-button>
              </div>
              <a-table
                :columns="conditionColumns"
                :data-source="formState.config.conditions"
                :pagination="false"
                size="small"
              >
                <!-- 字段 -->
                <template #field="{ text, record }">
                  <a-select
                    v-model:value="record.field"
                    style="width: 100%"
                    placeholder="请选择字段"
                  >
                    <a-select-option
                      v-for="field in availableFields"
                      :key="field.name"
                      :value="field.name"
                    >
                      {{ field.label || field.name }}
                    </a-select-option>
                  </a-select>
                </template>

                <!-- 标签 -->
                <template #label="{ text, record }">
                  <a-input
                    v-model:value="record.label"
                    placeholder="请输入标签"
                  />
                </template>

                <!-- 类型 -->
                <template #type="{ text, record }">
                  <a-select
                    v-model:value="record.type"
                    style="width: 100%"
                  >
                    <a-select-option value="input">输入框</a-select-option>
                    <a-select-option value="select">下拉框</a-select-option>
                    <a-select-option value="date">日期选择器</a-select-option>
                    <a-select-option value="datetime">日期时间选择器</a-select-option>
                    <a-select-option value="number">数字输入框</a-select-option>
                  </a-select>
                </template>

                <!-- 必填 -->
                <template #required="{ text, record }">
                  <a-switch v-model:checked="record.required" />
                </template>

                <!-- 默认值 -->
                <template #defaultValue="{ text, record }">
                  <a-input
                    v-model:value="record.defaultValue"
                    placeholder="请输入默认值"
                  />
                </template>

                <!-- 隐藏 -->
                <template #hidden="{ text, record }">
                  <a-switch v-model:checked="record.hidden" />
                </template>

                <!-- 排序 -->
                <template #order="{ text, record }">
                  <a-input-number
                    v-model:value="record.order"
                    :min="0"
                    :max="100"
                  />
                </template>

                <!-- 操作 -->
                <template #action="{ record, index }">
                  <a-space>
                    <a-button
                      type="link"
                      danger
                      size="small"
                      @click="handleRemoveCondition(index)"
                    >
                      删除
                    </a-button>
                  </a-space>
                </template>
              </a-table>
            </div>
          </a-collapse-panel>

          <!-- 结果列配置 -->
          <a-collapse-panel key="columns" header="结果列配置">
            <div class="config-section">
              <div class="config-header">
                <span>列表列</span>
                <a-button
                  type="link"
                  @click="handleAddColumn"
                >
                  <template #icon><PlusOutlined /></template>
                  添加列
                </a-button>
              </div>
              <a-table
                :columns="columnColumns"
                :data-source="formState.config.columns"
                :pagination="false"
                size="small"
              >
                <!-- 字段 -->
                <template #field="{ text, record }">
                  <a-select
                    v-model:value="record.field"
                    style="width: 100%"
                    placeholder="请选择字段"
                  >
                    <a-select-option
                      v-for="field in availableFields"
                      :key="field.name"
                      :value="field.name"
                    >
                      {{ field.label || field.name }}
                    </a-select-option>
                  </a-select>
                </template>

                <!-- 标签 -->
                <template #label="{ text, record }">
                  <a-input
                    v-model:value="record.label"
                    placeholder="请输入标签"
                  />
                </template>

                <!-- 类型 -->
                <template #type="{ text, record }">
                  <a-select
                    v-model:value="record.type"
                    style="width: 100%"
                  >
                    <a-select-option value="text">文本</a-select-option>
                    <a-select-option value="number">数字</a-select-option>
                    <a-select-option value="date">日期</a-select-option>
                    <a-select-option value="datetime">日期时间</a-select-option>
                    <a-select-option value="boolean">布尔值</a-select-option>
                  </a-select>
                </template>

                <!-- 宽度 -->
                <template #width="{ text, record }">
                  <a-input-number
                    v-model:value="record.width"
                    :min="0"
                    :max="1000"
                  />
                </template>

                <!-- 固定 -->
                <template #fixed="{ text, record }">
                  <a-select
                    v-model:value="record.fixed"
                    style="width: 100%"
                    allowClear
                  >
                    <a-select-option value="left">左侧</a-select-option>
                    <a-select-option value="right">右侧</a-select-option>
                  </a-select>
                </template>

                <!-- 排序 -->
                <template #sortable="{ text, record }">
                  <a-switch v-model:checked="record.sortable" />
                </template>

                <!-- 隐藏 -->
                <template #hidden="{ text, record }">
                  <a-switch v-model:checked="record.hidden" />
                </template>

                <!-- 格式化 -->
                <template #format="{ text, record }">
                  <a-input
                    v-model:value="record.format"
                    placeholder="请输入格式化表达式"
                  />
                </template>

                <!-- 掩码 -->
                <template #mask="{ text, record }">
                  <a-switch v-model:checked="record.mask" />
                </template>

                <!-- 操作 -->
                <template #action="{ record, index }">
                  <a-space>
                    <a-button
                      type="link"
                      danger
                      size="small"
                      @click="handleRemoveColumn(index)"
                    >
                      删除
                    </a-button>
                  </a-space>
                </template>
              </a-table>
            </div>
          </a-collapse-panel>

          <!-- 排序配置 -->
          <a-collapse-panel key="sorts" header="排序配置">
            <div class="config-section">
              <div class="config-header">
                <span>排序规则</span>
                <a-button
                  type="link"
                  @click="handleAddSort"
                >
                  <template #icon><PlusOutlined /></template>
                  添加排序
                </a-button>
              </div>
              <a-table
                :columns="sortColumns"
                :data-source="formState.config.sorts"
                :pagination="false"
                size="small"
              >
                <!-- 字段 -->
                <template #field="{ text, record }">
                  <a-select
                    v-model:value="record.field"
                    style="width: 100%"
                    placeholder="请选择字段"
                  >
                    <a-select-option
                      v-for="field in availableFields"
                      :key="field.name"
                      :value="field.name"
                    >
                      {{ field.label || field.name }}
                    </a-select-option>
                  </a-select>
                </template>

                <!-- 排序方式 -->
                <template #order="{ text, record }">
                  <a-select
                    v-model:value="record.order"
                    style="width: 100%"
                  >
                    <a-select-option value="ascend">升序</a-select-option>
                    <a-select-option value="descend">降序</a-select-option>
                  </a-select>
                </template>

                <!-- 操作 -->
                <template #action="{ record, index }">
                  <a-space>
                    <a-button
                      type="link"
                      danger
                      size="small"
                      @click="handleRemoveSort(index)"
                    >
                      删除
                    </a-button>
                  </a-space>
                </template>
              </a-table>
            </div>
          </a-collapse-panel>

          <!-- 分页配置 -->
          <a-collapse-panel key="pagination" header="分页配置">
            <div class="config-section">
              <a-form layout="vertical">
                <a-form-item label="启用分页">
                  <a-switch
                    v-model:checked="formState.config.pagination.enabled"
                  />
                </a-form-item>
                <a-form-item
                  v-if="formState.config.pagination.enabled"
                  label="每页条数"
                >
                  <a-input-number
                    v-model:value="formState.config.pagination.pageSize"
                    :min="1"
                    :max="1000"
                  />
                </a-form-item>
              </a-form>
            </div>
          </a-collapse-panel>
        </a-collapse>
      </a-form>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { FormInstance } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import MonacoEditor from '@/components/MonacoEditor.vue';
import { useQueryStore, useDataSourceStore } from '@/stores';

// 查询条件类型
type QueryConditionType = 'input' | 'select' | 'date' | 'datetime' | 'number';

// 查询列类型
type QueryColumnType = 'text' | 'number' | 'date' | 'datetime' | 'boolean';

// 查询条件
interface QueryCondition {
  field: string;
  label: string;
  type: QueryConditionType;
  required: boolean;
  defaultValue?: string;
  hidden: boolean;
  order: number;
}

// 查询列
interface QueryColumn {
  field: string;
  label: string;
  type: QueryColumnType;
  width?: number;
  fixed?: 'left' | 'right';
  sortable: boolean;
  hidden: boolean;
  format?: string;
  mask: boolean;
}

// 排序规则
interface QuerySort {
  field: string;
  order: 'ascend' | 'descend';
}

// 分页配置
interface QueryPagination {
  enabled: boolean;
  pageSize: number;
}

// 查询配置
interface QueryConfig {
  conditions: QueryCondition[];
  columns: QueryColumn[];
  sorts: QuerySort[];
  pagination: QueryPagination;
}

// 创建/更新查询参数
interface QueryParams {
  name: string;
  description?: string;
  dataSourceId: string;
  sql: string;
  config: QueryConfig;
}

// 可用字段
interface AvailableField {
  name: string;
  label: string;
  type: string;
}

const router = useRouter();
const route = useRoute();
const queryStore = useQueryStore();
const datasourceStore = useDataSourceStore();

// 是否编辑模式
const isEdit = computed(() => !!route.params.id);

// 加载状态
const loading = ref(false);
const submitting = ref(false);

// 表单实例
const formRef = ref<FormInstance>();

// 折叠面板激活的key
const activeKeys = ref<string[]>(['conditions', 'columns']);

// 可用字段列表
const availableFields = ref<AvailableField[]>([]);

// 表单数据
const formState = reactive<QueryParams>({
  name: '',
  description: '',
  dataSourceId: '',
  sql: '',
  config: {
    conditions: [],
    columns: [],
    sorts: [],
    pagination: {
      enabled: true,
      pageSize: 20,
    },
  },
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入查询名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\-\u4e00-\u9fa5]+$/, message: '只能包含字母、数字、下划线、中划线和中文', trigger: 'blur' },
  ],
  dataSourceId: [
    { required: true, message: '请选择数据源', trigger: 'change' },
  ],
  sql: [
    { required: true, message: '请输入SQL语句', trigger: 'blur' },
  ],
  description: [
    { max: 200, message: '长度不能超过200个字符', trigger: 'blur' },
  ],
};

// 条件列定义
const conditionColumns = [
  {
    title: '字段',
    dataIndex: 'field',
    key: 'field',
    slots: { customRender: 'field' },
    width: 200,
  },
  {
    title: '标签',
    dataIndex: 'label',
    key: 'label',
    slots: { customRender: 'label' },
    width: 150,
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    slots: { customRender: 'type' },
    width: 150,
  },
  {
    title: '必填',
    dataIndex: 'required',
    key: 'required',
    slots: { customRender: 'required' },
    width: 80,
  },
  {
    title: '默认值',
    dataIndex: 'defaultValue',
    key: 'defaultValue',
    slots: { customRender: 'defaultValue' },
    width: 150,
  },
  {
    title: '隐藏',
    dataIndex: 'hidden',
    key: 'hidden',
    slots: { customRender: 'hidden' },
    width: 80,
  },
  {
    title: '排序',
    dataIndex: 'order',
    key: 'order',
    slots: { customRender: 'order' },
    width: 100,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 100,
    fixed: 'right',
  },
];

// 列定义
const columnColumns = [
  {
    title: '字段',
    dataIndex: 'field',
    key: 'field',
    slots: { customRender: 'field' },
    width: 200,
  },
  {
    title: '标签',
    dataIndex: 'label',
    key: 'label',
    slots: { customRender: 'label' },
    width: 150,
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    slots: { customRender: 'type' },
    width: 150,
  },
  {
    title: '宽度',
    dataIndex: 'width',
    key: 'width',
    slots: { customRender: 'width' },
    width: 100,
  },
  {
    title: '固定',
    dataIndex: 'fixed',
    key: 'fixed',
    slots: { customRender: 'fixed' },
    width: 100,
  },
  {
    title: '排序',
    dataIndex: 'sortable',
    key: 'sortable',
    slots: { customRender: 'sortable' },
    width: 80,
  },
  {
    title: '隐藏',
    dataIndex: 'hidden',
    key: 'hidden',
    slots: { customRender: 'hidden' },
    width: 80,
  },
  {
    title: '格式化',
    dataIndex: 'format',
    key: 'format',
    slots: { customRender: 'format' },
    width: 150,
  },
  {
    title: '掩码',
    dataIndex: 'mask',
    key: 'mask',
    slots: { customRender: 'mask' },
    width: 80,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 100,
    fixed: 'right',
  },
];

// 排序列定义
const sortColumns = [
  {
    title: '字段',
    dataIndex: 'field',
    key: 'field',
    slots: { customRender: 'field' },
    width: 200,
  },
  {
    title: '排序方式',
    dataIndex: 'order',
    key: 'order',
    slots: { customRender: 'order' },
    width: 150,
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
    width: 100,
  },
];

// 获取查询详情
const fetchDetail = async () => {
  if (!isEdit.value) return;
  
  loading.value = true;
  try {
    const data = await queryStore.fetchDetail(route.params.id as string);
    Object.assign(formState, {
      name: data.name,
      description: data.description,
      dataSourceId: data.dataSourceId,
      sql: data.sql,
      config: data.config,
    });
  } catch (error: any) {
    message.error(error.message || '获取查询详情失败');
    router.back();
  } finally {
    loading.value = false;
  }
};

// 获取数据源列表
const fetchDataSources = async () => {
  try {
    await datasourceStore.fetchList();
  } catch (error: any) {
    message.error(error.message || '获取数据源列表失败');
  }
};

// 数据源变更
const handleDataSourceChange = async () => {
  if (!formState.dataSourceId) return;

  try {
    // TODO: 获取数据源的字段列表
    const fields = await queryStore.fetchFields(formState.dataSourceId);
    availableFields.value = fields;
  } catch (error: any) {
    message.error(error.message || '获取字段列表失败');
  }
};

// SQL变更
const handleSqlChange = async () => {
  if (!formState.dataSourceId || !formState.sql) return;

  try {
    // TODO: 解析SQL获取字段列表
    const fields = await queryStore.parseSql({
      dataSourceId: formState.dataSourceId,
      sql: formState.sql,
    });
    availableFields.value = fields;
  } catch (error: any) {
    message.error(error.message || '解析SQL失败');
  }
};

// 添加条件
const handleAddCondition = () => {
  formState.config.conditions.push({
    field: '',
    label: '',
    type: 'input',
    required: false,
    hidden: false,
    order: formState.config.conditions.length,
  });
};

// 移除条件
const handleRemoveCondition = (index: number) => {
  formState.config.conditions.splice(index, 1);
  // 重新计算排序
  formState.config.conditions.forEach((item, i) => {
    item.order = i;
  });
};

// 添加列
const handleAddColumn = () => {
  formState.config.columns.push({
    field: '',
    label: '',
    type: 'text',
    width: 150,
    sortable: false,
    hidden: false,
    mask: false,
  });
};

// 移除列
const handleRemoveColumn = (index: number) => {
  formState.config.columns.splice(index, 1);
};

// 添加排序
const handleAddSort = () => {
  formState.config.sorts.push({
    field: '',
    order: 'ascend',
  });
};

// 移除排序
const handleRemoveSort = (index: number) => {
  formState.config.sorts.splice(index, 1);
};

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    return;
  }

  submitting.value = true;
  try {
    if (isEdit.value) {
      await queryStore.update({
        id: route.params.id as string,
        ...formState,
      });
      message.success('更新成功');
    } else {
      await queryStore.create(formState);
      message.success('创建成功');
    }
    router.push({ name: 'query' });
  } catch (error: any) {
    message.error(error.message || (isEdit.value ? '更新失败' : '创建失败'));
  } finally {
    submitting.value = false;
  }
};

// 取消
const handleCancel = () => {
  router.back();
};

onMounted(() => {
  fetchDetail();
  fetchDataSources();
});
</script>

<style lang="scss" scoped>
.query-form {
  .sql-editor {
    border: 1px solid #d9d9d9;
    border-radius: 2px;
    overflow: hidden;

    :deep(.monaco-editor) {
      .margin {
        background: #f5f5f5;
      }
    }
  }

  .config-section {
    .config-header {
      margin-bottom: 16px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      span {
        font-weight: 500;
        color: rgba(0, 0, 0, 0.85);
      }
    }

    :deep(.ant-table) {
      .ant-table-cell {
        padding: 8px;
      }

      .ant-form-item {
        margin-bottom: 0;
      }
    }
  }

  :deep(.ant-collapse) {
    background: #fff;

    .ant-collapse-content-box {
      padding: 16px !important;
    }

    .ant-collapse-header {
      font-weight: 500;
    }
  }
}
</style>