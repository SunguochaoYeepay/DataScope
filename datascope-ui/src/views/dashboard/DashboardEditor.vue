# DashboardEditor.vue
<template>
  <div class="dashboard-editor">
    <!-- 顶部工具栏 -->
    <v-app-bar :elevation="1">
      <template v-slot:prepend>
        <v-btn
          icon="mdi-arrow-left"
          variant="text"
          @click="handleCancel"
        ></v-btn>
      </template>

      <v-app-bar-title>
        <v-text-field
          v-model="dashboard.name"
          placeholder="输入仪表盘名称"
          hide-details
          density="compact"
          class="dashboard-name-input"
          @blur="handleNameChange"
        ></v-text-field>
      </v-app-bar-title>

      <template v-slot:append>
        <div class="d-flex align-center">
          <v-btn
            prepend-icon="mdi-eye"
            variant="text"
            class="mr-2"
            @click="handlePreview"
          >
            预览
          </v-btn>
          <v-btn
            color="primary"
            prepend-icon="mdi-content-save"
            :loading="isSaving"
            @click="handleSave"
          >
            保存
          </v-btn>
        </div>
      </template>
    </v-app-bar>

    <!-- 主编辑区域 -->
    <div class="editor-container">
      <!-- 左侧组件面板 -->
      <v-navigation-drawer
        v-model="showWidgetPanel"
        location="left"
        :rail="isPanelCollapsed"
        width="300"
        class="widget-panel"
      >
        <v-list>
          <v-list-subheader>添加组件</v-list-subheader>
          
          <!-- 图表组件 -->
          <v-list-group value="charts">
            <template v-slot:activator="{ props }">
              <v-list-item
                v-bind="props"
                prepend-icon="mdi-chart-box"
                title="图表"
              ></v-list-item>
            </template>

            <v-list-item
              v-for="chart in chartTypes"
              :key="chart.type"
              :title="chart.name"
              :prepend-icon="chart.icon"
              @click="addWidget('chart', chart.type)"
              class="widget-item"
            >
              <template v-slot:append>
                <v-icon icon="mdi-plus" size="small"></v-icon>
              </template>
            </v-list-item>
          </v-list-group>

          <!-- 指标组件 -->
          <v-list-group value="metrics">
            <template v-slot:activator="{ props }">
              <v-list-item
                v-bind="props"
                prepend-icon="mdi-gauge"
                title="指标"
              ></v-list-item>
            </template>

            <v-list-item
              v-for="metric in metricTypes"
              :key="metric.type"
              :title="metric.name"
              :prepend-icon="metric.icon"
              @click="addWidget('metric', metric.type)"
              class="widget-item"
            >
              <template v-slot:append>
                <v-icon icon="mdi-plus" size="small"></v-icon>
              </template>
            </v-list-item>
          </v-list-group>

          <!-- 文本组件 -->
          <v-list-item
            prepend-icon="mdi-text-box"
            title="文本"
            @click="addWidget('text')"
            class="widget-item"
          >
            <template v-slot:append>
              <v-icon icon="mdi-plus" size="small"></v-icon>
            </template>
          </v-list-item>

          <!-- 过滤器组件 -->
          <v-list-item
            prepend-icon="mdi-filter"
            title="过滤器"
            @click="addWidget('filter')"
            class="widget-item"
          >
            <template v-slot:append>
              <v-icon icon="mdi-plus" size="small"></v-icon>
            </template>
          </v-list-item>
        </v-list>

        <template v-slot:append>
          <div class="pa-2">
            <v-btn
              block
              prepend-icon="mdi-cog"
              variant="outlined"
              @click="showSettings = true"
            >
              仪表盘设置
            </v-btn>
          </div>
        </template>
      </v-navigation-drawer>

      <!-- 中间画布区域 -->
      <div class="canvas-container">
        <grid-layout
          v-model:layout="dashboard.layout"
          :col-num="24"
          :row-height="30"
          :is-draggable="true"
          :is-resizable="true"
          :vertical-compact="true"
          :use-css-transforms="true"
          :margin="[10, 10]"
          @layout-updated="handleLayoutUpdate"
        >
          <grid-item
            v-for="widget in dashboard.widgets"
            :key="widget.id"
            :x="widget.x"
            :y="widget.y"
            :w="widget.w"
            :h="widget.h"
            :i="widget.i"
            :min-w="4"
            :min-h="4"
          >
            <widget-container
              :widget="widget"
              :is-editing="true"
              @edit="handleWidgetEdit(widget)"
              @delete="handleWidgetDelete(widget)"
              @duplicate="handleWidgetDuplicate(widget)"
            ></widget-container>
          </grid-item>
        </grid-layout>

        <!-- 空状态提示 -->
        <div
          v-if="dashboard.widgets.length === 0"
          class="empty-state"
        >
          <v-icon
            icon="mdi-view-dashboard-outline"
            size="64"
            color="grey-lighten-1"
            class="mb-4"
          ></v-icon>
          <h3 class="text-h6 mb-2">开始创建您的仪表盘</h3>
          <p class="text-body-2 text-grey mb-4">
            从左侧面板选择组件添加到仪表盘
          </p>
          <v-btn
            color="primary"
            prepend-icon="mdi-plus"
            @click="showWidgetPanel = true"
          >
            添加组件
          </v-btn>
        </div>
      </div>

      <!-- 右侧配置面板 -->
      <v-navigation-drawer
        v-model="showConfigPanel"
        location="right"
        width="400"
        class="config-panel"
      >
        <template v-if="selectedWidget">
          <v-toolbar density="compact" class="px-4">
            <v-toolbar-title class="text-subtitle-1">
              {{ getWidgetTypeName(selectedWidget.type) }}配置
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn
              icon="mdi-close"
              variant="text"
              size="small"
              @click="closeConfigPanel"
            ></v-btn>
          </v-toolbar>

          <div class="config-content pa-4">
            <component
              :is="getConfigComponent(selectedWidget.type)"
              v-model:config="selectedWidget.config"
              @update:config="handleConfigUpdate"
            ></component>
          </div>
        </template>
      </v-navigation-drawer>
    </div>

    <!-- 仪表盘设置对话框 -->
    <v-dialog v-model="showSettings" max-width="600">
      <v-card>
        <v-card-title class="text-h6">仪表盘设置</v-card-title>
        <v-card-text>
          <v-form ref="settingsForm" class="mt-4">
            <!-- 基本信息 -->
            <v-text-field
              v-model="dashboard.name"
              label="仪表盘名称"
              required
              :rules="[v => !!v || '请输入仪表盘名称']"
              class="mb-4"
            ></v-text-field>

            <v-textarea
              v-model="dashboard.description"
              label="描述"
              rows="3"
              class="mb-4"
            ></v-textarea>

            <!-- 自动刷新设置 -->
            <v-select
              v-model="dashboard.refreshInterval"
              label="自动刷新间隔"
              :items="[
                { title: '关闭自动刷新', value: 0 },
                { title: '30秒', value: 30 },
                { title: '1分钟', value: 60 },
                { title: '5分钟', value: 300 },
                { title: '15分钟', value: 900 },
                { title: '30分钟', value: 1800 }
              ]"
              class="mb-4"
            ></v-select>

            <!-- 默认时间范围 -->
            <v-select
              v-model="dashboard.defaultTimeRange"
              label="默认时间范围"
              :items="[
                { title: '今天', value: 'today' },
                { title: '昨天', value: 'yesterday' },
                { title: '最近7天', value: 'last7days' },
                { title: '最近30天', value: 'last30days' },
                { title: '本月', value: 'thisMonth' },
                { title: '上月', value: 'lastMonth' },
                { title: '自定义', value: 'custom' }
              ]"
              class="mb-4"
            ></v-select>

            <!-- 权限设置 -->
            <v-radio-group
              v-model="dashboard.visibility"
              label="可见性"
              class="mb-4"
            >
              <v-radio
                label="私有（仅创建者可见）"
                value="private"
              ></v-radio>
              <v-radio
                label="共享（指定用户可见）"
                value="shared"
              ></v-radio>
              <v-radio
                label="公开（所有人可见）"
                value="public"
              ></v-radio>
            </v-radio-group>

            <!-- 主题设置 -->
            <v-select
              v-model="dashboard.theme"
              label="主题"
              :items="[
                { title: '跟随系统', value: 'system' },
                { title: '浅色', value: 'light' },
                { title: '深色', value: 'dark' }
              ]"
              class="mb-4"
            ></v-select>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="showSettings = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            variant="text"
            @click="saveSettings"
          >
            确定
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 保存确认对话框 -->
    <v-dialog v-model="showSaveConfirm" max-width="400">
      <v-card>
        <v-card-title class="text-h6">保存仪表盘</v-card-title>
        <v-card-text>
          确定要保存更改吗？
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="showSaveConfirm = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            variant="text"
            :loading="isSaving"
            @click="saveDashboard"
          >
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 取消确认对话框 -->
    <v-dialog v-model="showCancelConfirm" max-width="400">
      <v-card>
        <v-card-title class="text-h6">放弃更改</v-card-title>
        <v-card-text>
          您有未保存的更改，确定要放弃吗？
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="showCancelConfirm = false"
          >
            继续编辑
          </v-btn>
          <v-btn
            color="error"
            variant="text"
            @click="confirmCancel"
          >
            放弃更改
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSnackbar } from '@/composables/useSnackbar'
import GridLayout from 'vue-grid-layout'
import WidgetContainer from '@/components/dashboard/WidgetContainer.vue'
import ChartConfig from '@/components/dashboard/config/ChartConfig.vue'
import MetricConfig from '@/components/dashboard/config/MetricConfig.vue'
import TextConfig from '@/components/dashboard/config/TextConfig.vue'
import FilterConfig from '@/components/dashboard/config/FilterConfig.vue'

