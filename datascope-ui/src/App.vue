# App.vue
<template>
  <v-app>
    <!-- 应用栏 -->
    <v-app-bar>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-app-bar-title>DataScope</v-app-bar-title>
      
      <v-spacer></v-spacer>
      
      <!-- 主题切换 -->
      <v-btn
        icon
        @click="toggleTheme"
      >
        <v-icon>{{ theme.global.current.value.dark ? 'mdi-weather-sunny' : 'mdi-weather-night' }}</v-icon>
      </v-btn>
      
      <!-- 用户菜单 -->
      <v-menu>
        <template v-slot:activator="{ props }">
          <v-btn
            icon
            v-bind="props"
          >
            <v-icon>mdi-account-circle</v-icon>
          </v-btn>
        </template>
        
        <v-list>
          <v-list-item
            value="profile"
            title="个人信息"
            prepend-icon="mdi-account"
          ></v-list-item>
          
          <v-list-item
            value="settings"
            title="设置"
            prepend-icon="mdi-cog"
          ></v-list-item>
          
          <v-divider></v-divider>
          
          <v-list-item
            value="logout"
            title="退出登录"
            prepend-icon="mdi-logout"
            color="error"
          ></v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>

    <!-- 侧边导航 -->
    <v-navigation-drawer
      v-model="drawer"
      elevation="1"
    >
      <v-list>
        <v-list-item
          prepend-icon="mdi-view-dashboard"
          title="仪表盘"
          value="dashboard"
          to="/"
        ></v-list-item>

        <v-list-group
          value="data"
        >
          <template v-slot:activator="{ props }">
            <v-list-item
              v-bind="props"
              prepend-icon="mdi-database"
              title="数据管理"
            ></v-list-item>
          </template>

          <v-list-item
            title="数据源管理"
            value="datasource"
            to="/datasource"
            prepend-icon="mdi-database-cog"
          ></v-list-item>

          <v-list-item
            title="数据集管理"
            value="dataset"
            to="/dataset"
            prepend-icon="mdi-table"
          ></v-list-item>
        </v-list-group>

        <v-list-item
          prepend-icon="mdi-chart-box"
          title="可视化"
          value="visualization"
          to="/visualization"
        ></v-list-item>

        <v-list-item
          prepend-icon="mdi-monitor-dashboard"
          title="仪表板"
          value="dashboard"
          to="/dashboard"
        ></v-list-item>

        <v-list-item
          prepend-icon="mdi-cog"
          title="系统设置"
          value="settings"
          to="/settings"
        ></v-list-item>
      </v-list>
    </v-navigation-drawer>

    <!-- 主内容区域 -->
    <v-main>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useTheme } from 'vuetify'

const theme = useTheme()
const drawer = ref(true)

// 切换主题
const toggleTheme = () => {
  theme.global.name.value = theme.global.current.value.dark ? 'light' : 'dark'
}
</script>

<style>
.v-application {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}
</style>