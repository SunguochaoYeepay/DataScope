# ChartTypeSelector.vue
<template>
  <div class="chart-type-selector">
    <v-row>
      <v-col 
        v-for="type in chartTypes" 
        :key="type.value"
        cols="6"
        sm="4"
        md="3"
      >
        <v-card
          :class="[
            'chart-type-card',
            { 'selected': modelValue === type.value }
          ]"
          @click="handleSelect(type.value)"
          variant="outlined"
          :color="modelValue === type.value ? 'primary' : undefined"
        >
          <v-card-text class="pa-4">
            <div class="d-flex flex-column align-center">
              <v-icon
                :icon="type.icon"
                size="32"
                :color="modelValue === type.value ? 'primary' : undefined"
                class="mb-2"
              ></v-icon>
              <span class="text-subtitle-2">{{ type.label }}</span>
              <span class="text-caption text-grey mt-1">{{ type.description }}</span>
            </div>
          </v-card-text>

          <v-overlay
            :model-value="modelValue === type.value"
            contained
            class="align-center justify-center"
            scrim="primary"
          >
            <v-icon
              icon="mdi-check-circle"
              color="white"
              size="24"
            ></v-icon>
          </v-overlay>
        </v-card>
      </v-col>
    </v-row>

    <!-- 图表类型说明对话框 -->
    <v-dialog v-model="showHelp" max-width="600">
      <template v-slot:activator="{ props }">
        <v-btn
          icon="mdi-help-circle"
          variant="text"
          size="small"
          class="help-button"
          v-bind="props"
        ></v-btn>
      </template>

      <v-card>
        <v-card-title class="text-h6 pa-4">
          图表类型说明
          <v-btn
            icon="mdi-close"
            variant="text"
            size="small"
            @click="showHelp = false"
            class="float-right"
          ></v-btn>
        </v-card-title>
        <v-card-text class="pa-4">
          <v-list>
            <v-list-item
              v-for="type in chartTypes"
              :key="type.value"
              :title="type.label"
              :subtitle="type.description"
            >
              <template v-slot:prepend>
                <v-icon :icon="type.icon" class="mr-3"></v-icon>
              </template>
              <template v-slot:append>
                <v-chip
                  v-for="(tag, index) in type.tags"
                  :key="index"
                  size="small"
                  class="ml-2"
                  color="primary"
                  variant="outlined"
                >
                  {{ tag }}
                </v-chip>
              </template>
            </v-list-item>
          </v-list>
        </v-card-text>
      </v-card>
    </v-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { ChartType } from '@/types/visualization'

// 属性定义
const props = defineProps<{
  modelValue: ChartType
}>()

// 事件定义
const emit = defineEmits<{
  (e: 'update:modelValue', value: ChartType): void
}>()

// 状态定义
const showHelp = ref(false)

// 图表类型定义
const chartTypes = [
  {
    value: 'line',
    label: '折线图',
    icon: 'mdi-chart-line',
    description: '展示数据随时间变化的趋势',
    tags: ['趋势', '时间序列', '连续数据']
  },
  {
    value: 'bar',
    label: '柱状图',
    icon: 'mdi-chart-bar',
    description: '比较不同类别的数值大小',
    tags: ['对比', '分类数据', '排名']
  },
  {
    value: 'pie',
    label: '饼图',
    icon: 'mdi-chart-pie',
    description: '展示整体中各部分的占比',
    tags: ['占比', '构成', '百分比']
  },
  {
    value: 'scatter',
    label: '散点图',
    icon: 'mdi-chart-scatter-plot',
    description: '展示两个变量之间的关系',
    tags: ['相关性', '分布', '聚类']
  },
  {
    value: 'area',
    label: '面积图',
    icon: 'mdi-chart-areaspline',
    description: '强调数值随时间的累积变化',
    tags: ['趋势', '累积', '占比变化']
  },
  {
    value: 'radar',
    label: '雷达图',
    icon: 'mdi-chart-arc',
    description: '多维数据的综合对比',
    tags: ['多维对比', '能力评估', '特征分析']
  },
  {
    value: 'funnel',
    label: '漏斗图',
    icon: 'mdi-chart-timeline-variant',
    description: '展示环节转化率',
    tags: ['转化率', '流程', '环节分析']
  },
  {
    value: 'heatmap',
    label: '热力图',
    icon: 'mdi-chart-box',
    description: '展示数据密度分布',
    tags: ['密度', '分布', '热点分析']
  }
] as const

// 选择图表类型
const handleSelect = (type: ChartType) => {
  emit('update:modelValue', type)
}
</script>

<style scoped>
.chart-type-selector {
  position: relative;
  padding: 16px;
}

.chart-type-card {
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.chart-type-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart-type-card.selected {
  border-color: var(--v-primary-base);
}

.help-button {
  position: absolute;
  top: 8px;
  right: 8px;
}

:deep(.v-overlay__content) {
  background: rgba(var(--v-theme-primary), 0.1);
}
</style>