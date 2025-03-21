# WidgetContainer.vue
<template>
  <div class="widget-container">
    <!-- 组件头部 -->
    <div class="widget-header px-4 py-2">
      <div class="d-flex align-center">
        <v-icon
          :icon="getWidgetIcon(widget.type, widget.subType)"
          size="small"
          class="mr-2"
        ></v-icon>
        <span class="text-subtitle-2 text-truncate">{{ widget.title }}</span>
        
        <v-spacer></v-spacer>

        <!-- 编辑模式下的操作按钮 -->
        <template v-if="isEditing">
          <v-btn
            icon="mdi-pencil"
            variant="text"
            size="small"
            @click="$emit('edit')"
          ></v-btn>
          <v-btn
            icon="mdi-content-copy"
            variant="text"
            size="small"
            @click="$emit('duplicate')"
          ></v-btn>
          <v-btn
            icon="mdi-delete"
            variant="text"
            size="small"
            color="error"
            @click="$emit('delete')"
          ></v-btn>
        </template>

        <!-- 查看模式下的操作按钮 -->
        <template v-else>
          <v-btn
            v-if="widget.type === 'chart'"
            icon="mdi-download"
            variant="text"
            size="small"
            @click="handleDownload"
          ></v-btn>
          <v-btn
            icon="mdi-refresh"
            variant="text"
            size="small"
            :loading="isLoading"
            @click="handleRefresh"
          ></v-btn>
          <v-menu>
            <template v-slot:activator="{ props }">
              <v-btn
                icon="mdi-dots-vertical"
                variant="text"
                size="small"
                v-bind="props"
              ></v-btn>
            </template>
            <v-list>
              <v-list-item
                v-if="widget.type === 'chart'"
                prepend-icon="mdi-fullscreen"
                title="全屏显示"
                @click="handleFullscreen"
              ></v-list-item>
              <v-list-item
                prepend-icon="mdi-export-variant"
                title="导出数据"
                @click="handleExport"
              ></v-list-item>
              <v-list-item
                prepend-icon="mdi-information"
                title="查看详情"
                @click="handleViewDetail"
              ></v-list-item>
            </v-list>
          </v-menu>
        </template>
      </div>
    </div>

    <!-- 组件内容 -->
    <div class="widget-content pa-4">
      <!-- 加载状态 -->
      <div v-if="isLoading" class="widget-loading">
        <v-progress-circular
          indeterminate
          color="primary"
        ></v-progress-circular>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="hasError" class="widget-error">
        <v-icon
          icon="mdi-alert-circle"
          color="error"
          size="32"
          class="mb-2"
        ></v-icon>
        <p class="text-body-2 text-error">{{ errorMessage }}</p>
        <v-btn
          color="primary"
          variant="text"
          size="small"
          @click="handleRefresh"
        >
          重试
        </v-btn>
      </div>

      <!-- 空数据状态 -->
      <div v-else-if="isEmpty" class="widget-empty">
        <v-icon
          icon="mdi-database-off"
          color="grey-lighten-1"
          size="32"
          class="mb-2"
        ></v-icon>
        <p class="text-body-2 text-grey">暂无数据</p>
      </div>

      <!-- 图表组件 -->
      <template v-else-if="widget.type === 'chart'">
        <chart-widget
          :type="widget.subType"
          :data="widgetData"
          :config="widget.config"
          :filter-values="filterValues"
          :time-range="timeRange"
          @error="handleError"
        ></chart-widget>
      </template>

      <!-- 指标组件 -->
      <template v-else-if="widget.type === 'metric'">
        <metric-widget
          :type="widget.subType"
          :data="widgetData"
          :config="widget.config"
          :filter-values="filterValues"
          :time-range="timeRange"
          @error="handleError"
        ></metric-widget>
      </template>

      <!-- 文本组件 -->
      <template v-else-if="widget.type === 'text'">
        <text-widget
          :content="widget.config.content"
          :style="widget.config.style"
        ></text-widget>
      </template>

      <!-- 过滤器组件 -->
      <template v-else-if="widget.type === 'filter'">
        <filter-widget
          :type="widget.subType"
          :config="widget.config"
          :value="filterValues[widget.config.field]"
          @update:value="handleFilterChange"
        ></filter-widget>
      </template>
    </div>

    <!-- 全屏对话框 -->
    <v-dialog
      v-model="showFullscreen"
      fullscreen
      :scrim="false"
      transition="dialog-bottom-transition"
    >
      <v-card>
        <v-toolbar density="compact">
          <v-btn
            icon="mdi-close"
            variant="text"
            @click="showFullscreen = false"
          ></v-btn>
          <v-toolbar-title>{{ widget.title }}</v-toolbar-title>
          <v-spacer></v-spacer>
          <v-btn
            icon="mdi-download"
            variant="text"
            @click="handleDownload"
          ></v-btn>
          <v-btn
            icon="mdi-refresh"
            variant="text"
            :loading="isLoading"
            @click="handleRefresh"
          ></v-btn>
        </v-toolbar>

        <v-card-text class="pa-0">
          <div class="fullscreen-content">
            <chart-widget
              :type="widget.subType"
              :data="widgetData"
              :config="widget.config"
              :filter-values="filterValues"
              :time-range="timeRange"
              :is-fullscreen="true"
              @error="handleError"
            ></chart-widget>
          </div>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useSnackbar } from '@/composables/useSnackbar'
