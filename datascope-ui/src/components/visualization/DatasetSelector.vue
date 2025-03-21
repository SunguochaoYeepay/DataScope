# DatasetSelector.vue
<template>
  <div class="dataset-selector">
    <!-- 数据集选择区域 -->
    <v-row>
      <v-col cols="12" md="8">
        <v-select
          v-model="selectedDatasetId"
          :items="datasets"
          item-title="name"
          item-value="id"
          label="选择数据集"
          :loading="loading"
          :rules="[v => !!v || '请选择数据集']"
          required
          :error-messages="errorMessage"
          @update:model-value="handleDatasetChange"
        >
          <template v-slot:prepend-inner>
            <v-icon icon="mdi-database-search"></v-icon>
          </template>

          <template v-slot:item="{ props, item }">
            <v-list-item v-bind="props">
              <template v-slot:prepend>
                <v-icon :icon="getDatasetTypeIcon(item.raw.type)"></v-icon>
              </template>
              <v-list-item-title>{{ item.raw.name }}</v-list-item-title>
              <v-list-item-subtitle>{{ item.raw.description }}</v-list-item-subtitle>
            </v-list-item>
          </template>
        </v-select>
      </v-col>

      <v-col cols="12" md="4" class="d-flex align-center">
        <v-btn
          prepend-icon="mdi-refresh"
          variant="text"
          :loading="refreshing"
          @click="refreshDatasets"
        >
          刷新列表
        </v-btn>
        <v-btn
          prepend-icon="mdi-database-plus"
          variant="text"
          color="primary"
          @click="$emit('create-dataset')"
        >
          新建数据集
        </v-btn>
      </v-col>
    </v-row>

    <!-- 数据预览区域 -->
    <v-card
      class="mt-4"
      variant="outlined"
    >
      <v-toolbar
        density="comfortable"
        color="surface"
      >
        <v-toolbar-title class="text-subtitle-1">数据预览</v-toolbar-title>
        
        <v-spacer></v-spacer>

        <!-- 刷新按钮 -->
        <v-btn
          icon="mdi-refresh"
          variant="text"
          :loading="previewLoading"
          @click="refreshPreview"
          :disabled="!selectedDatasetId"
        >
          <v-tooltip activator="parent" location="top">刷新预览</v-tooltip>
        </v-btn>

        <!-- 字段选择 -->
        <v-menu>
          <template v-slot:activator="{ props }">
            <v-btn
              icon="mdi-eye-settings"
              variant="text"
              v-bind="props"
              :disabled="!selectedDatasetId"
            >
              <v-tooltip activator="parent" location="top">显示字段</v-tooltip>
            </v-btn>
          </template>

          <v-card min-width="200">
            <v-list>
              <v-list-subheader>选择显示字段</v-list-subheader>
              <v-list-item
                v-for="field in availableFields"
                :key="field"
              >
                <template v-slot:prepend>
                  <v-checkbox
                    v-model="selectedFields"
                    :value="field"
                    hide-details
                  ></v-checkbox>
                </template>
                <v-list-item-title>{{ field }}</v-list-item-title>
              </v-list-item>
            </v-list>
          </v-card>
        </v-menu>
      </v-toolbar>

      <v-divider></v-divider>

      <!-- 数据表格 -->
      <v-data-table
        v-model:items-per-page="itemsPerPage"
        :headers="visibleHeaders"
        :items="previewData"
        :loading="previewLoading"
        hover
      >
        <!-- 空状态 -->
        <template v-slot:no-data>
          <div class="d-flex align-center justify-center pa-4">
            <v-icon
              icon="mdi-database-search"
              size="40"
              color="medium-emphasis"
              class="mr-4"
            ></v-icon>
            <div class="text-medium-emphasis">
              {{ !selectedDatasetId ? '请选择数据集' : '暂无数据' }}
            </div>
          </div>
        </template>

        <!-- 加载状态 -->
        <template v-slot:loading>
          <div class="d-flex align-center justify-center pa-4">
            <v-progress-circular
              indeterminate
              color="primary"
              class="mr-4"
            ></v-progress-circular>
            <div>加载数据中...</div>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- 错误提示 -->
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue'
import type { DataTableHeaders } from 'vuetify'

