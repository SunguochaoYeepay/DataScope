# DashboardDetail.vue
<template>
  <div class="dashboard-detail">
    <!-- 顶部工具栏 -->
    <v-app-bar
      :elevation="1"
      class="px-4"
    >
      <template v-slot:prepend>
        <v-btn
          icon="mdi-arrow-left"
          variant="text"
          @click="router.back()"
        ></v-btn>
      </template>

      <v-app-bar-title class="d-flex align-center">
        <span class="text-truncate">{{ dashboard.name }}</span>
        <v-chip
          v-if="isShared"
          color="primary"
          size="small"
          class="ml-2"
        >
          共享
        </v-chip>
      </v-app-bar-title>

      <template v-slot:append>
        <div class="d-flex align-center">
          <!-- 刷新间隔 -->
          <v-select
            v-model="refreshInterval"
            :items="[
              { title: '关闭自动刷新', value: 0 },
              { title: '30秒', value: 30 },
              { title: '1分钟', value: 60 },
              { title: '5分钟', value: 300 },
              { title: '15分钟', value: 900 },
              { title: '30分钟', value: 1800 }
            ]"
            density="comfortable"
            hide-details
            class="refresh-select mr-2"
          >
            <template v-slot:prepend>
              <v-icon icon="mdi-refresh"></v-icon>
            </template>
          </v-select>

          <!-- 时间范围选择 -->
          <v-menu>
            <template v-slot:activator="{ props }">
              <v-btn
                variant="outlined"
                v-bind="props"
                class="mr-2"
                prepend-icon="mdi-clock-outline"
              >
                {{ getTimeRangeText() }}
              </v-btn>
            </template>

            <v-card min-width="300">
              <v-card-text>
                <div class="d-flex flex-column">
                  <!-- 快捷选项 -->
                  <v-btn
                    v-for="preset in timePresets"
                    :key="preset.value"
                    variant="text"
                    class="justify-start mb-2"
                    @click="selectTimeRange(preset)"
                  >
                    {{ preset.title }}
                  </v-btn>

                  <v-divider class="my-2"></v-divider>

                  <!-- 自定义时间范围 -->
                  <div class="d-flex flex-column">
                    <v-text-field
                      v-model="timeRange.start"
                      type="datetime-local"
                      label="开始时间"
                      density="comfortable"
                      class="mb-2"
                    ></v-text-field>
                    <v-text-field
                      v-model="timeRange.end"
                      type="datetime-local"
                      label="结束时间"
                      density="comfortable"
                    ></v-text-field>
                  </div>
                </div>
              </v-card-text>
            </v-card>
          </v-menu>

          <!-- 全屏按钮 -->
          <v-btn
            :icon="isFullscreen ? 'mdi-fullscreen-exit' : 'mdi-fullscreen'"
            variant="text"
            @click="toggleFullscreen"
            class="mr-2"
          ></v-btn>

          <!-- 更多操作菜单 -->
          <v-menu>
            <template v-slot:activator="{ props }">
              <v-btn
                icon="mdi-dots-vertical"
                variant="text"
                v-bind="props"
              ></v-btn>
            </template>

            <v-list>
              <v-list-item
                prepend-icon="mdi-pencil"
                title="编辑仪表盘"
                @click="handleEdit"
              ></v-list-item>
              <v-list-item
                prepend-icon="mdi-share-variant"
                title="分享仪表盘"
                @click="handleShare"
              ></v-list-item>
              <v-list-item
                prepend-icon="mdi-content-copy"
                title="复制仪表盘"
                @click="handleCopy"
              ></v-list-item>
              <v-list-item
                prepend-icon="mdi-export-variant"
                title="导出数据"
                @click="handleExport"
              ></v-list-item>
              <v-divider></v-divider>
              <v-list-item
                prepend-icon="mdi-delete"
                title="删除仪表盘"
                @click="handleDelete"
                color="error"
              ></v-list-item>
            </v-list>
          </v-menu>
        </div>
      </template>
    </v-app-bar>

    <!-- 仪表盘内容 -->
    <div class="dashboard-content pa-4">
      <!-- 过滤器区域 -->
      <v-expand-transition>
        <div v-if="hasFilters" class="mb-4">
          <v-card>
            <v-card-text>
              <v-row>
                <v-col
                  v-for="filter in dashboard.filters"
                  :key="filter.id"
                  :cols="filter.width || 3"
                >
                  <component
                    :is="getFilterComponent(filter.type)"
                    v-model="filterValues[filter.id]"
                    v-bind="filter.props"
                    @update:modelValue="handleFilterChange"
                  ></component>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </div>
      </v-expand-transition>

      <!-- 网格布局 -->
      <grid-layout
        v-model:layout="dashboard.layout"
        :col-num="24"
        :row-height="30"
        :is-draggable="false"
        :is-resizable="false"
        :vertical-compact="true"
        :margin="[10, 10]"
        :use-css-transforms="true"
      >
        <grid-item
          v-for="widget in dashboard.widgets"
          :key="widget.id"
          :x="widget.x"
          :y="widget.y"
          :w="widget.w"
          :h="widget.h"
          :i="widget.i"
        >
          <widget-container
            :widget="widget"
            :filter-values="filterValues"
            :time-range="timeRange"
            :is-loading="isRefreshing"
            @refresh="refreshWidget(widget)"
          ></widget-container>
        </grid-item>
      </grid-layout>
    </div>

    <!-- 分享对话框 -->
    <v-dialog v-model="shareDialog" max-width="500">
      <v-card>
        <v-card-title class="text-h6">分享仪表盘</v-card-title>
        <v-card-text>
          <v-tabs v-model="shareTab" class="mb-4">
            <v-tab value="link">链接分享</v-tab>
            <v-tab value="user">指定用户</v-tab>
          </v-tabs>

          <v-window v-model="shareTab">
            <!-- 链接分享 -->
            <v-window-item value="link">
              <v-text-field
                v-model="shareLink"
                label="分享链接"
                readonly
                variant="outlined"
                append-inner-icon="mdi-content-copy"
                @click:append-inner="copyShareLink"
              ></v-text-field>
              <v-switch
                v-model="shareConfig.requirePassword"
                label="设置访问密码"
                hide-details
                class="mb-2"
              ></v-switch>
              <v-expand-transition>
                <div v-if="shareConfig.requirePassword">
                  <v-text-field
                    v-model="shareConfig.password"
                    label="访问密码"
                    variant="outlined"
                    type="password"
                    class="mt-2"
                  ></v-text-field>
                </div>
              </v-expand-transition>
              <v-switch
                v-model="shareConfig.enableExpiry"
                label="设置有效期"
                hide-details
                class="mb-2"
              ></v-switch>
              <v-expand-transition>
                <div v-if="shareConfig.enableExpiry">
                  <v-select
                    v-model="shareConfig.expiryDays"
                    :items="[
                      { title: '1天', value: 1 },
                      { title: '7天', value: 7 },
                      { title: '30天', value: 30 },
                      { title: '永久', value: -1 }
                    ]"
                    label="有效期"
                    variant="outlined"
                    class="mt-2"
                  ></v-select>
                </div>
              </v-expand-transition>
            </v-window-item>

            <!-- 指定用户 -->
            <v-window-item value="user">
              <v-autocomplete
                v-model="selectedUsers"
                :items="userList"
                label="选择用户"
                item-title="name"
                item-value="id"
                multiple
                chips
                variant="outlined"
              >
                <template v-slot:chip="{ props, item }">
                  <v-chip
                    v-bind="props"
                    :prepend-avatar="item.raw.avatar"
                  >
                    {{ item.raw.name }}
                  </v-chip>
                </template>
                <template v-slot:item="{ props, item }">
                  <v-list-item
                    v-bind="props"
                    :prepend-avatar="item.raw.avatar"
                    :title="item.raw.name"
                    :subtitle="item.raw.email"
                  ></v-list-item>
                </template>
              </v-autocomplete>
              <v-radio-group
                v-model="shareConfig.permission"
                hide-details
                class="mt-4"
              >
                <v-radio
                  label="仅查看"
                  value="view"
                ></v-radio>
                <v-radio
                  label="可编辑"
                  value="edit"
                ></v-radio>
              </v-radio-group>
            </v-window-item>
          </v-window>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="shareDialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            variant="text"
            :loading="isSharing"
            @click="confirmShare"
          >
            分享
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 删除确认对话框 -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6">删除仪表盘</v-card-title>
        <v-card-text>
          确定要删除仪表盘"{{ dashboard.name }}"吗？此操作无法撤销。
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="deleteDialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="error"
            variant="text"
            :loading="isDeleting"
            @click="confirmDelete"
          >
            删除
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

