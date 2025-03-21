# VisualizationList.vue
<template>
  <v-container fluid>
    <!-- 页面标题区域 -->
    <v-row class="mb-6">
      <v-col>
        <div class="text-h4 font-weight-bold">可视化管理</div>
        <div class="text-subtitle-1 text-medium-emphasis">创建和管理数据可视化图表</div>
      </v-col>
    </v-row>

    <!-- 主要内容区域 -->
    <v-row>
      <!-- 图表卡片网格 -->
      <v-col
        v-for="chart in visualizations"
        :key="chart.id"
        cols="12"
        sm="6"
        md="4"
        lg="3"
      >
        <v-card class="visualization-card">
          <v-card-item>
            <!-- 图表预览区域 -->
            <div class="chart-preview mb-4" style="height: 200px">
              <!-- 这里将根据图表类型渲染预览 -->
            </div>

            <v-card-title>{{ chart.name }}</v-card-title>
            <v-card-subtitle>{{ getChartTypeLabel(chart.type) }}</v-card-subtitle>
          </v-card-item>

          <v-card-text v-if="chart.description">
            {{ chart.description }}
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
              icon="mdi-pencil"
              variant="text"
              color="primary"
              @click="editChart(chart)"
            >
              <v-tooltip activator="parent" location="top">编辑</v-tooltip>
            </v-btn>
            <v-btn
              icon="mdi-content-copy"
              variant="text"
              color="info"
              @click="duplicateChart(chart)"
            >
              <v-tooltip activator="parent" location="top">复制</v-tooltip>
            </v-btn>
            <v-btn
              icon="mdi-delete"
              variant="text"
              color="error"
              @click="confirmDelete(chart)"
            >
              <v-tooltip activator="parent" location="top">删除</v-tooltip>
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>

      <!-- 新建图表卡片 -->
      <v-col cols="12" sm="6" md="4" lg="3">
        <v-card
          class="visualization-card d-flex align-center justify-center"
          height="100%"
          style="min-height: 300px"
          @click="openCreateDialog"
        >
          <div class="text-center">
            <v-icon
              icon="mdi-plus-circle"
              size="64"
              color="primary"
              class="mb-4"
            ></v-icon>
            <div class="text-h6">新建可视化</div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 删除确认对话框 -->
    <v-dialog v-model="deleteDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h5 pa-4">
          确认删除
        </v-card-title>

        <v-card-text>
          确定要删除可视化图表 "{{ deleteItem?.name }}" 吗？此操作无法撤销。
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
            @click="deleteVisualization"
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
import { ref, reactive } from 'vue'
import type { Visualization, ChartType } from '@/types/visualization'

// 状态定义
const visualizations = ref<Visualization[]>([])
const deleteDialog = ref(false)
const deleteItem = ref<Visualization | null>(null)
const deleting = ref(false)

// 提示消息状态
const snackbar = reactive({
  show: false,
  text: '',
  color: 'success',
})

// 获取图表类型标签
const getChartTypeLabel = (type: ChartType): string => {
  const labels: Record<ChartType, string> = {
    line: '折线图',
    bar: '柱状图',
    pie: '饼图',
    scatter: '散点图',
    table: '表格'
  }
  return labels[type] || type
}

// 打开创建对话框
const openCreateDialog = () => {
  // TODO: 实现创建图表的逻辑
}

// 编辑图表
const editChart = (chart: Visualization) => {
  // TODO: 实现编辑图表的逻辑
}

// 复制图表
const duplicateChart = (chart: Visualization) => {
  // TODO: 实现复制图表的逻辑
}

// 确认删除
const confirmDelete = (chart: Visualization) => {
  deleteItem.value = chart
  deleteDialog.value = true
}

// 删除图表
const deleteVisualization = async () => {
  if (!deleteItem.value) return

  deleting.value = true
  try {
    // TODO: 实现删除图表的API调用
    const response = await fetch(`/api/visualizations/${deleteItem.value.id}`, {
      method: 'DELETE'
    })

    if (!response.ok) throw new Error('删除失败')

    showSuccess('删除成功')
    deleteDialog.value = false
    // 重新加载图表列表
    await fetchVisualizations()
  } catch (error) {
    showError('删除失败')
  } finally {
    deleting.value = false
  }
}

// 获取可视化列表
const fetchVisualizations = async () => {
  try {
    // TODO: 实现获取图表列表的API调用
    const response = await fetch('/api/visualizations')
    const data = await response.json()
    visualizations.value = data
  } catch (error) {
    showError('获取可视化列表失败')
  }
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
</script>

<style scoped>
.visualization-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.visualization-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 25px 0 rgba(0, 0, 0, 0.1);
}

.chart-preview {
  background-color: var(--v-surface-variant);
  border-radius: 8px;
}
</style>