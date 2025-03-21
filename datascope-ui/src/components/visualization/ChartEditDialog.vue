# ChartEditDialog.vue
<template>
  <v-dialog
    v-model="dialogVisible"
    max-width="1200"
    persistent
  >
    <v-card>
      <v-card-title class="text-h5 pa-4">
        {{ props.editingChart ? '编辑图表' : '新建图表' }}
      </v-card-title>

      <!-- 步骤导航 -->
      <v-card-text>
        <v-stepper v-model="currentStep">
          <v-stepper-header>
            <v-stepper-item value="1">
              基本信息
            </v-stepper-item>

            <v-divider></v-divider>

            <v-stepper-item value="2">
              选择数据集
            </v-stepper-item>

            <v-divider></v-divider>

            <v-stepper-item value="3">
              图表配置
            </v-stepper-item>
          </v-stepper-header>

          <v-stepper-window>
            <!-- 步骤1：基本信息 -->
            <v-stepper-window-item value="1">
              <v-container>
                <v-row>
                  <v-col cols="12">
                    <v-text-field
                      v-model="formData.name"
                      label="图表名称"
                      :rules="[v => !!v || '请输入图表名称']"
                      required
                    ></v-text-field>
                  </v-col>

                  <v-col cols="12">
                    <v-select
                      v-model="formData.type"
                      :items="chartTypes"
                      label="图表类型"
                      :rules="[v => !!v || '请选择图表类型']"
                      required
                    ></v-select>
                  </v-col>

                  <v-col cols="12">
                    <v-textarea
                      v-model="formData.description"
                      label="描述"
                      rows="3"
                      placeholder="请输入图表描述（选填）"
                    ></v-textarea>
                  </v-col>
                </v-row>
              </v-container>
            </v-stepper-window-item>

            <!-- 步骤2：选择数据集 -->
            <v-stepper-window-item value="2">
              <v-container>
                <v-row>
                  <v-col cols="12">
                    <v-select
                      v-model="formData.datasetId"
                      :items="datasets"
                      item-title="name"
                      item-value="id"
                      label="选择数据集"
                      :rules="[v => !!v || '请选择数据集']"
                      required
                    ></v-select>
                  </v-col>

                  <!-- 数据预览表格 -->
                  <v-col cols="12">
                    <v-card variant="outlined">
                      <v-card-title class="text-subtitle-1">数据预览</v-card-title>
                      <v-data-table
                        v-if="previewData.length > 0"
                        :headers="previewHeaders"
                        :items="previewData"
                        :loading="previewLoading"
                      ></v-data-table>
                      <v-card-text v-else class="text-center pa-4">
                        请选择数据集查看预览数据
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
              </v-container>
            </v-stepper-window-item>

            <!-- 步骤3：图表配置 -->
            <v-stepper-window-item value="3">
              <v-container>
                <v-row>
                  <!-- 配置面板 -->
                  <v-col cols="12" md="6">
                    <v-card variant="outlined">
                      <v-card-title class="text-subtitle-1">图表配置</v-card-title>
                      <v-card-text>
                        <!-- 这里将根据图表类型显示不同的配置选项 -->
                        <div v-if="formData.type === 'line' || formData.type === 'bar'">
                          <v-select
                            v-model="formData.config.xAxis.field"
                            :items="availableFields"
                            label="X轴字段"
                            required
                          ></v-select>
                          <v-select
                            v-model="formData.config.yAxis.field"
                            :items="availableFields"
                            label="Y轴字段"
                            required
                          ></v-select>
                        </div>

                        <div v-else-if="formData.type === 'pie'">
                          <v-select
                            v-model="formData.config.series[0].field"
                            :items="availableFields"
                            label="类别字段"
                            required
                          ></v-select>
                          <v-select
                            v-model="formData.config.series[0].valueField"
                            :items="availableFields"
                            label="数值字段"
                            required
                          ></v-select>
                        </div>
                      </v-card-text>
                    </v-card>
                  </v-col>

                  <!-- 预览面板 -->
                  <v-col cols="12" md="6">
                    <v-card variant="outlined" height="400">
                      <v-card-title class="text-subtitle-1">预览</v-card-title>
                      <v-card-text>
                        <!-- 这里将根据配置显示图表预览 -->
                        <div class="chart-preview" style="height: 300px">
                          <!-- 图表预览将在这里渲染 -->
                        </div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
              </v-container>
            </v-stepper-window-item>
          </v-stepper-window>
        </v-stepper>
      </v-card-text>

      <!-- 操作按钮 -->
      <v-card-actions class="pa-4">
        <v-btn
          variant="tonal"
          @click="closeDialog"
        >
          取消
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn
          v-if="currentStep > 1"
          variant="tonal"
          @click="currentStep--"
        >
          上一步
        </v-btn>
        <v-btn
          v-if="currentStep < 3"
          color="primary"
          @click="currentStep++"
        >
          下一步
        </v-btn>
        <v-btn
          v-else
          color="primary"
          :loading="saving"
          @click="saveChart"
        >
          保存
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { ChartType, Visualization } from '@/types/visualization'

