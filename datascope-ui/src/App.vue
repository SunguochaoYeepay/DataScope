<template>
  <a-config-provider :theme="{ 
    token: { 
      colorPrimary: '#1890ff',
      borderRadius: 2,
    },
    components: {
      Layout: {
        bodyBg: '#f0f2f5',
        headerBg: '#fff',
        siderBg: '#001529',
      },
      Menu: {
        darkItemBg: '#001529',
      },
    },
  }">
    <router-view v-slot="{ Component }">
      <component :is="Component" />
    </router-view>
  </a-config-provider>
</template>

<script setup lang="ts">
// 空的 setup 块，因为我们现在不需要任何逻辑
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  font-size: 14px;
  line-height: 1.5;
  background: #f0f2f5;
}

#app {
  width: 100%;
  min-height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}
</style>