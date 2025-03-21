# ChartConfig.vue
<template>
  <div class="chart-config">
    <v-expansion-panels v-model="activePanel" multiple>
      <!-- 基础配置面板 -->
      <v-expansion-panel value="basic">
        <v-expansion-panel-title>
          <v-icon icon="mdi-cog" class="mr-2"></v-icon>
          基础配置
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row>
            <!-- 图表标题 -->
            <v-col cols="12">
              <v-text-field
                v-model="config.title"
                label="图表标题"
                placeholder="请输入图表标题"
                hide-details="auto"
                class="mb-3"
              ></v-text-field>
            </v-col>

            <!-- 图表主题色 -->
            <v-col cols="12" sm="6">
              <v-select
                v-model="config.theme"
                :items="themeOptions"
                label="主题色"
                hide-details="auto"
              ></v-select>
            </v-col>

            <!-- 图例显示 -->
            <v-col cols="12" sm="6">
              <v-switch
                v-model="config.legend"
                label="显示图例"
                hide-details="auto"
              ></v-switch>
            </v-col>
          </v-row>
        </v-expansion-panel-text>
      </v-expansion-panel>

      <!-- 数据映射面板 -->
      <v-expansion-panel value="mapping">
        <v-expansion-panel-title>
          <v-icon icon="mdi-chart-scatter-plot" class="mr-2"></v-icon>
          数据映射
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <!-- 折线图/柱状图配置 -->
          <template v-if="['line', 'bar'].includes(props.chartType)">
            <v-row>
              <!-- X轴配置 -->
              <v-col cols="12">
                <v-select
                  v-model="config.xAxis.field"
                  :items="availableFields"
                  label="X轴字段"
                  hide-details="auto"
                  class="mb-3"
                ></v-select>
                <v-text-field
                  v-model="config.xAxis.label"
                  label="X轴标签"
                  placeholder="请输入X轴标签"
                  hide-details="auto"
                  class="mb-3"
                ></v-text-field>
              </v-col>

              <!-- Y轴配置 -->
              <v-col cols="12">
                <v-select
                  v-model="config.yAxis.field"
                  :items="availableFields"
                  label="Y轴字段"
                  hide-details="auto"
                  class="mb-3"
                ></v-select>
                <v-text-field
                  v-model="config.yAxis.label"
                  label="Y轴标签"
                  placeholder="请输入Y轴标签"
                  hide-details="auto"
                ></v-text-field>
              </v-col>
            </v-row>
          </template>

          <!-- 饼图配置 -->
          <template v-else-if="props.chartType === 'pie'">
            <v-row>
              <v-col cols="12">
                <v-select
                  v-model="config.series[0].field"
                  :items="availableFields"
                  label="类别字段"
                  hide-details="auto"
                  class="mb-3"
                ></v-select>
              </v-col>
              <v-col cols="12">
                <v-select
                  v-model="config.series[0].valueField"
                  :items="availableFields"
                  label="数值字段"
                  hide-details="auto"
                ></v-select>
              </v-col>
            </v-row>
          </template>

          <!-- 散点图配置 -->
          <template v-else-if="props.chartType === 'scatter'">
            <v-row>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="config.xAxis.field"
                  :items="availableFields"
                  label="X轴字段"
                  hide-details="auto"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-select
                  v-model="config.yAxis.field"
                  :items="availableFields"
                  label="Y轴字段"
                  hide-details="auto"
                ></v-select>
              </v-col>
              <v-col cols="12">
                <v-select
                  v-model="config.series[0].field"
                  :items="availableFields"
                  label="分组字段（可选）"
                  hide-details="auto"
                  clearable
                ></v-select>
              </v-col>
            </v-row>
          </template>
        </v-expansion-panel-text>
      </v-expansion-panel>

      <!-- 样式配置面板 -->
      <v-expansion-panel value="style">
        <v-expansion-panel-title>
          <v-icon icon="mdi-palette" class="mr-2"></v-icon>
          样式配置
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row>
            <!-- 配色方案 -->
            <v-col cols="12">
              <v-select
                v-model="config.colorScheme"
                :items="colorSchemes"
                label="配色方案"
                hide-details="auto"
                class="mb-3"
              >
                <template v-slot:selection="{ item }">
                  <div class="d-flex align-center">
                    <div 
                      class="color-preview mr-2"
                      :style="{ background: getColorPreview(item.raw.colors) }"
                    ></div>
                    {{ item.title }}
                  </div>
                </template>
                <template v-slot:item="{ props, item }">
                  <v-list-item v-bind="props">
                    <template v-slot:prepend>
                      <div 
                        class="color-preview"
                        :style="{ background: getColorPreview(item.raw.colors) }"
                      ></div>
                    </template>
                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                  </v-list-item>
                </template>
              </v-select>
            </v-col>

            <!-- 图表特定样式 -->
            <template v-if="['line', 'bar'].includes(props.chartType)">
              <v-col cols="12" sm="6">
                <v-switch
                  v-model="config.showLabel"
                  label="显示数据标签"
                  hide-details="auto"
                ></v-switch>
              </v-col>
              <v-col cols="12" sm="6">
                <v-switch
                  v-model="config.smooth"
                  label="平滑曲线"
                  v-if="props.chartType === 'line'"
                  hide-details="auto"
                ></v-switch>
              </v-col>
            </template>

            <template v-else-if="props.chartType === 'pie'">
              <v-col cols="12" sm="6">
                <v-select
                  v-model="config.pieType"
                  :items="[
                    { title: '普通饼图', value: 'pie' },
                    { title: '环形图', value: 'doughnut' },
                    { title: '玫瑰图', value: 'rose' }
                  ]"
                  label="饼图类型"
                  hide-details="auto"
                ></v-select>
              </v-col>
              <v-col cols="12" sm="6">
                <v-switch
                  v-model="config.showPercentage"
                  label="显示百分比"
                  hide-details="auto"
                ></v-switch>
              </v-col>
            </template>
          </v-row>
        </v-expansion-panel-text>
      </v-expansion-panel>

      <!-- 交互配置面板 -->
      <v-expansion-panel value="interaction">
        <v-expansion-panel-title>
          <v-icon icon="mdi-gesture-tap" class="mr-2"></v-icon>
          交互配置
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row>
            <v-col cols="12" sm="6">
              <v-switch
                v-model="config.tooltip"
                label="显示提示框"
                hide-details="auto"
              ></v-switch>
            </v-col>
            <v-col cols="12" sm="6">
              <v-switch
                v-model="config.zoom"
                label="允许缩放"
                hide-details="auto"
              ></v-switch>
            </v-col>
            <v-col cols="12">
              <v-select
                v-model="config.animation"
                :items="[
                  { title: '无动画', value: 'none' },
                  { title: '渐显', value: 'fade' },
                  { title: '缩放', value: 'scale' },
                  { title: '滑动', value: 'slide' }
                ]"
                label="动画效果"
                hide-details="auto"
              ></v-select>
            </v-col>
          </v-row>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { ChartType } from '@/types/visualization'