// 路由
const route = useRoute()
const router = useRouter()
const { showSuccess, showError } = useSnackbar()

// 状态定义
const dashboard = ref<any>({
  id: '',
  name: '',
  description: '',
  refreshInterval: 0,
  defaultTimeRange: 'last7days',
  visibility: 'private',
  theme: 'system',
  widgets: [],
  layout: []
})

// 面板控制
const showWidgetPanel = ref(true)
const isPanelCollapsed = ref(false)
const showConfigPanel = ref(false)
const showSettings = ref(false)
const showSaveConfirm = ref(false)
const showCancelConfirm = ref(false)

// 加载状态
const isSaving = ref(false)
const isLoading = ref(false)

// 选中的组件
const selectedWidget = ref<any>(null)

// 组件类型定义
const chartTypes = [
  { type: 'line', name: '折线图', icon: 'mdi-chart-line' },
  { type: 'bar', name: '柱状图', icon: 'mdi-chart-bar' },
  { type: 'pie', name: '饼图', icon: 'mdi-chart-pie' },
  { type: 'scatter', name: '散点图', icon: 'mdi-chart-scatter-plot' },
  { type: 'area', name: '面积图', icon: 'mdi-chart-areaspline' },
  { type: 'radar', name: '雷达图', icon: 'mdi-chart-arc' },
  { type: 'funnel', name: '漏斗图', icon: 'mdi-chart-timeline-variant' },
  { type: 'heatmap', name: '热力图', icon: 'mdi-chart-box' }
]

