# DatasetList.vue
<template>
  <v-container fluid>
    <!-- 页面标题区域 -->
    <v-row class="mb-6">
      <v-col>
        <div class="text-h4 font-weight-bold">数据集管理</div>
        <div class="text-subtitle-1 text-medium-emphasis">创建和管理数据集</div>
      </v-col>
    </v-row>

    <!-- 主要内容区域 -->
    <v-card>
      <!-- 工具栏 -->
      <v-toolbar flat density="comfortable">
        <v-text-field
          v-model="search"
          prepend-inner-icon="mdi-magnify"
          label="搜索数据集"
          single-line
          hide-details
          density="comfortable"
          class="mr-4"
          style="max-width: 300px"
        ></v-text-field>

        <v-spacer></v-spacer>

        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="openDialog()"
        >
          新建数据集
        </v-btn>
      </v-toolbar>

      <v-divider></v-divider>

      <!-- 数据表格 -->
      <v-data-table
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="datasets"
        :search="search"
        :loading="loading"
        hover
      >
        <!-- 类型列自定义 -->
        <template v-slot:item.type="{ item }">
          <v-chip
            :color="getTypeColor(item.type)"
            size="small"
            label
          >
            {{ item.type }}
          </v-chip>
        </template>

        <!-- 状态列自定义 -->
        <template v-slot:item.status="{ item }">
          <v-chip
            :color="item.status === 'active' ? 'success' : 'error'"
            size="small"
            label
          >
            {{ item.status === 'active' ? '正常' : '异常' }}
          </v-chip>
        </template>

        <!-- 操作列自定义 -->
        <template v-slot:item.actions="{ item }">
          <v-tooltip text="编辑">
            <template v-slot:activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-pencil"
                variant="text"
                size="small"
                color="primary"
                class="mr-1"
                @click="openDialog(item)"
              ></v-btn>
            </template>
          </v-tooltip>

          <v-tooltip text="预览数据">
            <template v-slot:activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-eye"
                variant="text"
                size="small"
                color="info"
                class="mr-1"
                @click="previewData(item)"
              ></v-btn>
            </template>
          </v-tooltip>

          <v-tooltip text="删除">
            <template v-slot:activator="{ props }">
              <v-btn
                v-bind="props"
                icon="mdi-delete"
                variant="text"
                size="small"
                color="error"
                @click="confirmDelete(item)"
              ></v-btn>
            </template>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>

    <!-- 新建/编辑对话框 -->
    <v-dialog
      v-model="dialog"
      max-width="800"
      persistent
    >
      <v-card>
        <v-card-title class="text-h5 pa-4">
          {{ editingItem ? '编辑数据集' : '新建数据集' }}
        </v-card-title>

        <v-card-text>
          <v-form ref="form" v-model="valid" @submit.prevent="saveDataset">
            <v-container>
              <v-row>
                <v-col cols="12">
                  <v-text-field
                    v-model="formData.name"
                    label="数据集名称"
                    :rules="[v => !!v || '请输入数据集名称']"
                    required
                  ></v-text-field>
                </v-col>

                <v-col cols="12">
                  <v-select
                    v-model="formData.datasourceId"
                    :items="datasources"
                    item-title="name"
                    item-value="id"
                    label="选择数据源"
                    :rules="[v => !!v || '请选择数据源']"
                    required
                  ></v-select>
                </v-col>

                <v-col cols="12">
                  <v-textarea
                    v-model="formData.sql"
                    label="SQL 查询"
                    :rules="[v => !!v || '请输入 SQL 查询']"
                    required
                    rows="5"
                  ></v-textarea>
                </v-col>

                <v-col cols="12">
                  <v-text-field
                    v-model="formData.description"
                    label="描述"
                    placeholder="请输入数据集描述"
                  ></v-text-field>
                </v-col>
              </v-row>
            </v-container>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
            variant="tonal"
            @click="dialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            :loading="saving"
            :disabled="!valid"
            @click="saveDataset"
          >
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 数据预览对话框 -->
    <v-dialog
      v-model="previewDialog"
      max-width="1000"
    >
      <v-card>
        <v-card-title class="text-h5 pa-4">
          数据预览
          <v-spacer></v-spacer>
          <v-btn
            icon="mdi-close"
            variant="text"
            @click="previewDialog = false"
          ></v-btn>
        </v-card-title>

        <v-card-text>
          <v-data-table
            :headers="previewHeaders"
            :items="previewData"
            :loading="previewLoading"
          ></v-data-table>
        </v-card-text>
      </v-card>
    </v-dialog>

    <!-- 删除确认对话框 -->
    <v-dialog
      v-model="deleteDialog"
      max-width="400"
    >
      <v-card>
        <v-card-title class="text-h5 pa-4">
          确认删除
        </v-card-title>

        <v-card-text>
          确定要删除数据集 "{{ deleteItem?.name }}" 吗？此操作无法撤销。
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
            variant="tonal"
            @click="deleteDialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="error"
            :loading="deleting"
            @click="deleteDataset"
          >
            删除
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 提示消息 -->
    <v-snackbar
      v-model="snackbar.show"
      :color="snackbar.color"
      :timeout="3000"
    >
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar.show = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { DataTableHeaders } from 'vuetify'

