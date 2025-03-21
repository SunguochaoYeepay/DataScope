<template>
  <div v-if="loading" class="loading-container">
    <a-spin size="large">
      <template #indicator>
        <loading-outlined spin />
      </template>
      <div class="loading-text">{{ text || '加载中...' }}</div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { LoadingOutlined } from '@ant-design/icons-vue';
import { defineProps } from 'vue';

defineProps<{
  loading: boolean;
  text?: string;
}>();
</script>

<style scoped>
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.loading-text {
  margin-top: 8px;
  color: #1890ff;
}
</style>