// 属性定义
const props = defineProps<{
  modelValue?: number
}>()

// 事件定义
const emit = defineEmits<{
  (e: 'update:modelValue', value: number): void
  (e: 'create-dataset'): void
  (e: 'dataset-change', datasetId: number): void
  (e: 'preview-data-change', data: any[]): void
}>()

// 状态定义
const loading = ref(false)
const refreshing = ref(false)
const previewLoading = ref(false)
const errorMessage = ref('')
const datasets = ref<any[]>([])
const selectedDatasetId = ref(props.modelValue)
const itemsPerPage = ref(10)

// 预览数据相关
const previewData = ref<any[]>([])
const availableFields = ref<string[]>([])
const selectedFields = ref<string[]>([])

// 提示消息
const snackbar = reactive({
  show: false,
  text: '',
  color: 'success'
})

// 计算可见的表头
const visibleHeaders = computed(() => {
  if (!selectedFields.value.length) return []
  return selectedFields.value.map(field => ({
    title: field,
    key: field,
    align: 'start'
  })) as DataTableHeaders
})

// 监听数据集选择变化
watch(() => props.modelValue, (newValue) => {
  if (newValue !== selectedDatasetId.value) {
    selectedDatasetId.value = newValue
  }
})

watch(selectedDatasetId, (newValue) => {
  emit('update:modelValue', newValue)
  if (newValue) {
    loadPreviewData(newValue)
  }
})

// 获取数据集列表
const fetchDatasets = async () => {
  loading.value = true
  errorMessage.value = ''
  
  try {
    // TODO: 替换为实际的 API 调用
    const response = await fetch('/api/datasets')
    const data = await response.json()
    datasets.value = data
  } catch (error) {
    console.error('Failed to fetch datasets:', error)
    errorMessage.value = '获取数据集列表失败'
    showError('获取数据集列表失败')
  } finally {
    loading.value = false
  }
}

// 刷新数据集列表
const refreshDatasets = async () => {
  refreshing.value = true
  try {
    await fetchDatasets()
  } finally {
    refreshing.value = false
  }
}

// 加载预览数据
const loadPreviewData = async (datasetId: number) => {
  previewLoading.value = true
  try {
    // TODO: 替换为实际的 API 调用
    const response = await fetch(`/api/datasets/${datasetId}/preview`)
    const data = await response.json()
    
    // 更新预览数据
    previewData.value = data.data
    
    // 更新可用字段
    if (data.data.length > 0) {
      availableFields.value = Object.keys(data.data[0])
      selectedFields.value = [...availableFields.value]
    }
    
    // 触发事件
    emit('preview-data-change', data.data)
  } catch (error) {
    console.error('Failed to load preview data:', error)
    showError('加载预览数据失败')
  } finally {
    previewLoading.value = false
  }
}

// 刷新预览数据
const refreshPreview = () => {
  if (selectedDatasetId.value) {
    loadPreviewData(selectedDatasetId.value)
  }
}

// 处理数据集变更
const handleDatasetChange = (datasetId: number) => {
  emit('dataset-change', datasetId)
}

// 获取数据集类型图标
const getDatasetTypeIcon = (type: string): string => {
  const icons: Record<string, string> = {
    'MySQL': 'mdi-database',
    'PostgreSQL': 'mdi-elephant',
    'Oracle': 'mdi-database-check',
    'SQLServer': 'mdi-microsoft'
  }
  return icons[type] || 'mdi-database'
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
})
</script>

<style scoped>
.dataset-selector {
  width: 100%;
}
</style>