import ChartWidget from './widgets/ChartWidget.vue'
import MetricWidget from './widgets/MetricWidget.vue'
import TextWidget from './widgets/TextWidget.vue'
import FilterWidget from './widgets/FilterWidget.vue'

// 属性定义
const props = defineProps<{
  widget: {
    id: number | string
    type: string
    subType?: string
    title: string
    config: Record<string, any>
  }
  isEditing?: boolean
  filterValues?: Record<string, any>
  timeRange?: {
    start: string
    end: string
  }
}>()

// 事件定义
const emit = defineEmits<{
  (e: 'edit'): void
  (e: 'delete'): void
  (e: 'duplicate'): void
  (e: 'refresh'): void
  (e: 'filter-change', field: string, value: any): void
}>()

const { showSuccess, showError } = useSnackbar()

// 状态定义
const isLoading = ref(false)
const hasError = ref(false)
const errorMessage = ref('')
const showFullscreen = ref(false)
const widgetData = ref<any>(null)

// 计算属性：是否为空数据
const isEmpty = computed(() => {
  if (!widgetData.value) return true
  if (Array.isArray(widgetData.value)) return widgetData.value.length === 0
  if (typeof widgetData.value === 'object') return Object.keys(widgetData.value).length === 0
  return false
})

// 获取组件图标
const getWidgetIcon = (type: string, subType?: string) => {
  switch (type) {
    case 'chart':
      switch (subType) {
        case 'line':
          return 'mdi-chart-line'
        case 'bar':
          return 'mdi-chart-bar'
        case 'pie':
          return 'mdi-chart-pie'
        case 'scatter':
          return 'mdi-chart-scatter-plot'
        case 'area':
          return 'mdi-chart-areaspline'
        case 'radar':
          return 'mdi-chart-arc'
        case 'funnel':
          return 'mdi-chart-timeline-variant'
        case 'heatmap':
          return 'mdi-chart-box'
        default:
          return 'mdi-chart-box'
      }
    case 'metric':
      return 'mdi-gauge'
    case 'text':
      return 'mdi-text-box'
    case 'filter':
      return 'mdi-filter'
    default:
      return 'mdi-view-dashboard'
  }
}

// 刷新数据
const handleRefresh = async () => {
  isLoading.value = true
  hasError.value = false
  errorMessage.value = ''

  try {
    // TODO: 调用数据加载 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    // 模拟数据
    widgetData.value = {
      // 根据组件类型生成不同的模拟数据
    }
    emit('refresh')
  } catch (error: any) {
    handleError(error)
  } finally {
    isLoading.value = false
  }
}

// 处理错误
const handleError = (error: any) => {
  hasError.value = true
  errorMessage.value = error.message || '加载失败'
  showError(errorMessage.value)
}

// 处理下载
const handleDownload = async () => {
  try {
    // TODO: 实现下载功能
    await new Promise(resolve => setTimeout(resolve, 500))
    showSuccess('下载成功')
  } catch (error) {
    showError('下载失败')
  }
}

// 处理导出
const handleExport = async () => {
  try {
    // TODO: 实现导出功能
    await new Promise(resolve => setTimeout(resolve, 500))
    showSuccess('导出成功')
  } catch (error) {
    showError('导出失败')
  }
}

// 处理全屏显示
const handleFullscreen = () => {
  showFullscreen.value = true
}

// 处理查看详情
const handleViewDetail = () => {
  // TODO: 实现查看详情功能
}

// 处理过滤器变化
const handleFilterChange = (value: any) => {
  emit('filter-change', props.widget.config.field, value)
}

// 初始加载数据
onMounted(() => {
  handleRefresh()
})
</script>

<style scoped>
.widget-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.widget-header {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.widget-content {
  flex: 1;
  position: relative;
  overflow: hidden;
}

.widget-loading,
.widget-error,
.widget-empty {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.9);
}

.fullscreen-content {
  height: calc(100vh - 48px);
  padding: 24px;
}
</style>