// 路由
const route = useRoute()
const router = useRouter()
const { showSuccess, showError } = useSnackbar()

// 状态定义
const dashboard = ref<any>({
  id: 1,
  name: '销售数据分析',
  description: '实时监控销售数据，包含销售趋势、区域分布等多个维度分析',
  isShared: true,
  filters: [
    {
      id: 'date',
      type: 'dateRange',
      width: 6,
      props: {
        label: '日期范围'
      }
    },
    {
      id: 'region',
      type: 'select',
      width: 3,
      props: {
        label: '地区',
        items: ['华东', '华南', '华北', '西南']
      }
    }
  ],
  widgets: [
    {
      id: 1,
      type: 'chart',
      title: '销售趋势',
      x: 0,
      y: 0,
      w: 12,
      h: 8,
      config: {
        // 图表配置
      }
    },
    {
      id: 2,
      type: 'metric',
      title: '总销售额',
      x: 12,
      y: 0,
      w: 6,
      h: 4,
      config: {
        // 指标配置
      }
    }
  ]
})

const isShared = computed(() => dashboard.value.isShared)
const hasFilters = computed(() => dashboard.value.filters?.length > 0)

// 刷新控制
const refreshInterval = ref(0)
let refreshTimer: number | null = null

// 时间范围控制
const timeRange = ref({
  start: '',
  end: ''
})

