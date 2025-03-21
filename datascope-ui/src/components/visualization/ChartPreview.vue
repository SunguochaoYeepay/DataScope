# ChartPreview.vue
<template>
  <div class="chart-preview" ref="chartContainer">
    <!-- 加载状态 -->
    <v-progress-circular
      v-if="loading"
      indeterminate
      color="primary"
    ></v-progress-circular>

    <!-- 空状态 -->
    <div v-else-if="!hasData" class="empty-state">
      <v-icon icon="mdi-chart-box-outline" size="48" color="medium-emphasis"></v-icon>
      <div class="text-medium-emphasis mt-2">{{ emptyText }}</div>
    </div>

    <!-- 图表容器 -->
    <div v-else ref="chartRef" class="chart-container"></div>

    <!-- 错误提示 -->
    <v-snackbar
      v-model="errorSnackbar.show"
      color="error"
      :timeout="3000"
    >
      {{ errorSnackbar.text }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="errorSnackbar.show = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import type { ChartType } from '@/types/visualization'

// 定义组件属性
const props = defineProps<{
  type: ChartType
  data: Record<string, any>[]
  config: {
    xAxis?: {
      field: string
      label?: string
    }
    yAxis?: {
      field: string
      label?: string
    }
    series?: {
      field: string
      label?: string
      type?: string
      valueField?: string
    }[]
    color?: string[]
    legend?: boolean
    grid?: {
      show: boolean
    }
  }
}>()

// 状态定义
const loading = ref(false)
const chartRef = ref<HTMLElement | null>(null)
const chartInstance = ref<echarts.ECharts | null>(null)
const errorSnackbar = ref({
  show: false,
  text: ''
})

// 计算属性
const hasData = computed(() => props.data && props.data.length > 0)
const emptyText = computed(() => {
  if (!props.data) return '暂无数据'
  if (props.data.length === 0) return '数据为空'
  return '请配置图表'
})

// 监听数据变化
watch(
  () => [props.data, props.config, props.type],
  () => {
    updateChart()
  },
  { deep: true }
)

// 生成图表配置
const generateChartOption = (): EChartsOption => {
  const { type, data, config } = props

  try {
    switch (type) {
      case 'line':
      case 'bar':
        return {
          grid: {
            show: config.grid?.show ?? false,
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            }
          },
          legend: config.legend !== false ? {} : undefined,
          xAxis: {
            type: 'category',
            data: data.map(item => item[config.xAxis?.field || '']),
            name: config.xAxis?.label,
            axisLabel: {
              rotate: data.length > 10 ? 45 : 0
            }
          },
          yAxis: {
            type: 'value',
            name: config.yAxis?.label
          },
          series: [{
            type,
            data: data.map(item => item[config.yAxis?.field || '']),
            smooth: type === 'line',
            label: {
              show: false
            }
          }],
          color: config.color
        }

      case 'pie':
        return {
          tooltip: {
            trigger: 'item',
            formatter: '{b}: {c} ({d}%)'
          },
          legend: config.legend !== false ? {
            orient: 'vertical',
            left: 'left'
          } : undefined,
          series: [{
            type: 'pie',
            radius: '50%',
            data: data.map(item => ({
              name: item[config.series?.[0].field || ''],
              value: item[config.series?.[0].valueField || '']
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }],
          color: config.color
        }

      case 'scatter':
        return {
          grid: {
            show: config.grid?.show ?? false,
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          tooltip: {
            trigger: 'item',
            formatter: (params: any) => {
              const { data } = params
              return `${config.xAxis?.label || 'X'}: ${data[0]}<br/>${config.yAxis?.label || 'Y'}: ${data[1]}`
            }
          },
          xAxis: {
            type: 'value',
            name: config.xAxis?.label,
            scale: true
          },
          yAxis: {
            type: 'value',
            name: config.yAxis?.label,
            scale: true
          },
          series: [{
            type: 'scatter',
            data: data.map(item => [
              item[config.xAxis?.field || ''],
              item[config.yAxis?.field || '']
            ]),
            symbolSize: 10
          }],
          color: config.color
        }

      default:
        throw new Error(`Unsupported chart type: ${type}`)
    }
  } catch (error) {
    console.error('Failed to generate chart option:', error)
    showError('生成图表配置失败')
    return {}
  }
}

// 更新图表
const updateChart = async () => {
  if (!chartRef.value || !hasData.value) return

  loading.value = true
  try {
    if (!chartInstance.value) {
      chartInstance.value = echarts.init(chartRef.value)
    }

    const option = generateChartOption()
    chartInstance.value.setOption(option, true)
  } catch (error) {
    console.error('Failed to update chart:', error)
    showError('更新图表失败')
  } finally {
    loading.value = false
  }
}

// 显示错误提示
const showError = (text: string) => {
  errorSnackbar.value = {
    show: true,
    text
  }
}

// 监听窗口大小变化
const handleResize = () => {
  chartInstance.value?.resize()
}

// 生命周期钩子
onMounted(() => {
  window.addEventListener('resize', handleResize)
  updateChart()
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance.value?.dispose()
})
</script>

<style scoped>
.chart-preview {
  width: 100%;
  height: 100%;
  min-height: 300px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-container {
  width: 100%;
  height: 100%;
}

.empty-state {
  text-align: center;
  padding: 20px;
}
</style>