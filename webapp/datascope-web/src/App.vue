<template>
  <a-config-provider>
    <a-layout class="layout">
      <a-layout-header class="header">
        <div class="logo">DataScope</div>
        <a-menu
          v-model:selectedKeys="selectedKeys"
          theme="dark"
          mode="horizontal"
          :style="{ lineHeight: '64px' }"
        >
          <a-menu-item key="query-history">
            <router-link to="/query-history">查询历史</router-link>
          </a-menu-item>
          <a-menu-item key="query-stats">
            <router-link to="/query-stats">查询统计</router-link>
          </a-menu-item>
          <a-menu-item key="slow-queries">
            <router-link to="/slow-queries">慢查询分析</router-link>
          </a-menu-item>
        </a-menu>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
      <a-layout-footer class="footer">
        DataScope ©2024 Created by Your Company
      </a-layout-footer>
    </a-layout>
  </a-config-provider>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const selectedKeys = ref<string[]>(['query-history']);

watch(
  () => route.path,
  (path) => {
    const key = path.split('/')[1];
    selectedKeys.value = [key];
  },
  { immediate: true }
);
</script>

<style>
#app {
  height: 100vh;
}

.layout {
  min-height: 100vh;
}

.header {
  display: flex;
  align-items: center;
}

.logo {
  color: white;
  font-size: 20px;
  font-weight: bold;
  margin-right: 48px;
}

.content {
  background: #f0f2f5;
  min-height: calc(100vh - 64px - 70px);
}

.footer {
  text-align: center;
  background: #fff;
}
</style>