const timePresets = [
  { title: '今天', value: 'today' },
  { title: '昨天', value: 'yesterday' },
  { title: '最近7天', value: 'last7days' },
  { title: '最近30天', value: 'last30days' },
  { title: '本月', value: 'thisMonth' },
  { title: '上月', value: 'lastMonth' }
]

// 过滤器值
const filterValues = ref<Record<string, any>>({})

// 全屏控制
const isFullscreen = ref(false)

// 对话框控制
const shareDialog = ref(false)
const deleteDialog = ref(false)
const shareTab = ref('link')
const isSharing = ref(false)
const isDeleting = ref(false)
const shareLink = ref('')
const selectedUsers = ref([])
const shareConfig = ref({
  requirePassword: false,
  password: '',
  enableExpiry: false,
  expiryDays: 7,
  permission: 'view'
})

// 模拟用户列表
const userList = [
  {
    id: 1,
    name: '张三',
    email: 'zhangsan@example.com',
    avatar: 'https://randomuser.me/api/portraits/men/1.jpg'
  },
  {
    id: 2,
    name: '李四',
    email: 'lisi@example.com',
    avatar: 'https://randomuser.me/api/portraits/women/2.jpg'
  }
]

// 生命周期钩子
onMounted(() => {
  loadDashboard()
  setupRefreshTimer()
})

onBeforeUnmount(() => {
  clearRefreshTimer()
})

// 加载仪表盘数据
const loadDashboard = async () => {
  try {
    // TODO: 调用加载 API
    await new Promise(resolve => setTimeout(resolve, 1000))
  } catch (error) {
    showError('加载失败')
  }
}

// 设置刷新定时器
const setupRefreshTimer = () => {
  clearRefreshTimer()
  if (refreshInterval.value > 0) {
    refreshTimer = window.setInterval(() => {
      refreshAllWidgets()
    }, refreshInterval.value * 1000)
  }
}

// 清除刷新定时器
const clearRefreshTimer = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 监听刷新间隔变化
watch(refreshInterval, () => {
  setupRefreshTimer()
})

// 刷新所有组件
const refreshAllWidgets = () => {
  dashboard.value.widgets.forEach((widget: any) => {
    refreshWidget(widget)
  })
}

