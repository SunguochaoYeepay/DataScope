<template>
  <div class="datasource-form">
    <a-card
      :title="isEdit ? '编辑数据源' : '新建数据源'"
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
        <a-divider>基本信息</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="数据源名称"
              name="name"
              required
            >
              <a-input
                v-model:value="formState.name"
                placeholder="请输入数据源名称"
                :maxLength="50"
                showCount
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              label="数据源类型"
              name="type"
              required
            >
              <a-select
                v-model:value="formState.type"
                placeholder="请选择数据源类型"
              >
                <a-select-option value="MySQL">MySQL</a-select-option>
                <a-select-option value="DB2">DB2</a-select-option>
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
            placeholder="请输入数据源描述"
            :maxLength="200"
            :rows="4"
            showCount
          />
        </a-form-item>

        <!-- 连接信息 -->
        <a-divider>连接信息</a-divider>
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="主机地址"
              name="host"
              required
            >
              <a-input
                v-model:value="formState.host"
                placeholder="请输入主机地址"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              label="端口"
              name="port"
              required
            >
              <a-input-number
                v-model:value="formState.port"
                placeholder="请输入端口"
                :min="1"
                :max="65535"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item
              label="数据库名"
              name="database"
              required
            >
              <a-input
                v-model:value="formState.database"
                placeholder="请输入数据库名"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item
              label="用户名"
              name="username"
              required
            >
              <a-input
                v-model:value="formState.username"
                placeholder="请输入用户名"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item
          label="密码"
          name="password"
          :required="!isEdit"
        >
          <a-input-password
            v-model:value="formState.password"
            placeholder="请输入密码"
            autocomplete="new-password"
          />
          <template #help>
            <span v-if="isEdit" class="help-text">如不修改密码请留空</span>
          </template>
        </a-form-item>

        <!-- 测试连接 -->
        <div class="form-footer">
          <a-button
            type="primary"
            ghost
            :loading="testing"
            @click="handleTest"
          >
            测试连接
          </a-button>
        </div>
      </a-form>
    </a-card>

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
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import type { FormInstance } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import { useDataSourceStore } from '@/stores';

// 数据源类型
type DataSourceType = 'MySQL' | 'DB2';

// 创建/更新数据源参数
interface DataSourceParams {
  name: string;
  type: DataSourceType;
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  description?: string;
}

// 测试连接结果
interface TestConnectionResult {
  success: boolean;
  message: string;
  details?: string;
}

const router = useRouter();
const route = useRoute();
const datasourceStore = useDataSourceStore();

// 是否编辑模式
const isEdit = computed(() => !!route.params.id);

// 加载状态
const loading = ref(false);
const submitting = ref(false);
const testing = ref(false);

// 表单实例
const formRef = ref<FormInstance>();

// 表单数据
const formState = reactive<DataSourceParams>({
  name: '',
  type: 'MySQL',
  host: '',
  port: 3306,
  database: '',
  username: '',
  password: '',
  description: '',
});

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入数据源名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\-\u4e00-\u9fa5]+$/, message: '只能包含字母、数字、下划线、中划线和中文', trigger: 'blur' },
  ],
  type: [
    { required: true, message: '请选择数据源类型', trigger: 'change' },
  ],
  host: [
    { required: true, message: '请输入主机地址', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9\-\.]+$/, message: '主机地址格式不正确', trigger: 'blur' },
  ],
  port: [
    { required: true, message: '请输入端口', trigger: 'blur' },
    { type: 'number', min: 1, max: 65535, message: '端口范围为1-65535', trigger: 'blur' },
  ],
  database: [
    { required: true, message: '请输入数据库名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\-]+$/, message: '只能包含字母、数字、下划线和中划线', trigger: 'blur' },
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: !isEdit.value, message: '请输入密码', trigger: 'blur' },
  ],
  description: [
    { max: 200, message: '长度不能超过200个字符', trigger: 'blur' },
  ],
};

// 测试连接相关
const testModalVisible = ref(false);
const testResult = ref<TestConnectionResult>({
  success: false,
  message: '',
});

// 获取数据源详情
const fetchDetail = async () => {
  if (!isEdit.value) return;
  
  loading.value = true;
  try {
    const data = await datasourceStore.fetchDetail(route.params.id as string);
    Object.assign(formState, {
      name: data.name,
      type: data.type,
      host: data.host,
      port: data.port,
      database: data.database,
      username: data.username,
      description: data.description,
    });
  } catch (error: any) {
    message.error(error.message || '获取数据源详情失败');
    router.back();
  } finally {
    loading.value = false;
  }
};

// 测试连接
const handleTest = async () => {
  try {
    await formRef.value?.validate(['type', 'host', 'port', 'database', 'username', 'password']);
  } catch {
    return;
  }

  testing.value = true;
  try {
    testResult.value = await datasourceStore.testConnection(formState);
    testModalVisible.value = true;
  } catch (error: any) {
    message.error(error.message || '测试连接失败');
  } finally {
    testing.value = false;
  }
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
      await datasourceStore.update({
        id: route.params.id as string,
        ...formState,
      });
      message.success('更新成功');
    } else {
      await datasourceStore.create(formState);
      message.success('创建成功');
    }
    router.push({ name: 'datasource' });
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
});
</script>

<style lang="scss" scoped>
.datasource-form {
  .form-footer {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #f0f0f0;
    text-align: center;
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

  .help-text {
    color: #666;
    font-size: 12px;
  }

  :deep(.ant-form-item-label) {
    font-weight: 500;
  }

  :deep(.ant-input-number) {
    width: 100%;
  }
}
</style>