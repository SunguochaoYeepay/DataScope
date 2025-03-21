# DashboardList.vue
<template>
  <div class="dashboard-list">
    <!-- 顶部操作栏 -->
    <div class="d-flex align-center mb-4">
      <h2 class="text-h5 font-weight-bold mb-0">仪表盘</h2>
      <v-spacer></v-spacer>
      <v-btn
        color="primary"
        prepend-icon="mdi-plus"
        @click="handleCreate"
      >
        创建仪表盘
      </v-btn>
    </div>

    <!-- 搜索和筛选 -->
    <v-card class="mb-4">
      <v-card-text>
        <v-row>
          <v-col cols="12" sm="4">
            <v-text-field
              v-model="searchQuery"
              label="搜索仪表盘"
              prepend-inner-icon="mdi-magnify"
              hide-details
              density="comfortable"
              variant="outlined"
              clearable
            ></v-text-field>
          </v-col>
          <v-col cols="12" sm="4">
            <v-select
              v-model="filterType"
              :items="[
                { title: '全部', value: 'all' },
                { title: '我创建的', value: 'created' },
                { title: '我收藏的', value: 'favorite' },
                { title: '共享给我的', value: 'shared' }
              ]"
              label="筛选类型"
              hide-details
              density="comfortable"
              variant="outlined"
            ></v-select>
          </v-col>
          <v-col cols="12" sm="4">
            <v-select
              v-model="sortBy"
              :items="[
                { title: '最近更新', value: 'updated' },
                { title: '最近创建', value: 'created' },
                { title: '名称', value: 'name' },
                { title: '访问次数', value: 'views' }
              ]"
              label="排序方式"
              hide-details
              density="comfortable"
              variant="outlined"
            ></v-select>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 仪表盘列表 -->
    <v-row>
      <v-col
        v-for="dashboard in filteredDashboards"
        :key="dashboard.id"
        cols="12"
        sm="6"
        md="4"
      >
        <v-card
          :to="'/dashboard/' + dashboard.id"
          class="dashboard-card"
          hover
        >
          <!-- 仪表盘预览图 -->
          <v-img
            :src="dashboard.thumbnail || '/images/dashboard-placeholder.png'"
            height="160"
            cover
            class="bg-grey-lighten-2"
          >
            <template v-slot:placeholder>
              <div class="d-flex align-center justify-center fill-height">
                <v-icon
                  icon="mdi-view-dashboard"
                  size="48"
                  color="grey-lighten-1"
                ></v-icon>
              </div>
            </template>
          </v-img>

          <v-card-text class="pt-4">
            <div class="d-flex align-center mb-2">
              <h3 class="text-h6 text-truncate">{{ dashboard.name }}</h3>
              <v-spacer></v-spacer>
              <v-btn
                icon="mdi-star"
                :color="dashboard.favorite ? 'warning' : undefined"
                variant="text"
                size="small"
                @click.stop="toggleFavorite(dashboard)"
              ></v-btn>
            </div>

            <p class="text-body-2 text-grey mb-2 text-truncate">
              {{ dashboard.description || '暂无描述' }}
            </p>

            <div class="d-flex align-center text-caption text-grey">
              <v-icon icon="mdi-clock-outline" size="small" class="mr-1"></v-icon>
              {{ formatDate(dashboard.updatedAt) }}
              <v-icon icon="mdi-eye" size="small" class="ml-3 mr-1"></v-icon>
              {{ dashboard.views }}
              <v-spacer></v-spacer>
              <v-avatar size="24" class="mr-1">
                <v-img :src="dashboard.creator.avatar"></v-img>
              </v-avatar>
              {{ dashboard.creator.name }}
            </div>
          </v-card-text>

          <!-- 快捷操作菜单 -->
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              icon="mdi-pencil"
              variant="text"
              size="small"
              @click.stop="handleEdit(dashboard)"
            ></v-btn>
            <v-btn
              icon="mdi-share-variant"
              variant="text"
              size="small"
              @click.stop="handleShare(dashboard)"
            ></v-btn>
            <v-btn
              icon="mdi-delete"
              variant="text"
              size="small"
              color="error"
              @click.stop="handleDelete(dashboard)"
            ></v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- 空状态展示 -->
      <v-col v-if="filteredDashboards.length === 0" cols="12">
        <v-card class="pa-12 text-center">
          <v-icon
            icon="mdi-view-dashboard-outline"
            size="64"
            color="grey-lighten-1"
            class="mb-4"
          ></v-icon>
          <h3 class="text-h6 mb-2">暂无仪表盘</h3>
          <p class="text-body-2 text-grey mb-4">
            {{ getEmptyMessage() }}
          </p>
          <v-btn
            color="primary"
            prepend-icon="mdi-plus"
            @click="handleCreate"
          >
            创建仪表盘
          </v-btn>
        </v-card>
      </v-col>
    </v-row>

    <!-- 删除确认对话框 -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h6">删除仪表盘</v-card-title>
        <v-card-text>
          确定要删除仪表盘"{{ selectedDashboard?.name }}"吗？此操作无法撤销。
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSnackbar } from '@/composables/useSnackbar'