// 属性定义
const props = defineProps<{
  modelValue: boolean
  editingChart?: Visualization
}>()

// 事件定义
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'save', chart: Visualization): void
}>()

// 状态定义
const dialogVisible = ref(props.modelValue)
const currentStep = ref(1)
const saving = ref(false)

// 可用的图表类型
const chartTypes = [
  { title: '折线图', value: 'line' },
  { title: '柱状图', value: 'bar' },
  { title: '饼图', value: 'pie' },
  { title: '散点图', value: 'scatter' },
  { title: '表格', value: 'table' }
]

// 表单数据
const formData = reactive({
  name: '',
  type: '' as ChartType,
  description: '',
  datasetId: null as number | null,
  config: {
    xAxis: {
      field: '',
      label: ''
    },
    yAxis: {
      field: '',
      label: ''
    },
    series: [{
      field: '',
      valueField: '',
      label: ''
    }]
  }
})

// 数据预览相关
const datasets = ref([])
const previewData = ref([])
const previewHeaders = ref([])
const previewLoading = ref(false)
const availableFields = ref([])

// 监听对话框可见性
watch(() => props.modelValue, (newValue) => {
  dialogVisible.value = newValue
})

watch(dialogVisible, (newValue) => {
  emit('update:modelValue', newValue)
})

// 监听数据集选择
watch(() => formData.datasetId, async (newValue) => {
  if (newValue) {
    await loadPreviewData(newValue)
  }
})

// 加载数据预览
const loadPreviewData = async (datasetId: number) => {
  previewLoading.value = true
  try {
    // TODO: 替换为实际的API调用
    const response = await fetch(`/api/datasets/${datasetId}/preview`)
    const data = await response.json()
    
    previewData.value = data.data
    availableFields.value = data.fields.map((field: string) => ({
      title: field,
      value: field
    }))
    
    if (data.data.length > 0) {
      previewHeaders.value = Object.keys(data.data[0]).map(key => ({
        title: key,
        key,
        align: 'start'
      }))
    }
  } catch (error) {
    console.error('Failed to load preview data:', error)
  } finally {
    previewLoading.value = false
  }
}

// 保存图表
const saveChart = async () => {
  saving.value = true
  try {
    // TODO: 实现保存逻辑
    emit('save', formData as unknown as Visualization)
    closeDialog()
  } catch (error) {
    console.error('Failed to save chart:', error)
  } finally {
    saving.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  dialogVisible.value = false
  currentStep.value = 1
  // 重置表单数据
  Object.assign(formData, {
    name: '',
    type: '',
    description: '',
    datasetId: null,
    config: {
      xAxis: { field: '', label: '' },
      yAxis: { field: '', label: '' },
      series: [{ field: '', valueField: '', label: '' }]
    }
  })
}
</script>

<style scoped>
.chart-preview {
  background-color: var(--v-surface-variant);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>