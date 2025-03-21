# Home.vue
<template>
  <v-container fluid>
    <v-row>
      <v-col cols="12">
        <v-card class="mb-4">
          <v-card-item>
            <v-card-title class="text-h4">
              欢迎使用 DataScope
            </v-card-title>
            <v-card-subtitle class="mt-2">
              强大的数据管理与分析平台
            </v-card-subtitle>
          </v-card-item>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <!-- 快速入口卡片 -->
      <v-col cols="12" md="4">
        <v-card
          to="/datasource"
          class="h-100"
        >
          <v-card-item>
            <template v-slot:prepend>
              <v-icon
                color="primary"
                size="large"
                icon="mdi-database"
              ></v-icon>
            </template>
            <v-card-title>数据源管理</v-card-title>
            <v-card-subtitle>
              管理和配置数据库连接
            </v-card-subtitle>
          </v-card-item>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card
          to="/dataset"
          class="h-100"
        >
          <v-card-item>
            <template v-slot:prepend>
              <v-icon
                color="info"
                size="large"
                icon="mdi-table"
              ></v-icon>
            </template>
            <v-card-title>数据集管理</v-card-title>
            <v-card-subtitle>
              创建和管理数据集
            </v-card-subtitle>
          </v-card-item>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card
          to="/visualization"
          class="h-100"
        >
          <v-card-item>
            <template v-slot:prepend>
              <v-icon
                color="success"
                size="large"
                icon="mdi-chart-box"
              ></v-icon>
            </template>
            <v-card-title>可视化管理</v-card-title>
            <v-card-subtitle>
              创建数据可视化图表
            </v-card-subtitle>
          </v-card-item>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mt-4">
      <!-- 系统状态卡片 -->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-subtitle-1">
            系统状态
          </v-card-title>
          <v-card-text>
            <v-list lines="one">
              <v-list-item>
                <template v-slot:prepend>
                  <v-icon
                    :color="systemStatus.server ? 'success' : 'error'"
                    icon="mdi-server"
                  ></v-icon>
                </template>
                <v-list-item-title>服务器状态</v-list-item-title>
                <template v-slot:append>
                  <v-chip
                    :color="systemStatus.server ? 'success' : 'error'"
                    size="small"
                  >
                    {{ systemStatus.server ? '正常' : '异常' }}
                  </v-chip>
                </template>
              </v-list-item>

              <v-list-item>
                <template v-slot:prepend>
                  <v-icon
                    :color="systemStatus.database ? 'success' : 'error'"
                    icon="mdi-database"
                  ></v-icon>
                </template>
                <v-list-item-title>数据库状态</v-list-item-title>
                <template v-slot:append>
                  <v-chip
                    :color="systemStatus.database ? 'success' : 'error'"
                    size="small"
                  >
                    {{ systemStatus.database ? '正常' : '异常' }}
                  </v-chip>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- 快速统计卡片 -->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-subtitle-1">
            数据统计
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="6">
                <div class="text-h4 font-weight-bold text-primary">
                  {{ statistics.datasources }}
                </div>
                <div class="text-subtitle-2 text-medium-emphasis">
                  数据源数量
                </div>
              </v-col>
              <v-col cols="6">
                <div class="text-h4 font-weight-bold text-info">
                  {{ statistics.datasets }}
                </div>
                <div class="text-subtitle-2 text-medium-emphasis">
                  数据集数量
                </div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 系统状态数据
const systemStatus = ref({
  server: true,
  database: true
})

// 统计数据
const statistics = ref({
  datasources: 5,
  datasets: 12
})
</script>

<style scoped>
.h-100 {
  height: 100%;
}
</style>