// 属性定义
const props = defineProps<{
  chartType: ChartType
  availableFields: string[]
  modelValue: Record<string, any>
}>()

// 事件定义
const emit = defineEmits<{
  (e: 'update:modelValue', value: Record<string, any>): void
}>()

// 状态定义
const activePanel = ref(['basic'])
const config = reactive({ ...props.modelValue })

// 主题选项
const themeOptions = [
  { title: '默认主题', value: 'default' },
  { title: '浅色主题', value: 'light' },
  { title: '深色主题', value: 'dark' },
  { title: '商务主题', value: 'business' }
]

// 配色方案
const colorSchemes = [
  {
    title: '经典',
    value: 'classic',
    colors: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de']
  },
  {
    title: '清新',
    value: 'fresh',
    colors: ['#91cc75', '#5470c6', '#fac858', '#ee6666', '#73c0de']
  },
  {
    title: '暖色',
    value: 'warm',
    colors: ['#ee6666', '#fac858', '#91cc75', '#5470c6', '#73c0de']
  },
  {
    title: '冷色',
    value: 'cold',
    colors: ['#73c0de', '#5470c6', '#91cc75', '#fac858', '#ee6666']
  }
]

// 监听配置变化
watch(config, (newValue) => {
  emit('update:modelValue', { ...newValue })
}, { deep: true })

// 监听属性变化
watch(() => props.modelValue, (newValue) => {
  Object.assign(config, newValue)
}, { deep: true })

// 获取配色方案预览
const getColorPreview = (colors: string[]) => {
  return `linear-gradient(to right, ${colors.join(', ')})`
}
</script>

<style scoped>
.chart-config {
  width: 100%;
}

.color-preview {
  width: 50px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid rgba(0, 0, 0, 0.12);
}

:deep(.v-expansion-panel-title) {
  font-size: 0.875rem;
  font-weight: 500;
}
</style>