// 路由
const router = useRouter()
const { showSuccess, showError } = useSnackbar()

// 状态定义
const searchQuery = ref('')
const filterType = ref('all')
const sortBy = ref('updated')
const deleteDialog = ref(false)
const shareDialog = ref(false)
const shareTab = ref('link')
const isDeleting = ref(false)
const isSharing = ref(false)
const selectedDashboard = ref<any>(null)
const shareLink = ref('')
const selectedUsers = ref([])
const shareConfig = ref({
  requirePassword: false,
  password: '',
  enableExpiry: false,
  expiryDays: 7,
  permission: 'view'
})

// 模拟数据
const dashboards = ref([
  {
    id: 1,
    name: '销售数据分析',
    description: '实时监控销售数据，包含销售趋势、区域分布等多个维度分析',
    thumbnail: '',
    favorite: true,
    views: 1234,
    updatedAt: '2024-01-20T10:00:00',
    creator: {
      id: 1,
      name: '张三',
      avatar: 'https://randomuser.me/api/portraits/men/1.jpg'
    }
  },
  {
    id: 2,
    name: '用户行为分析',
    description: '用户访问路径、停留时间、转化率等关键指标分析',
    thumbnail: '',
    favorite: false,
    views: 856,
    updatedAt: '2024-01-19T15:30:00',
    creator: {
      id: 2,
      name: '李四',
      avatar: 'https://randomuser.me/api/portraits/women/2.jpg'
    }
  }
])

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

// 计算属性：过滤后的仪表盘列表
const filteredDashboards = computed(() => {
  let result = [...dashboards.value]

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(d => 
      d.name.toLowerCase().includes(query) || 
      d.description.toLowerCase().includes(query)
    )
  }

  // 类型过滤
  switch (filterType.value) {
    case 'created':
      result = result.filter(d => d.creator.id === 1) // 假设当前用户 ID 为 1
      break
    case 'favorite':
      result = result.filter(d => d.favorite)
      break
    case 'shared':
      result = result.filter(d => d.creator.id !== 1) // 假设当前用户 ID 为 1
      break
  }

  // 排序
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'updated':
        return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
      case 'created':
        return b.id - a.id
      case 'name':
        return a.name.localeCompare(b.name)
      case 'views':
        return b.views - a.views
      default:
        return 0
    }
  })

  return result
})

// 获取空状态提示信息
const getEmptyMessage = () => {
  switch (filterType.value) {
    case 'created':
      return '您还没有创建任何仪表盘'
    case 'favorite':
      return '您还没有收藏任何仪表盘'
    case 'shared':
      return '暂无共享给您的仪表盘'
    default:
      return searchQuery.value 
        ? '没有找到匹配的仪表盘' 
        : '系统中还没有任何仪表盘'
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 处理创建仪表盘
const handleCreate = () => {
  router.push('/dashboard/create')
}

// 处理编辑仪表盘
const handleEdit = (dashboard: any) => {
  router.push(`/dashboard/${dashboard.id}/edit`)
}

// 处理删除仪表盘
const handleDelete = (dashboard: any) => {
  selectedDashboard.value = dashboard
  deleteDialog.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (!selectedDashboard.value) return
  
  isDeleting.value = true
  try {
    // TODO: 调用删除 API
    await new Promise(resolve => setTimeout(resolve, 1000))
    const index = dashboards.value.findIndex(d => d.id === selectedDashboard.value.id)
    if (index > -1) {
      dashboards.value.splice(index, 1)
    }
    showSuccess('仪表盘已删除')
    deleteDialog.value = false
  } catch (error) {
    showError('删除失败')
  } finally {
    isDeleting.value = false
  }
}

// 处理分享仪表盘
const handleShare = (dashboard: any) => {
  selectedDashboard.value = dashboard
  shareLink.value = `${window.location.origin}/dashboard/share/${dashboard.id}`
  shareDialog.value = true
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
  if (!selectedDashboard.value) return

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

// 切换收藏状态
const toggleFavorite = async (dashboard: any) => {
  try {
    // TODO: 调用收藏 API
    await new Promise(resolve => setTimeout(resolve, 500))
    dashboard.favorite = !dashboard.favorite
    showSuccess(dashboard.favorite ? '已添加到收藏' : '已取消收藏')
  } catch (error) {
    showError('操作失败')
  }
}
</script>

<style scoped>
.dashboard-list {
  padding: 24px;
}

.dashboard-card {
  transition: transform 0.2s;
}

.dashboard-card:hover {
  transform: translateY(-4px);
}
</style>