const metricTypes = [
  { type: 'number', name: '数值指标', icon: 'mdi-numeric' },
  { type: 'percentage', name: '百分比', icon: 'mdi-percent' },
  { type: 'trend', name: '趋势指标', icon: 'mdi-trending-up' }
]

// 生命周期钩子
onMounted(() => {
  loadDashboard()
})

// 加载仪表盘数据
const loadDashboard = async () => {
  const id = route.params.id
  if (id === 'create') {
    // 创建新仪表盘
    dashboard.value = {
      name: '新建仪表盘',
      description: '',
      refreshInterval: 0,
      defaultTimeRange: 'last7days',
      visibility: 'private',
      theme: 'system',
      widgets: [],
      layout: []
    }
  } else {
    // 加载现有仪表盘
    isLoading.value = true
    try {
      // TODO: 调用加载 API
      await new Promise(resolve => setTimeout(resolve, 1000))
      // 模拟数据
      dashboard.value = {
        id,
        name: '销售数据分析',
        description: '实时监控销售数据，包含销售趋势、区域分布等多个维度分析',
        refreshInterval: 300,
        defaultTimeRange: 'last7days',
        visibility: 'private',
        theme: 'system',
        widgets: [],
        layout: []
      }
    } catch (error) {
      showError('加载失败')
    } finally {
      isLoading.value = false
    }
  }
}

// 添加组件
const addWidget = (type: string, subType?: string) => {
  const widget = {
    id: Date.now(),
    type,
    subType,
    title: getWidgetTypeName(type),
    x: 0,
    y: 0,
    w: getDefaultWidth(type),
    h: getDefaultHeight(type),
    i: dashboard.value.widgets.length.toString(),
    config: {}
  }

  dashboard.value.widgets.push(widget)
  dashboard.value.layout.push({
    x: widget.x,
    y: widget.y,
    w: widget.w,
    h: widget.h,
    i: widget.i
  })

  // 自动打开配置面板
  selectedWidget.value = widget
  showConfigPanel.value = true
}

// 获取组件默认宽度
const getDefaultWidth = (type: string) => {
  switch (type) {
    case 'chart':
      return 12
    case 'metric':
      return 6
    case 'text':
      return 8
    case 'filter':
      return 6
    default:
      return 6
  }
}

// 获取组件默认高度
const getDefaultHeight = (type: string) => {
  switch (type) {
    case 'chart':
      return 8
    case 'metric':
      return 4
    case 'text':
      return 4
    case 'filter':
      return 2
    default:
      return 4
  }
}