// 刷新单个组件
const refreshWidget = async (widget: any) => {
  try {
    // TODO: 调用刷新 API
    await new Promise(resolve => setTimeout(resolve, 1000))
  } catch (error) {
    showError(`刷新 ${widget.title} 失败`)
  }
}

// 处理过滤器变化
const handleFilterChange = () => {
  refreshAllWidgets()
}

// 获取过滤器组件
const getFilterComponent = (type: string) => {
  switch (type) {
    case 'dateRange':
      return 'v-date-picker'
    case 'select':
      return 'v-select'
    default:
      return 'v-text-field'
  }
}

// 获取时间范围文本
const getTimeRangeText = () => {
  if (timeRange.value.start && timeRange.value.end) {
    return `${formatDate(timeRange.value.start)} - ${formatDate(timeRange.value.end)}`
  }
  return '选择时间范围'
}

// 选择时间范围
const selectTimeRange = (preset: any) => {
  // TODO: 根据预设值设置时间范围
  timeRange.value = calculateTimeRange(preset.value)
  refreshAllWidgets()
}

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

// 编辑仪表盘
const handleEdit = () => {
  router.push(`/dashboard/${dashboard.value.id}/edit`)
}

// 分享仪表盘
const handleShare = () => {
  shareLink.value = `${window.location.origin}/dashboard/share/${dashboard.value.id}`
  shareDialog.value = true
}

// 复制仪表盘
const handleCopy = async () => {
  try {
    // TODO: 调用复制 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('复制成功')
    router.push('/dashboard/list')
  } catch (error) {
    showError('复制失败')
  }
}

// 导出数据
const handleExport = async () => {
  try {
    // TODO: 调用导出 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('导出成功')
  } catch (error) {
    showError('导出失败')
  }
}

// 删除仪表盘
const handleDelete = () => {
  deleteDialog.value = true
}

// 确认删除
const confirmDelete = async () => {
  isDeleting.value = true
  try {
    // TODO: 调用删除 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('删除成功')
    router.push('/dashboard/list')
  } catch (error) {
    showError('删除失败')
  } finally {
    isDeleting.value = false
    deleteDialog.value = false
  }
}

// 复制分享链接
const copyShareLink = async () => {
  try {
    await navigator.clipboard.writeText(shareLink.value)
    showSuccess('链接已复制')
  } catch (error) {
    showError('复制失败')
  }
}

// 确认分享
const confirmShare = async () => {
  isSharing.value = true
  try {
    // TODO: 调用分享 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSuccess('分享成功')
    shareDialog.value = false
  } catch (error) {
    showError('分享失败')
  } finally {
    isSharing.value = false
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 计算时间范围
const calculateTimeRange = (preset: string) => {
  const now = new Date()
  const start = new Date()
  const end = new Date()

  switch (preset) {
    case 'today':
      start.setHours(0, 0, 0, 0)
      end.setHours(23, 59, 59, 999)
      break
    case 'yesterday':
      start.setDate(start.getDate() - 1)
      start.setHours(0, 0, 0, 0)
      end.setDate(end.getDate() - 1)
      end.setHours(23, 59, 59, 999)
      break
    case 'last7days':
      start.setDate(start.getDate() - 7)
      start.setHours(0, 0, 0, 0)
      end.setHours(23, 59, 59, 999)
      break
    case 'last30days':
      start.setDate(start.getDate() - 30)
      start.setHours(0, 0, 0, 0)
      end.setHours(23, 59, 59, 999)
      break
    case 'thisMonth':
      start.setDate(1)
      start.setHours(0, 0, 0, 0)
      end.setHours(23, 59, 59, 999)
      break
    case 'lastMonth':
      start.setMonth(start.getMonth() - 1)
      start.setDate(1)
      start.setHours(0, 0, 0, 0)
      end.setDate(0)
      end.setHours(23, 59, 59, 999)
      break
  }

  return {
    start: start.toISOString().slice(0, 16),
    end: end.toISOString().slice(0, 16)
  }
}
</script>

<style scoped>
.dashboard-detail {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.dashboard-content {
  flex: 1;
  overflow: auto;
}

.refresh-select {
  max-width: 200px;
}

:deep(.v-card) {
  border-radius: 8px;
}
</style>