// 类型定义
interface Dataset {
  id: number
  name: string
  type: string
  datasourceId: number
  sql: string
  description?: string
  status: 'active' | 'error'
  createdAt: string
  updatedAt: string
}

interface Datasource {
  id: number
  name: string
}

// 表格列定义
const headers: DataTableHeaders = [
  { title: '数据集名称', key: 'name', align: 'start' },
  { title: '数据源', key: 'datasourceName', align: 'start' },
  { title: '状态', key: 'status', align: 'center', width: '100' },
  { title: '更新时间', key: 'updatedAt', align: 'center' },
  { title: '操作', key: 'actions', align: 'center', width: '150', sortable: false },
]

// 状态定义
const search = ref('')
const loading = ref(false)
const datasets = ref<Dataset[]>([])
const datasources = ref<Datasource[]>([])
const itemsPerPage = ref(10)

// 表单状态
const dialog = ref(false)
const valid = ref(false)
const form = ref<any>(null)
const saving = ref(false)
const editingItem = ref<Dataset | null>(null)

// 预览对话框状态
const previewDialog = ref(false)
const previewLoading = ref(false)
const previewHeaders = ref<DataTableHeaders>([])
const previewData = ref<any[]>([])

// 删除对话框状态
const deleteDialog = ref(false)
const deleteItem = ref<Dataset | null>(null)
const deleting = ref(false)

// 提示消息状态
const snackbar = reactive({
  show: false,
  text: '',
  color: 'success',
})

// 表单数据
const formData = reactive({
  name: '',
  datasourceId: null as number | null,
  sql: '',
  description: '',
})

// 获取数据集列表
const fetchDatasets = async () => {
  loading.value = true
  try {
    // TODO: 替换为实际的 API 调用
    const response = await fetch('/api/datasets')
    datasets.value = await response.json()
  } catch (error) {
    showError('获取数据集列表失败')
  } finally {
    loading.value = false
  }
}

// 获取数据源列表
const fetchDatasources = async () => {
  try {
    // TODO: 替换为实际的 API 调用
    const response = await fetch('/api/datasources')
    datasources.value = await response.json()
  } catch (error) {
    showError('获取数据源列表失败')
  }
}

// 打开新建/编辑对话框
const openDialog = (item?: Dataset) => {
  editingItem.value = item || null
  if (item) {
    Object.assign(formData, {
      name: item.name,
      datasourceId: item.datasourceId,
      sql: item.sql,
      description: item.description,
    })
  } else {
    Object.assign(formData, {
      name: '',
      datasourceId: null,
      sql: '',
      description: '',
    })
  }
  dialog.value = true
}

// 保存数据集
const saveDataset = async () => {
  if (!valid.value) return

  saving.value = true
  try {
    // TODO: 替换为实际的 API 调用
    const method = editingItem.value ? 'PUT' : 'POST'
    const url = editingItem.value 
      ? `/api/datasets/${editingItem.value.id}`
      : '/api/datasets'
    
    const response = await fetch(url, {
      method,
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(formData),
    })

    if (!response.ok) throw new Error('保存失败')

    showSuccess(editingItem.value ? '更新成功' : '创建成功')
    dialog.value = false
    fetchDatasets()
  } catch (error) {
    showError(editingItem.value ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

// 预览数据
const previewData = async (item: Dataset) => {
  previewDialog.value = true
  previewLoading.value = true
  try {
    // TODO: 替换为实际的 API 调用
    const response = await fetch(`/api/datasets/${item.id}/preview`)
    const data = await response.json()
    
    // 根据返回的数据动态生成表头
    if (data.length > 0) {
      previewHeaders.value = Object.keys(data[0]).map(key => ({
        title: key,
        key,
        align: 'start',
      }))
    }
    
    previewData.value = data
  } catch (error) {
    showError('获取预览数据失败')
  } finally {
    previewLoading.value = false
  }
}

// 确认删除
const confirmDelete = (item: Dataset) => {
  deleteItem.value = item
  deleteDialog.value = true
}

// 删除数据集
const deleteDataset = async () => {
  if (!deleteItem.value) return

  deleting.value = true
  try {
    // TODO: 替换为实际的 API 调用
    const response = await fetch(`/api/datasets/${deleteItem.value.id}`, {
      method: 'DELETE',
    })

    if (!response.ok) throw new Error('删除失败')

    showSuccess('删除成功')
    deleteDialog.value = false
    fetchDatasets()
  } catch (error) {
    showError('删除失败')
  } finally {
    deleting.value = false
  }
}

// 获取类型对应的颜色
const getTypeColor = (type: string): string => {
  const colors: Record<string, string> = {
    'MySQL': 'blue',
    'PostgreSQL': 'purple',
    'Oracle': 'red',
    'SQLServer': 'green',
  }
  return colors[type] || 'grey'
}

// 显示成功提示
const showSuccess = (text: string) => {
  snackbar.text = text
  snackbar.color = 'success'
  snackbar.show = true
}

// 显示错误提示
const showError = (text: string) => {
  snackbar.text = text
  snackbar.color = 'error'
  snackbar.show = true
}

// 页面加载时获取数据
onMounted(() => {
  fetchDatasets()
  fetchDatasources()
})
</script>

<style scoped>
.v-data-table {
  --v-table-header-height: 48px;
}
</style>