// 获取组件类型名称
const getWidgetTypeName = (type: string) => {
  switch (type) {
    case 'chart':
      return '图表'
    case 'metric':
      return '指标'
    case 'text':
      return '文本'
    case 'filter':
      return '过滤器'
    default:
      return '组件'
  }
}

// 获取配置组件
const getConfigComponent = (type: string) => {
  switch (type) {
    case 'chart':
      return ChartConfig
    case 'metric':
      return MetricConfig
    case 'text':
      return TextConfig
    case 'filter':
      return FilterConfig
    default:
      return null
  }
}

// 处理组件编辑
const handleWidgetEdit = (widget: any) => {
  selectedWidget.value = widget
  showConfigPanel.value = true
}

// 处理组件删除
const handleWidgetDelete = (widget: any) => {
  const index = dashboard.value.widgets.findIndex((w: any) => w.id === widget.id)
  if (index > -1) {
    dashboard.value.widgets.splice(index, 1)
    dashboard.value.layout.splice(index, 1)
  }
  if (selectedWidget.value?.id === widget.id) {
    closeConfigPanel()
  }
}

// 处理组件复制
const handleWidgetDuplicate = (widget: any) => {
  const newWidget = {
    ...JSON.parse(JSON.stringify(widget)),
    id: Date.now(),
    i: dashboard.value.widgets.length.toString()
  }
  dashboard.value.widgets.push(newWidget)
  dashboard.value.layout.push({
    ...JSON.parse(JSON.stringify(widget)),
    i: newWidget.i
  })
}

// 处理布局更新
const handleLayoutUpdate = (layout: any[]) => {
  dashboard.value.layout = layout
  // 同步更新组件位置和大小
  layout.forEach((item: any) => {
    const widget = dashboard.value.widgets.find((w: any) => w.i === item.i)
    if (widget) {
      widget.x = item.x
      widget.y = item.y
      widget.w = item.w
      widget.h = item.h
    }
  })
}

// 处理配置更新
const handleConfigUpdate = (config: any) => {
  if (selectedWidget.value) {
    selectedWidget.value.config = config
  }
}

// 关闭配置面板
const closeConfigPanel = () => {
  selectedWidget.value = null
  showConfigPanel.value = false
}

// 处理名称变更
const handleNameChange = () => {
  if (!dashboard.value.name) {
    dashboard.value.name = '新建仪表盘'
  }
}

// 保存设置
const saveSettings = () => {
  showSettings.value = false
}

// 处理预览
const handlePreview = () => {
  // TODO: 实现预览功能
}

// 处理保存
const handleSave = () => {
  if (!dashboard.value.name) {
    showError('请输入仪表盘名称')
    return
  }
  showSaveConfirm.value = true
}

// 保存仪表盘
const saveDashboard = async () => {
  isSaving.value = true
  try {
    // TODO: 调用保存 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('保存成功')
    showSaveConfirm.value = false
    router.push('/dashboard/list')
  } catch (error) {
    showError('保存失败')
  } finally {
    isSaving.value = false
  }
}

// 处理取消
const handleCancel = () => {
  if (hasChanges()) {
    showCancelConfirm.value = true
  } else {
    router.back()
  }
}

// 确认取消
const confirmCancel = () => {
  showCancelConfirm.value = false
  router.back()
}

// 检查是否有更改
const hasChanges = () => {
  // TODO: 实现更改检测
  return true
}

// 在组件销毁前提示保存
onBeforeUnmount(() => {
  if (hasChanges()) {
    // TODO: 提示保存
  }
})
</script>

<style scoped>
.dashboard-editor {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.editor-container {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.dashboard-name-input {
  max-width: 300px;
}

.widget-panel {
  border-right: 1px solid rgba(0, 0, 0, 0.12);
}

.config-panel {
  border-left: 1px solid rgba(0, 0, 0, 0.12);
}

.canvas-container {
  flex: 1;
  overflow: auto;
  padding: 24px;
  background-color: #f5f5f5;
}

.widget-item {
  cursor: pointer;
}

.widget-item:hover {
  background-color: rgba(var(--v-theme-primary), 0.05);
}

.empty-state {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 24px;
}

.config-content {
  height: calc(100% - 48px);
  overflow: auto;
}

:deep(.vue-grid-item) {
  background: white;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 8px;
}

:deep(.vue-grid-item.vue-grid-placeholder) {
  background: rgba(var(--v-theme-primary), 0.1) !important;
  border: 2px dashed var(--v-theme-primary);
}
</style>